/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/
package pictorial.controller;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import pictorial.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oeiupac.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    
    private OEGraphMol _mol = new OEGraphMol();
    private Settings _settings = new Settings();

    private File _savePath = new File(System.getProperty("user.home"));
    private OEPen _bondPen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.On, 2.0);
    private OEFont _titleFont = new OEFont();
    private DecimalFormat _df = new DecimalFormat("0.0");
    
    @FXML
    private TextField _height, _width, _title, _submatch, _input;
                     
    @FXML
    private ComboBox _colorStyle, _atomStereo, _bondStereo, 
                     _superAtoms, _hydrogens, _aromaticity, _highlightStyle;
    
    @FXML
    private Slider _penSize, _titleSize, _fontSize, _rotation;
    
    @FXML
    private Label _penLabel, _titleSizeLable, _fontLabel, _rotationLabel;
    
    @FXML
    private WebView _webView;
    
    @FXML
    private Button _save, _gencpp, _gencs, _genpy, _genjava;
    
    @FXML
    private ColorPicker _color;
    
    @FXML
    private RadioButton _titleLocTop, _titleLocBottom;
    
    private LinkedHashSet<Node> _widgets = new LinkedHashSet<>();
    private final String _errorStyle = "-fx-background-color: red;";
    private Controller me = this;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        _color.setValue(Color.RED);
        _webView.contextMenuEnabledProperty().set(false);

        updateImage(null);
        _penSize.valueProperty().addListener( (o, oldVal, newVal) -> {
                me.updateImage(null);
                int p = newVal.intValue();
                _penLabel.setText("Pen Size: " + String.valueOf(p));
                _settings.setPenSize(p); } );
        
        _titleSize.valueProperty().addListener((o, oldVal, newVal) -> {
                me.updateImage(null);
                int p = newVal.intValue();
                _titleSizeLable.setText("Title Font Size: " + String.valueOf(p));
                _settings.setFontSize(p);
            });
        
        _fontSize.valueProperty().addListener( (o, oldVal, newVal) -> {
                me.updateImage(null);
                float p = newVal.floatValue();
                _fontLabel.setText("Atom Font Scale: " + _df.format(p));
                _settings.setAtomFloatScale(p);
            });

        _rotation.valueProperty().addListener( (o, oldVal, newVal)-> {
            float p = newVal.floatValue();
            _rotationLabel.setText("Rotation: " + _df.format(p) + "°");
            _settings.setRotation(p);
            me.updateImage(null);
        });

        // maintain a set of widgets to disable in case of error
        _widgets.add(_height);         _widgets.add(_width);
        _widgets.add(_title);          _widgets.add(_submatch);
        _widgets.add(_titleSize);      _widgets.add(_input);
        _widgets.add(_color);          _widgets.add(_colorStyle);
        _widgets.add(_atomStereo);     _widgets.add(_bondStereo);
        _widgets.add(_superAtoms);     _widgets.add(_hydrogens);
        _widgets.add(_aromaticity);    _widgets.add(_genjava);
        _widgets.add(_titleLocBottom); _widgets.add(_penSize);
        _widgets.add(_fontSize);       _widgets.add(_save);
        _widgets.add(_titleLocTop);    _widgets.add(_gencpp);
        _widgets.add(_gencs);          _widgets.add(_genpy);
        _widgets.add(_rotation);       _widgets.add(_highlightStyle);
    }   
    
    // Most of the OpenEye API calls are demonstrated in this method     
    // To create a depiction from a smiles string:
    // 1) Parse the smiles
    // 2) Prepare the molecule
    // 3) Prepare the depiction
    //    - Set the appropriate depict settings
    //    - Create an OEImage to render to
    //    - Create a OEDisplay 
    // 4) Render image
    public void updateImage(File saveFile) {
        try {
            String smiles = _input.getText();
            _settings.setSmiles(smiles);
             if(smiles.length() == 0) { 
                setErrorStyle(_input);
                throw new RuntimeException("Valid SMILES or IUPAC name required.");
            }
            
            // parse the smiles string
            boolean success = oechem.OESmilesToMol(_mol, smiles);
            if (!success) { 
                _mol.Clear();
                success = oeiupac.OEParseIUPACName(_mol, smiles);
                if (!success) {
                    setErrorStyle(_input);
                    return;
                }
            }

            setSuccessStyle(_input);
            
            // prepare the molecule
            _settings.setMolTitle(_title.getText());
            if (!_title.getText().equals("")) { 
                _mol.SetTitle(_title.getText());
            }

            // prepare the depiction
            success = oedepict.OEPrepareDepiction(_mol, true, true);
            if (!success) { 
               throw new RuntimeException("PrepareDepiction failed");
            }

            // rotate the molecule
            if (_settings.getRotation() != 0.0f) {
                double[] angles = new double[3];
                angles[0] = _settings.getRotation();
                oechem.OEEulerRotate(_mol, angles);
            }
            
            int width = getTextFieldIntValue(_width);
            _settings.setImageWidth(width);
            int height = getTextFieldIntValue(_height);
            _settings.setImageHeight(height);

            // create an OEImage to render to
            OEImage img = new OEImage(width, height);

            // set the display options
            OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
            _bondPen.SetLineWidth(_penSize.getValue());
            opts.SetDefaultBondPen(_bondPen);
            _titleFont.SetSize((int)_titleSize.getValue());
            opts.SetAromaticStyle(getAromaticValue());
            opts.SetBondStereoStyle(getBondStereoValue());
            opts.SetAtomStereoStyle(getAtomStereoValue());
            opts.SetSuperAtomStyle(getSuperAtomValue());
            opts.SetHydrogenStyle(getHydrogenValue());
            opts.SetTitleLocation(getTitleLocValue());
            opts.SetTitleFont(_titleFont);
            opts.SetAtomColorStyle(getColorStyleValue());
            opts.SetAtomLabelFontScale(_fontSize.getValue());

            OE2DMolDisplay disp = new OE2DMolDisplay(_mol, opts);
            
            // handle substructure highlighting
            if(_submatch.getText().length() > 0) {
                
                OESubSearch ss = new OESubSearch(_submatch.getText());
                _settings.setSubSearchQuery(_submatch.getText());
                if(!ss.IsValid()) { 
                    setErrorStyle(_submatch);
                    throw new RuntimeException("Invalid substructure match query.");
                } else { 
                    setSuccessStyle(_submatch);
                    _color.setDisable(false);
                    _highlightStyle.setDisable(false);
                }
              
                boolean unique = true;
                Color c = _color.getValue();
                _settings.setRedHighlight((int)(255*c.getRed()));
                _settings.setGreenHighlight((int)(255*c.getGreen()));
                _settings.setBlueHighlight((int)(255*c.getBlue()));

                OEColor oec = new OEColor((int)(255*c.getRed()),
                                          (int)(255*c.getGreen()),
                                          (int)(255*c.getBlue()));
                for (OEMatchBase match : ss.Match(_mol, unique)) {
                    oedepict.OEAddHighlighting(disp, oec, getHighlightStyleValue(), match);
                }
            } else { 
                setSuccessStyle(_submatch);
                _color.setDisable(true);
                _highlightStyle.setDisable(true);
                _settings.setSubSearchQuery("");
            }
            
            // render the molecule
            success = oedepict.OERenderMolecule(img, disp);
            if(!success) { 
                throw new RuntimeException("OERenderMolecule failed");
            }

            byte[] imgBytes = oedepict.OEWriteImageToByteArray("bsvg", img);
            if (imgBytes == null) { 
                throw new RuntimeException("OEWriteImageToByteArray failed");
            }
            String content = "<div style=\"border: 1px solid black;width:" + width + "px;height:" + height + "px;margin:0 auto;position:relative;\" align='center'>" +
                            new String(imgBytes) + "</div>";
            _webView.getEngine().loadContent(content);

            // write to save file
            if(saveFile != null) {
                // JavaFX FileChooser doesn't set the extension if the user didn't pick one.
                String path = saveFile.getAbsolutePath();
                if(!path.endsWith(".svg") && !path.endsWith(".png")) {
                    path += ".svg";
                } 
                success = oedepict.OEWriteImage(path, img);
                if (!success) { 
                    throw new RuntimeException("OEWriteImage failed");
                }
            }

        } catch(RuntimeException e) {
            _webView.getEngine().loadContent("<table align='center'><tr><td>" + 
                                           "<font color='red'>" + e.getMessage() + 
                                           "</font></td></tr></table>");
        } finally { 
             _mol.Clear();
        }
    }
    
    int getTextFieldIntValue(TextField widget) {
        int v = 0;
        try { 
            v = Integer.parseInt(widget.getText());
        } catch(Exception e) { 
            setErrorStyle(widget);
            throw new RuntimeException("Error: Invalid integer value: [" + String.valueOf(v) + "]");
        } 
        if (v <= 0) { 
            setErrorStyle(widget);
            throw new RuntimeException("Error: Invalid integer value: [" + String.valueOf(v) + "]");
        }
        if (v > 4000) { 
            setErrorStyle(widget);
            throw new RuntimeException("Error: value too large: [" + String.valueOf(v) + "]");
        }
        setSuccessStyle(widget);
        return v;
    }
    
    @FXML
    public void handleTyping(KeyEvent evnt) {
        boolean haveTitle = _title.getText().equals("");
        _titleLocBottom.setDisable(haveTitle);
        _titleLocTop.setDisable(haveTitle);
        _titleSize.setDisable(haveTitle);
        
        updateImage(null);
    }
    
    @FXML
    public void update(ActionEvent event) {
        updateImage(null);
    }
    
    @FXML
    public int getTitleLocValue() { 
        if (_title.getText().equals("")) {
            _settings.setTitleLocation(TitleLocation.HIDDEN);
            return OETitleLocation.Hidden;
        }
        
        if (_titleLocTop.isSelected()) {
            _settings.setTitleLocation(TitleLocation.TOP);
            return OETitleLocation.Top;

        }
        _settings.setTitleLocation(TitleLocation.BOTTOM);
        return OETitleLocation.Bottom;
    }
    
    @FXML
    public void saveImage(ActionEvent event) { 
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG (*.svg)", "*.svg"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"));
        fc.titleProperty().set("Save Image");
        fc.setInitialDirectory(_savePath);
        File tmp = fc.showSaveDialog(_webView.getScene().getWindow());
        if (tmp != null) { 
            updateImage(tmp);
            _savePath = tmp.getAbsoluteFile().getParentFile();
        }       
    }
    
    private void setErrorStyle(Node widget) {
        String style = widget.getStyle();
        widget.setStyle(style + _errorStyle);
        for(Node n: _widgets) { 
            if (n!=null && n != widget) { 
                n.disableProperty().set(true);
            }
        }
    }
    
    private void setSuccessStyle(Node widget) {
        boolean noTitle = _title.getText().equals("");
        String style = widget.getStyle();
        style = style.replace(_errorStyle, "");
        widget.setStyle(style); 
            for(Node n: _widgets) { 
            if (n!=null && n != widget) {
                if(noTitle && (n == _titleLocBottom || n == _titleLocTop || n == _titleSize)) { 
                   continue; 
                }
                n.disableProperty().set(false);
            }
        }
    }
 
    private int getAromaticValue() {
        int val = -1;
        String aro = _aromaticity.getValue().toString();
        switch (aro) {
            case "Kekule":
                val =  OEAromaticStyle.Kekule;
                _settings.setAromaticStyle(AromaticStyle.KEKULE);
                break;
            case "Circle":
                val = OEAromaticStyle.Circle;
                _settings.setAromaticStyle(AromaticStyle.CIRCLE);
                break;
            case "Dash":
                val = OEAromaticStyle.Dash;
                _settings.setAromaticStyle(AromaticStyle.DASH);
                break;
            default:
                setErrorStyle(_aromaticity);
                throw new RuntimeException("Invalid Aromatic Style: [" + aro + "]");
        }
        setSuccessStyle(_aromaticity);
        return val;
    }
    
    private int getBondStereoValue() {
        int val = -1;
        String bondStyle = _bondStereo.getValue().toString();
        switch (bondStyle) {
            case "Default":
                val =  OEBondStereoStyle.Default;
                _settings.setBondStereoStyle(BondStereoStyle.DEFAULT);
                break;
            case "CIP":
                val = OEBondStereoStyle.Display.CIPBondStereo;
                _settings.setBondStereoStyle(BondStereoStyle.CIPBONDSTEREO);
                break;
            case "Hidden":
                val = OEBondStereoStyle.Hidden;
                _settings.setBondStereoStyle(BondStereoStyle.HIDDEN);
                break;
            case "All":
                val = OEBondStereoStyle.Display.CIPBondStereo +
                            OEBondStereoStyle.Display.BondStereo;
                _settings.setBondStereoStyle(BondStereoStyle.ALL);
                break;
           default:
                setErrorStyle(_bondStereo);
                throw new RuntimeException("Invalid Bond Stereo style: [" + bondStyle + "]");
        }
        setSuccessStyle(_bondStereo);
        return val;
    }
    
    private int getAtomStereoValue() {
        int val = -1;
        String atomStyle = _atomStereo.getValue().toString();
        switch (atomStyle) {
            case "Default":
                val = OEAtomStereoStyle.Default;
                _settings.setAtomStereoStyle(AtomStereoStyle.DEFAULT);
                break;
            case "CIP":
                val = OEAtomStereoStyle.Display.CIPAtomStereo;
                _settings.setAtomStereoStyle(AtomStereoStyle.CIPATOMSTEREO);
                break;
            case "Hidden":
                val = OEAtomStereoStyle.Hidden;
                _settings.setAtomStereoStyle(AtomStereoStyle.HIDDEN);
                break;
            case "All":
                val = OEAtomStereoStyle.Display.CIPAtomStereo +
                            OEAtomStereoStyle.Display.AtomStereo;
                _settings.setAtomStereoStyle(AtomStereoStyle.ALL);
                break;
            default:
                setErrorStyle(_atomStereo);
                throw new RuntimeException("Invalid Atom Stereo style: [" + atomStyle + "]");
        }
        
        setSuccessStyle(_atomStereo);
        return val;
     }
    
    private int getSuperAtomValue() {
        int val = -1; 
        String sa = _superAtoms.getValue().toString();
        switch (sa) {
            case "None":
                val =  OESuperAtomStyle.Off;
                _settings.setSuperAtomStyle(SuperAtomStyle.OFF);
                break;
            case "Carbon":
                val = OESuperAtomStyle.Carbon;
                _settings.setSuperAtomStyle(SuperAtomStyle.CARBON);
                break;
            case "Oxygen":
                val = OESuperAtomStyle.Oxygen;
                _settings.setSuperAtomStyle(SuperAtomStyle.OXYGEN);
                break;
            case "Nitrogen":
                val = OESuperAtomStyle.Nitrogen;
                _settings.setSuperAtomStyle(SuperAtomStyle.NITROGEN);
                break;
            case "Oxygen & Nitrogen":
                val = OESuperAtomStyle.OxygenAndNitrogen;
                _settings.setSuperAtomStyle(SuperAtomStyle.OXYGEN_AND_NITROGEN);
                break;
            case "Sulphur":
                val = OESuperAtomStyle.Sulfur;
                _settings.setSuperAtomStyle(SuperAtomStyle.SULPHUR);
                break;
            case "Phosphorus":
                val = OESuperAtomStyle.Phosphorus;
                _settings.setSuperAtomStyle(SuperAtomStyle.PHOSPHORUS);
                break;
            case "Halides":
                val = OESuperAtomStyle.Halide;
                _settings.setSuperAtomStyle(SuperAtomStyle.HALIDES);
                break;
            case "Ether":
                val = OESuperAtomStyle.Ether;
                _settings.setSuperAtomStyle(SuperAtomStyle.ETHER);
                break;
            case "All":
                val = OESuperAtomStyle.All;
                _settings.setSuperAtomStyle(SuperAtomStyle.ALL);
                break;
            default:
                setErrorStyle(_superAtoms);
                throw new RuntimeException("Invalid value for Super Atoms: [" + sa + "]");
        }
        
        setSuccessStyle(_superAtoms);
        return val;
    }
    
    private int getHydrogenValue() {
        int val = -1;
        String hydrogenVal = _hydrogens.getValue().toString();
        switch(hydrogenVal) {
            case "Default":
                val = OEHydrogenStyle.Default;
                _settings.setHydrogenStyle(HydrogenStyle.DEFAULT);
                break;
            case "Explicit: All":
                val = OEHydrogenStyle.ExplicitAll;
                _settings.setHydrogenStyle(HydrogenStyle.EXPLICIT_ALL);
                break;
            case "Excplicit: Hetero":                    
                val = OEHydrogenStyle.ExplicitHetero;
                _settings.setHydrogenStyle(HydrogenStyle.EXPLICT_HETERO);
                break;
            case "Explicit: Terminal":
                val = OEHydrogenStyle.ExplicitTerminal;
                _settings.setHydrogenStyle(HydrogenStyle.EXPLICIT_TERMINAL);
                break;
            case "Implicit: All":
                val = OEHydrogenStyle.ImplicitAll;
                _settings.setHydrogenStyle(HydrogenStyle.IMPLICIT_ALL);
                break;
            case "Implicit: Hetero":
                val = OEHydrogenStyle.ImplicitHetero;
                _settings.setHydrogenStyle(HydrogenStyle.IMPLICIT_HETERO);
                break;
            case "Implicit: Terminal":
                val = OEHydrogenStyle.ImplicitTerminal;
                _settings.setHydrogenStyle(HydrogenStyle.IMPLICIT_TERMINAL);
                break;
            case "Hidden":
                val = OEHydrogenStyle.Hidden;
                _settings.setHydrogenStyle(HydrogenStyle.HIDDEN);
                break;
            default:
                setErrorStyle(_hydrogens);
                throw new RuntimeException("Invalid Hydrogen style: [" + hydrogenVal + "]");
        }
        
        setSuccessStyle(_hydrogens);
        return val;
    }
    
    private int getColorStyleValue() { 
        int val = -1;
        String colorStyle = _colorStyle.getValue().toString();
        
        switch(colorStyle) {
            case "White CPK":
                val = OEAtomColorStyle.WhiteCPK;
                _settings.setColorStyle(ColorStyle.WHITE_CPK);
                break;
            case "White Monochrome":
                val = OEAtomColorStyle.WhiteMonochrome;
                _settings.setColorStyle(ColorStyle.WHITE_MONOCHROME);
                break;
            case "Black CPK":                    
                val = OEAtomColorStyle.BlackCPK;
                _settings.setColorStyle(ColorStyle.BLACK_CPK);
                break;
            case "Black Monochrome":
                val = OEAtomColorStyle.BlackMonochrome;
                _settings.setColorStyle(ColorStyle.BLACK_MONOTONE);
                break;
            default:
                setErrorStyle(_colorStyle);
                throw new RuntimeException("Invalid color style: [" + colorStyle + "]");
        }
        setSuccessStyle(_colorStyle);
        return val;
    }

    private int getHighlightStyleValue() {
        int val = -1;
        String style = _highlightStyle.getValue().toString();

        switch(style) {
            case "Color":
                val = OEHighlightStyle.Color;
                _settings.setHiglightStyle(HighlightStyle.COLOR);
                break;
            case "Stick":
                val = OEHighlightStyle.Stick;
                _settings.setHiglightStyle(HighlightStyle.STICK);
                break;
            case "Ball And Stick":
                val = OEHighlightStyle.BallAndStick;
                _settings.setHiglightStyle(HighlightStyle.BALL_AND_STICK);
                break;
            case "Cogwheel":
                val = OEHighlightStyle.Cogwheel;
                _settings.setHiglightStyle(HighlightStyle.COGWHEEL);
                break;
            default:
                setErrorStyle(_colorStyle);
                throw new RuntimeException("Invalid highligh style: [" + style + "]");
        }
        setSuccessStyle(_colorStyle);
        return val;
    }

    private void generateCode(String language, String extension, Settings.LanguageFormat langFormat) { 
        final String filter = String.format("%s (*%s)", language, extension);
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(filter, "*" + extension));

        fc.titleProperty().set("Saving a " + language + " file ");
        fc.setInitialDirectory(_savePath);
        File tmp = fc.showSaveDialog(_webView.getScene().getWindow());
        if (tmp == null) { return; }

        _savePath = tmp.getParentFile(); // remember the directory previously chosen
        
        // remove the extension for code generation purposes
        // java.io.File: how to be awkward
        String tmpName = tmp.getName();
        int index =  tmpName.lastIndexOf('.');
        if (index == -1) { 
            tmp = new File(tmp.getAbsolutePath() + extension);
        } else {
            tmpName = tmpName.substring(0,index);
        }
        _settings.setImageName(tmpName);

        // load the template
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(System.class, "/pictorial");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");
        Template temp = null;
        try {
            temp = cfg.getTemplate("template" + extension);
            Writer fw = new FileWriter(new File(tmp.getParent(), _settings.getImageName()) + extension);
            temp.process(_settings.getHashTable(langFormat), fw);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(TemplateException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void generatePython(ActionEvent event) {
        Settings.LanguageFormat pyFormat = s -> {
            if (s.equals("true")) {
                return "True";
            } else if (s.equals("false")) {
                return "False";
            }
            return s.replace("::", "_");
        };
        generateCode("Python", ".py", pyFormat);
    }

    @FXML
    public void generateCpp(ActionEvent event) {
        Settings.LanguageFormat cppFormat = s -> s;
        generateCode("C++", ".cpp", cppFormat);
    }

    @FXML
    public void generateJava(ActionEvent event) {
        Settings.LanguageFormat javaFormat = s -> s.replace("::", ".");
        generateCode("Java", ".java", javaFormat);
    }

    @FXML
    public void generateCSharp(ActionEvent event) {
        Settings.LanguageFormat csFormat = s -> s.replace("::", ".");
        generateCode("C#", ".cs", csFormat);
    }
}
