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
            mol.Clear();
            success = oeiupac.OEParseIUPACName(mol, smiles);
            if (!success) {
                throw new RuntimeException("Invalid smiles string " + smiles);
            }
        }

        OEImage image = new OEImage(imageWidth, imageHeight);
        <#if titleLen != 0>
        mol.SetTitle(molTitle);
        </#if>
        oedepict.OEPrepareDepiction(mol, true, true);  // assigns 2D coordinates to the atoms

        <#if rotation != "0.0" || flipX != "1.0" || flipY != "1.0">
        rotateAndFlip(mol);
        </#if>

        OE2DMolDisplayOptions displayOpts = new OE2DMolDisplayOptions(imageWidth, imageHeight, OEScale.AutoScale);
        <#if titleLen != 0>
        OEFont titleFont = new OEFont();
        titleFont.SetSize(${fontSize});
        displayOpts.SetTitleFont(titleFont);
        displayOpts.SetTitleLocation(${titleLoc});
        <#else>
        displayOpts.SetTitleLocation(OETitleLocation.Hidden);
        </#if>
        displayOpts.SetDefaultBondPen(bondPen);
        displayOpts.SetAromaticStyle(${aromaticStyle});
        displayOpts.SetBondStereoStyle(${bondStereoStyle});
        displayOpts.SetAtomStereoStyle(${atomStereoStyle});
        displayOpts.SetSuperAtomStyle(${superAtomStyle});
        displayOpts.SetHydrogenStyle(${hydrogenStyle});
        displayOpts.SetAtomLabelFontScale(${atomFontScale});
        displayOpts.SetAtomColorStyle(${colorStyle});

        OE2DMolDisplay display2d = new OE2DMolDisplay(mol, displayOpts);

        <#if queryLen != 0>
        OESubSearch subsearch = new OESubSearch(ssquery);
        if (!subsearch.IsValid()) {
            throw new RuntimeException("Invalid substructure query: " + ssquery);
        }

        OEColor color = new OEColor(${redHighlight}, ${greenHighlight}, ${blueHighlight});
        for (OEMatchBase match: subsearch.Match(mol, true)) {
            oedepict.OEAddHighlighting(display2d, color, ${highlightStyle}, match);
        }

        </#if>
        oedepict.OERenderMolecule(image, display2d);
        oedepict.OEWriteImage("${imageName}.png", image);
        System.out.println("Depiction saved to ${imageName}.png");
    }

    <#if rotation != "0.0" || flipX != "1.0" || flipY != "1.0">
    private void rotateAndFlip(OEGraphMol mol) {
        float flipX = ${flipX}f;
        float flipY = ${flipY}f;
        float rotate = ${rotation}f;

        float cos = (float) Math.cos(rotate);
        float sin = (float) Math.sin(rotate);
        OEFloatArray matrix = new OEFloatArray(8);
        matrix.setItem(0, flipY *  cos);  matrix.setItem(1, flipY * sin);   matrix.setItem(2, 0.0f);
        matrix.setItem(3, flipX * -sin);  matrix.setItem(4, flipX * cos);   matrix.setItem(5, 0.0f);
        matrix.setItem(6, 0.0f);          matrix.setItem(7, 0.0f);          matrix.setItem(8, 0.0f);

        oechem.OECenter(mol);  // this must be called before OERotate
        oechem.OERotate(mol, matrix);

        if (flipX == -1.0f || flipY == -1.0f)
            oechem.OEMDLPerceiveBondStereo(mol);
    }
    </#if>

    public static void main(String[] args) {
        ${imageName} obj = new ${imageName}();
        obj.makeImage();
    }
} 
