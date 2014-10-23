
using System;
using OpenEye.OEChem;
using OpenEye.OEDepict;
using OpenEye.OEIUPAC;
<#assign titleLen = molTitle?length>
<#assign queryLen = substructure?length>

public class ${imageName} {

    <#if titleLen != 0>
    private string molTitle = "${molTitle}";
    </#if>
    private string smiles = "${smiles}";
    <#if queryLen != 0>
    private string ssquery = "${substructure}";
    </#if>
    private OEPen bondPen = new OEPen(OEChem.OEBlack, OEChem.OEBlack, OEFill.On, ${penSize});
    private int imageWidth = ${imageWidth};
    private int imageHeight= ${imageHeight};

    public void makeImage() {

        OEGraphMol mol = new OEGraphMol();

        // parse the smiles string
        bool success = OEChem.OESmilesToMol(mol, smiles);
        if (!success) { 
            mol.Clear();
            success = OEIUPAC.OEParseIUPACName(mol, smiles);
            if (!success) {
                throw new Exception("Invalid smiles string " + smiles);
            }
        }

        OEImage image = new OEImage(imageWidth, imageHeight);
        <#if titleLen != 0>
        mol.SetTitle(molTitle);
        </#if>
        OEDepict.OEPrepareDepiction(mol, true, true); // add 2D coordinates to the molecule

        <#if reaction != "true" && (rotation != "0.0" || flipX != "1.0" || flipY != "1.0")>
        rotateAndFlip(mol);

        </#if>
        OE2DMolDisplayOptions displayOpts = new OE2DMolDisplayOptions(imageWidth, imageHeight, OEScale.AutoScale);
        <#if titleLen != 0>
        // set the title font and location
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
        // Substructure highlighting
        OESubSearch subsearch = new OESubSearch(ssquery);
        if (!subsearch.IsValid()) {
            throw new Exception("Invalid substructure query: " + ssquery);
        }

        OEColor color = new OEColor(${redHighlight}, ${greenHighlight}, ${blueHighlight}, ${alphaHighlight});
        foreach (OEMatchBase match in subsearch.Match(mol, true)) {
            OEDepict.OEAddHighlighting(display2d, color, ${highlightStyle}, match);
        }

        </#if>
        OEDepict.OERenderMolecule(image, display2d);
        OEDepict.OEWriteImage("${imageName}.png", image);
        Console.WriteLine("Depiction saved to ${imageName}.png");
    }
    <#if reaction != "true" && (rotation != "0.0" || flipX != "1.0" || flipY != "1.0")>

    private void rotateAndFlip(OEGraphMol mol) {
        float flipX = ${flipX}f;
        float flipY = ${flipY}f;
        float rotate = ${rotation}f;

        float cos = (float) Math.Cos(rotate);
        float sin = (float) Math.Sin(rotate);
        float[] matrix = {
            flipY * cos,  flipY * sin, 0.0f,
            flipX * -sin, flipX * cos, 0.0f,
            0.0f,         0.0f,        0.0f};

        OEChem.OECenter(mol);  // this must be called before OERotate
        OEChem.OERotate(mol, matrix);

        if ( flipX == -1.0 || flipY == -1.0)
            OEChem.OEMDLPerceiveBondStereo(mol);
    }
    </#if>

    public static int Main(string[] args) {
        ${imageName} obj = new ${imageName}();
        obj.makeImage();
        return 0;
    }
} 
