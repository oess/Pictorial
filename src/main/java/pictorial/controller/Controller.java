/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/
package pictorial.controller;
import javafx.geometry.Insets;
import javafx.stage.Window;
import org.controlsfx.control.PopOver;
import org.controlsfx.glyphfont.Glyph;
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
import openeye.oeiupac.*;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Settings _settings = new Settings();
    private Image _image = new Image();
    private File _savePath = new File(System.getProperty("user.home"));
    private DecimalFormat _df = new DecimalFormat("0.0");
    private OEGraphMol _mol = new OEGraphMol();

    @FXML
    private TextField _height, _width, _title, _submatch;

    @FXML
    private TextArea _input;

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
    private Button _saveImage, _saveCode;
    
    @FXML
    private ColorPicker _color;
    
    @FXML
    private RadioButton _titleLocTop, _titleLocBottom;

    @FXML
    private Accordion _accordian;

    @FXML
    private CheckBox _flipX, _flipY;

    @FXML
    private Tab _codeGenTab;

    @FXML
    private TextArea _codeArea;

    @FXML
    private ComboBox<String> _selectLanguage;

    @FXML
    private TitledPane _imagePropsPane;
    
    private LinkedHashSet<Node> _widgets = new LinkedHashSet<>();
    private final String _errorStyle = "-fx-focus-color: red; -fx-border-color: red;";
    private final Glyph _warningGlyf = new Glyph("fontawesome", "WARNING");
    private Controller me = this;
    private PopOver _errorPopOver;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Color c = Color.rgb(_settings.getRedHighlight(), _settings.getGreenHighlight(), _settings.getBlueHighlight());
        _color.setValue(c);
        _input.setText(_settings.getSmiles());
        _title.setText(_settings.getMolTitle());
        _webView.contextMenuEnabledProperty().set(false);

        _penSize.valueProperty().addListener( (o, oldVal, newVal) -> {
                me.updateImage();
                int p = newVal.intValue();
                _penLabel.setText("Pen Size: " + String.valueOf(p));
                _settings.setPenSize(p); } );
        
        _titleSize.valueProperty().addListener((o, oldVal, newVal) -> {
                me.updateImage();
                int p = newVal.intValue();
                _titleSizeLable.setText("Title Font Size: " + String.valueOf(p));
                _settings.setFontSize(p);
            });
        
        _fontSize.valueProperty().addListener( (o, oldVal, newVal) -> {
                me.updateImage();
                float p = newVal.floatValue();
                _fontLabel.setText("Atom Font Scale: " + _df.format(p));
                _settings.setAtomFloatScale(p);
            });

        _rotation.valueProperty().addListener( (o, oldVal, newVal)-> {
            float p = newVal.floatValue();
            _rotationLabel.setText("Rotation: " + _df.format(p) + "°");
            _settings.setRotation(p);
            me.updateImage();
        });

        _selectLanguage.getItems().addAll("Python", "Java", "C++", "C#");
        _selectLanguage.setValue("Python");

        // maintain a set of widgets to disable in case of error
        _widgets.add(_height);         _widgets.add(_width);
        _widgets.add(_title);          _widgets.add(_submatch);
        _widgets.add(_titleSize);      _widgets.add(_input);
        _widgets.add(_color);          _widgets.add(_colorStyle);
        _widgets.add(_atomStereo);     _widgets.add(_bondStereo);
        _widgets.add(_superAtoms);     _widgets.add(_hydrogens);
        _widgets.add(_aromaticity);    _widgets.add(_penSize);
        _widgets.add(_titleLocBottom); _widgets.add(_saveImage);
        _widgets.add(_fontSize);       _widgets.add(_titleLocTop);
        _widgets.add(_rotation);       _widgets.add(_highlightStyle);
        _widgets.add(_flipX);          _widgets.add(_flipY);

        // show the image properties
        _accordian.setExpandedPane(_imagePropsPane);
        updateImage();
    }

    public void updateImage() {
        try {
            String smiles = _input.getText();
            _settings.setSmiles(smiles);
            if(smiles.length() == 0) {
                setErrorStyle(_input, "Valid SMILES, IUPAC name, or reaction required.");
                return;
            }
            
            // parse the smiles string
            _mol.Clear();
            boolean success = oechem.OESmilesToMol(_mol, smiles);
            if (!success) {
                success = oeiupac.OEParseIUPACName(_mol, smiles);
                if (!success) {
                    setErrorStyle(_input, "Failed to parse SMILES.");
                    return;
                }
            }
            setSuccessStyle(_input);

            _settings.setMolTitle(_title.getText());

            int width = getTextFieldIntValue(_width);
            if (width == 0) { return; }
            _settings.setImageWidth(width);
            int height = getTextFieldIntValue(_height);
            if (height == 0) { return; }
            _settings.setImageHeight(height);

            _settings.setFlipX(_flipX.isSelected());
            _settings.setFlipY(_flipY.isSelected());

            // set the styles for the molecule
            setTitleLocValue();
            setAromaticValue();
            setBondStereoValue();
            setAtomStereoValue();
            setSuperAtomValue();
            setHydrogenValue();
            setColorStyleValue();
            setHighlightStyleValue();
            setHighlighColor();

            // handle substructure highlighting
            if(_submatch.getText().length() > 0) {
                OESubSearch ss = new OESubSearch(_submatch.getText());
                _settings.setSubSearchQuery(_submatch.getText());
                if(!ss.IsValid()) {
                    setErrorStyle(_submatch, "Invalid substructure match query.");
                    return;
                } else {
                    setSuccessStyle(_submatch);
                    _color.setDisable(false);
                    _highlightStyle.setDisable(false);
                }
            } else { 
                setSuccessStyle(_submatch);
                _color.setDisable(true);
                _highlightStyle.setDisable(true);
                _settings.setSubSearchQuery("");
            }

            byte[] imgBytes = _image.drawSvgImage(_settings);

            String content = "<div style=\"border: 1px solid black;width:" + width + "px;height:" + height + "px;margin:0 auto;position:relative;\" align='center'>" +
                            new String(imgBytes) + "</div>";
            _webView.getEngine().loadContent(content);
            updateCodeArea();
            boolean reaction = _mol.IsRxn();
            _settings.setReaction(reaction);
            _flipX.setDisable(reaction);
            _flipY.setDisable(reaction);
            _rotation.setDisable(reaction);

        } catch(RuntimeException e) {
            _webView.getEngine().loadContent("<table align='center'><tr><td>" + 
                                           "<font color='red'>" + e.getMessage() + 
                                           "</font></td></tr></table>");
        }
    }

    // returns zero on error
    private int getTextFieldIntValue(TextField widget) {
        int v = 0;
        try { 
            v = Integer.parseInt(widget.getText());
        } catch(Exception e) { 
            setErrorStyle(widget, "Error: Invalid integer value: [" + String.valueOf(v) + "]");
           return 0;
        } 
        if (v <= 0) {
            setErrorStyle(widget, "Error: Invalid positive integer value: [" + String.valueOf(v) + "]");
            return 0;
        }
        if (v > 4000) {
            setErrorStyle(widget, "Error: Integer value too large: [" + String.valueOf(v) + "]");
            return 0;
        }
        setSuccessStyle(widget);
        return v;
    }
    
    @FXML
    public void handleTyping(KeyEvent event) {
        boolean haveTitle = _title.getText().equals("");
        _titleLocBottom.setDisable(haveTitle);
        _titleLocTop.setDisable(haveTitle);
        _titleSize.setDisable(haveTitle);
        updateImage();
    }
    
    @FXML
    public void update(ActionEvent event) {
        updateImage();
    }
    
    @FXML
    public void setTitleLocValue() {
        if (_title.getText().equals("")) {
            _settings.setTitleLocation(TitleLocation.HIDDEN);
            return;
        }
        
        if (_titleLocTop.isSelected())
            _settings.setTitleLocation(TitleLocation.TOP);
        else
            _settings.setTitleLocation(TitleLocation.BOTTOM);
    }
    
    @FXML
    public void saveImage(ActionEvent event) { 
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("SVG (*.svg)", ".svg"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG (*.png)", ".png"));
        fc.titleProperty().set("Save Image");
        fc.setInitialDirectory(_savePath);
        File tmp = fc.showSaveDialog(_webView.getScene().getWindow());
        if (tmp != null) {
            String path = tmp.getAbsolutePath();
            if(!path.endsWith(".svg") && !path.endsWith(".png")) {
                path += fc.getSelectedExtensionFilter().getExtensions().get(0);
                tmp = new File(path);
            }
            if(tmp != null) {
                // JavaFX FileChooser doesn't set the extension if the user didn't pick one.
                _image.saveImage(_settings, tmp);
            }
            _savePath = tmp.getAbsoluteFile().getParentFile();
        }
    }
    
    private void setErrorStyle(Node widget, String message) {
        Label label = new Label(message);
        //label.setGraphic(_warningGlyf);
        label.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
        if (_errorPopOver != null &&_errorPopOver.isShowing())
            _errorPopOver.hide();

        _errorPopOver = new PopOver(label);
        _errorPopOver.detachableProperty().set(false);
        _errorPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        _errorPopOver.show(widget);
        setErrorStyle(widget);
    }

    private void setErrorStyle(Node widget) {
        String style = widget.getStyle();
        widget.setStyle(style + _errorStyle);
        for(Node n: _widgets) {
            if (n!=null && n != widget) {
                n.disableProperty().set(true);
            }
        }
        _codeGenTab.setDisable(true);
    }
    
    private void setSuccessStyle(Node widget) {
        boolean noTitle = _title.getText().equals("");
        String style = widget.getStyle();
        style = style.replace(_errorStyle, "");
        widget.setStyle(style);
        if (_errorPopOver != null)
            _errorPopOver.hide();
        for(Node n: _widgets) {
            if (n!=null && n != widget) {
                if(noTitle && (n == _titleLocBottom || n == _titleLocTop || n == _titleSize)) { 
                   continue; 
                }
                n.disableProperty().set(false);
            }
        }
        _codeGenTab.setDisable(false);
    }
 
    private void setAromaticValue() {
        String aro = _aromaticity.getValue().toString();
        switch (aro) {
            case "Kekule":
                _settings.setAromaticStyle(AromaticStyle.KEKULE);
                break;
            case "Circle":
                _settings.setAromaticStyle(AromaticStyle.CIRCLE);
                break;
            case "Dash":
                _settings.setAromaticStyle(AromaticStyle.DASH);
                break;
            default:
                setErrorStyle(_aromaticity);
                throw new RuntimeException("Invalid Aromatic Style: [" + aro + "]");
        }
        setSuccessStyle(_aromaticity);
    }
    
    private void setBondStereoValue() {
        String bondStyle = _bondStereo.getValue().toString();
        switch (bondStyle) {
            case "Default":
                _settings.setBondStereoStyle(BondStereoStyle.DEFAULT);
                break;
            case "CIP":
                _settings.setBondStereoStyle(BondStereoStyle.CIPBONDSTEREO);
                break;
            case "Hidden":
                _settings.setBondStereoStyle(BondStereoStyle.HIDDEN);
                break;
            case "All":
                _settings.setBondStereoStyle(BondStereoStyle.ALL);
                break;
           default:
                setErrorStyle(_bondStereo);
                throw new RuntimeException("Invalid Bond Stereo style: [" + bondStyle + "]");
        }
        setSuccessStyle(_bondStereo);
    }
    
    private void setAtomStereoValue() {
        String atomStyle = _atomStereo.getValue().toString();
        switch (atomStyle) {
            case "Default":
                _settings.setAtomStereoStyle(AtomStereoStyle.DEFAULT);
                break;
            case "CIP":
                _settings.setAtomStereoStyle(AtomStereoStyle.CIPATOMSTEREO);
                break;
            case "Hidden":
                _settings.setAtomStereoStyle(AtomStereoStyle.HIDDEN);
                break;
            case "All":
                _settings.setAtomStereoStyle(AtomStereoStyle.ALL);
                break;
            default:
                setErrorStyle(_atomStereo);
                throw new RuntimeException("Invalid Atom Stereo style: [" + atomStyle + "]");
        }
        
        setSuccessStyle(_atomStereo);
     }
    
    private void setSuperAtomValue() {
        String sa = _superAtoms.getValue().toString();
        switch (sa) {
            case "None":
                _settings.setSuperAtomStyle(SuperAtomStyle.OFF);
                break;
            case "Carbon":
                _settings.setSuperAtomStyle(SuperAtomStyle.CARBON);
                break;
            case "Oxygen":
                _settings.setSuperAtomStyle(SuperAtomStyle.OXYGEN);
                break;
            case "Nitrogen":
                _settings.setSuperAtomStyle(SuperAtomStyle.NITROGEN);
                break;
            case "Oxygen & Nitrogen":
                _settings.setSuperAtomStyle(SuperAtomStyle.OXYGEN_AND_NITROGEN);
                break;
            case "Sulphur":
                _settings.setSuperAtomStyle(SuperAtomStyle.SULPHUR);
                break;
            case "Phosphorus":
                _settings.setSuperAtomStyle(SuperAtomStyle.PHOSPHORUS);
                break;
            case "Halides":
                _settings.setSuperAtomStyle(SuperAtomStyle.HALIDES);
                break;
            case "Ether":
                _settings.setSuperAtomStyle(SuperAtomStyle.ETHER);
                break;
            case "All":
                _settings.setSuperAtomStyle(SuperAtomStyle.ALL);
                break;
            default:
                setErrorStyle(_superAtoms);
                throw new RuntimeException("Invalid value for Super Atoms: [" + sa + "]");
        }
        
        setSuccessStyle(_superAtoms);
    }
    
    private void setHydrogenValue() {
        String hydrogenVal = _hydrogens.getValue().toString();
        switch(hydrogenVal) {
            case "Default":
                _settings.setHydrogenStyle(HydrogenStyle.DEFAULT);
                break;
            case "Explicit: All":
                _settings.setHydrogenStyle(HydrogenStyle.EXPLICIT_ALL);
                break;
            case "Excplicit: Hetero":                    
                _settings.setHydrogenStyle(HydrogenStyle.EXPLICT_HETERO);
                break;
            case "Explicit: Terminal":
                _settings.setHydrogenStyle(HydrogenStyle.EXPLICIT_TERMINAL);
                break;
            case "Implicit: All":
                _settings.setHydrogenStyle(HydrogenStyle.IMPLICIT_ALL);
                break;
            case "Implicit: Hetero":
                _settings.setHydrogenStyle(HydrogenStyle.IMPLICIT_HETERO);
                break;
            case "Implicit: Terminal":
                _settings.setHydrogenStyle(HydrogenStyle.IMPLICIT_TERMINAL);
                break;
            case "Hidden":
                _settings.setHydrogenStyle(HydrogenStyle.HIDDEN);
                break;
            default:
                setErrorStyle(_hydrogens);
                throw new RuntimeException("Invalid Hydrogen style: [" + hydrogenVal + "]");
        }
        
        setSuccessStyle(_hydrogens);
    }
    
    private void setColorStyleValue() {
        String colorStyle = _colorStyle.getValue().toString();
        
        switch(colorStyle) {
            case "White CPK":
                _settings.setColorStyle(ColorStyle.WHITE_CPK);
                break;
            case "White Monochrome":
                _settings.setColorStyle(ColorStyle.WHITE_MONOCHROME);
                break;
            case "Black CPK":
                _settings.setColorStyle(ColorStyle.BLACK_CPK);
                break;
            case "Black Monochrome":
                _settings.setColorStyle(ColorStyle.BLACK_MONOTONE);
                break;
            default:
                setErrorStyle(_colorStyle);
                throw new RuntimeException("Invalid color style: [" + colorStyle + "]");
        }
        setSuccessStyle(_colorStyle);
    }

    private void setHighlightStyleValue() {
        String style = _highlightStyle.getValue().toString();
        switch(style) {
            case "Color":
                _settings.setHiglightStyle(HighlightStyle.COLOR);
                break;
            case "Stick":
                _settings.setHiglightStyle(HighlightStyle.STICK);
                break;
            case "Ball And Stick":
                _settings.setHiglightStyle(HighlightStyle.BALL_AND_STICK);
                break;
            case "Cogwheel":
                _settings.setHiglightStyle(HighlightStyle.COGWHEEL);
                break;
            default:
                setErrorStyle(_colorStyle);
                throw new RuntimeException("Invalid highligh style: [" + style + "]");
        }
        setSuccessStyle(_colorStyle);
    }

    private void setHighlighColor() {
        Color c = _color.getValue();
        _settings.setRedHighlight((int)(c.getRed()*256));
        _settings.setGreenHighlight((int)(c.getGreen()*256));
        _settings.setBlueHighlight((int)(c.getBlue()*256));
    }

private File chooseFile(String language, String extension) {
    final String filter = String.format("%s (*%s)", language, extension);
    FileChooser fc = new FileChooser();
    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(filter, "*" + extension));

    fc.titleProperty().set("Saving a " + language + " file ");
    fc.setInitialDirectory(_savePath);
    File tmp = fc.showSaveDialog(_webView.getScene().getWindow());
    if (tmp == null) { return null; }

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
    return tmp;
}

    @FXML
    public void saveCode(ActionEvent event) {
        String language = _selectLanguage.getValue();
        String extension = ".py";
        if (language == "C++") {
            extension = ".cpp";
        } else if (language == "Java") {
            extension = ".java";
        } else if (language == "C#") {
            extension = ".cs";
        }

        File f = chooseFile(language, extension);
        try {
            if (f != null) {
                String code = generateCode();
                Writer fw = new FileWriter(f);
                fw.write(code);
                fw.close();
            }
        } catch(Exception e) {

            Window w = _webView.getScene().getWindow();
            String trace = stackTraceToString(e.getStackTrace());
    // TODO: figure out how to make nice dialogs!
    //        final Dialog<Void> ed = new Dialog<>();
    //        ed.setContentText("An error occurred while generating example code:\n" + e.getMessage());
    //        ed.setGraphic(_warningGlyf);
    //        ed.getDialogPane().autosize();
    //        final Button close = new Button("Close");
    //        close.onActionProperty().setValue(a -> {ed.close();});
    //        ed.getDialogPane().getChildren().add(close);
    //        ed.showAndWait();
        }
    }

    private static String stackTraceToString(StackTraceElement[] stack) {
        StringBuffer buffer = new StringBuffer();
        for (StackTraceElement frame: stack)
            buffer.append(frame.toString() + "\n");
        return buffer.toString();
    }

    public String generateCode() {
        try {
            String content = "";
            switch (_selectLanguage.getValue()) {
                case "Python":
                    content = CodeGen.generatePython(_settings);
                    break;
                case "C++":
                    content = CodeGen.generateCpp(_settings);
                    break;
                case "Java":
                    content = CodeGen.generateJava(_settings);
                    break;
                case "C#":
                    content = CodeGen.generateCSharp(_settings);
                    break;
            }
            return content;
        } catch(Exception e) {
            System.out.println("Caught exception: " + e.getMessage() + "\n" + stackTraceToString(e.getStackTrace()));
            // TODO: nice dialog explaining things went wrong
        }
        return null;
    }

    public void updateCodeArea() {
        _codeArea.setText(generateCode());
    }

    @FXML
    public void updateCodeArea(ActionEvent event) {
        updateCodeArea();
    }

}
