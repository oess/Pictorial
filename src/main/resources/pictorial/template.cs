
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

        <#if rotation != "0.0" || flipX != "1.0" || flipY != "1.0">
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

        OEColor color = new OEColor(${redHighlight}, ${greenHighlight}, ${blueHighlight});
        foreach (OEMatchBase match in subsearch.Match(mol, true)) {
            OEDepict.OEAddHighlighting(display2d, color, ${highlightStyle}, match);
        }

        </#if>
        OEDepict.OERenderMolecule(image, display2d);
        OEDepict.OEWriteImage("${imageName}.png", image);
        Console.WriteLine("Depiction saved to ${imageName}.png");
    }
    <#if rotation != "0.0" || flipX != "1.0" || flipY != "1.0">

    private void rotateAndFlip(Settings s) {
        float flipX = ${flipX}F;
        float flipY = ${flipY}F;
        float rotate = ${rotation}F;

        float cos = (float) Math.cos(rotate);
        float sin = (float) Math.sin(rotate);
        OEFloatArray angles = new OEFloatArray(8);
        angles.setItem(0, flipY *  cos);  angles.setItem(1, flipY * sin);   angles.setItem(2, 0.0f);
        angles.setItem(3, flipX * -sin);  angles.setItem(4, flipX * cos);   angles.setItem(5, 0.0f);
        angles.setItem(6,0.0f);           angles.setItem(7, 0.0f);          angles.setItem(8, 0.0f);

        oechem.OECenter(mol);  // this must be called before OERotate
        oechem.OERotate(mol, matrix);

        if ( flipX == -1.0 || flipY == -1.0)
            oechem.OEMDLPerceiveBondStereo(mol);
    }
    </#if>

    public static int Main(string[] args) {
        ${imageName} obj = new ${imageName}();
        obj.makeImage();
        return 0;
    }
} 
