import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oeiupac.*;

<#assign titleLen = molTitle?length>
<#assign queryLen = substructure?length>

public class ${imageName} {

    <#if titleLen != 0>
    private String molTitle = "${molTitle}";
    </#if>
    private String smiles = "${smiles}";

    <#if queryLen != 0>
    private String ssquery = "${substructure}";
    </#if>
    private OEPen bondPen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.On, ${penSize});
    private int imageWidth = ${imageWidth};
    private int imageHeight= ${imageHeight};

    public void makeImage() {

        OEGraphMol mol = new OEGraphMol();

        // parse the smiles string
        boolean success = oechem.OESmilesToMol(mol, smiles);
        if (!success) { 
            success = oeiupac.OEParseIUPACName(mol, smiles);
            if (!success) {
                throw new RuntimeException("invalid smiles string" + smiles);
            }
        }

        OEImage image = new OEImage(imageWidth, imageHeight);
        <#if titleLen != 0>
        mol.SetTitle(molTitle);
        </#if>
        oedepict.OEPrepareDepiction(mol, true, true);

        <#if rotation != "0.0"> 
        double[] angles = new double[3];
        angles[0] = ${rotation};
        oechem.OEEulerRotate(mol, angles);
        </#if>

        OE2DMolDisplayOptions displayOpts = new OE2DMolDisplayOptions(imageWidth, imageHeight, OEScale.AutoScale);
        displayOpts.SetDefaultBondPen(bondPen);
        displayOpts.SetAromaticStyle(${aromaticStyle});
        displayOpts.SetBondStereoStyle(${bondStereoStyle});
        displayOpts.SetAtomStereoStyle(${atomStereoStyle});
        displayOpts.SetSuperAtomStyle(${superAtomStyle});
        displayOpts.SetHydrogenStyle(${hydrogenStyle});
        displayOpts.SetAtomLabelFontScale(${atomFontScale});
        displayOpts.SetAtomColorStyle(${colorStyle});

        <#if titleLen != 0>
        OEFont titleFont = new OEFont();
        titleFont.SetSize(${fontSize});
        displayOpts.SetTitleFont(titleFont);
        <#else>
        displayOpts.SetTitleLocation(OETitleLocation.Hidden);
        </#if>

        OE2DMolDisplay display2d = new OE2DMolDisplay(mol, displayOpts);

        <#if queryLen != 0>
        OESubSearch subsearch = new OESubSearch(ssquery);
        if (!subsearch.IsValid()) {
            throw new RuntimeException("Invalid substructure query: " + ssquery);
        }

        OEColor color = new OEColor(${redHighlight}, ${greenHighlight}, ${blueHighlight});
        for (OEMatchBase match: subsearch.Match(mol, true)) {
            oedepict.OEAddHighlighting(display2d, color, OEHighlightStyle.Stick, match);
        }

        </#if>
        oedepict.OERenderMolecule(image, display2d);
        oedepict.OEWriteImage("${imageName}.png", image);
        System.out.println("Depiction saved to ${imageName}.png");
    }

    public static void main(String[] args) {
        ${imageName} obj = new ${imageName}();
        obj.makeImage();
    }
} 
