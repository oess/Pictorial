//
// compile with
// g++ ${imageName}.cpp -Iinclude -Llib -Wl,--start-group -loeplatform -loesystem -loechem -loedepict -loeiupac -Wl,--end-group -lpthread -lz -lcairo
//
#include <string.h>

#include <openeye.h>
#include <oeplatform.h>
#include <oesystem.h>
#include <oechem.h>
#include <oedepict.h>
#include <oeiupac.h>
#include <iostream>
#include <cmath>

using namespace OEPlatform;
using namespace OESystem;
using namespace OEChem;
using namespace OEDepict;
using namespace OEIUPAC;
using namespace std;

<#assign titleLen = molTitle?length>
<#assign queryLen = substructure?length>
class ${imageName} 
{
    private:
    string smiles;
    <#if titleLen != 0>
    string molTitle;
    </#if>
    <#if queryLen != 0>
    string ssquery;
    </#if>
    OEPen bondPen;
    int imageWidth;
    int imageHeight;

    public:
    ${imageName}() 
    {
        imageWidth  = ${imageWidth};
        imageHeight = ${imageHeight};
        smiles = "${smiles}";
        <#if titleLen != 0>molTitle = "${molTitle}";</#if>
        <#if queryLen != 0>ssquery = "${substructure}";</#if>
        bondPen = OEPen(OEBlack, OEBlack, OEFill::On, ${penSize});
    }
    <#if reaction != "true" && (rotation != "0.0" || flipX != "1.0" || flipY != "1.0")>

    void rotateAndFlip(OEGraphMol &mol)
    {
        float flipX = ${flipX};
        float flipY = ${flipY};
        float rotate = ${rotation};
        float cosine = (float) cos(${rotation});
        float sine = (float) sin(${rotation});
        float matrix[9] = { flipY * cosine, flipY * sine,   0.0,
                            flipX * -sine,  flipX * cosine, 0.0,
                             0.0,           0.0,            0.0 };

        OECenter(mol);  // this must be called prior to OERotate
        OERotate(mol, matrix);

        if (flipX == -1.0f || flipY == -1.0f)
            OEMDLPerceiveBondStereo(mol);
    }
    </#if>

    void makeImage() 
    {
        OEGraphMol mol;

        // parse the smiles string
        bool success = OESmilesToMol(mol, smiles);
        if (!success) 
        { 
            mol.Clear();
            success = OEParseIUPACName(mol, smiles.c_str());
            if (!success)
                OEThrow.Fatal("Invalid smiles string %s", smiles.c_str());
        }

        OEImage image(imageWidth, imageHeight);
        <#if titleLen != 0>
        mol.SetTitle(molTitle);
        </#if>
        OEPrepareDepiction(mol, true, true);

        <#if reaction != "true" && (rotation != "0.0" || flipX != "1.0" || flipY != "1.0")>
        rotateAndFlip(mol);

        </#if>
        OE2DMolDisplayOptions displayOpts(imageWidth, imageHeight, OEScale::AutoScale);
        <#if titleLen != 0>
        // set the title font and location
        OEFont titleFont;
        titleFont.SetSize(${fontSize});
        displayOpts.SetTitleFont(titleFont);
        displayOpts.SetTitleLocation(${titleLoc});
        <#else>
        displayOpts.SetTitleLocation(OETitleLocation::Hidden);
        </#if>
        displayOpts.SetDefaultBondPen(bondPen);
        displayOpts.SetAromaticStyle(${aromaticStyle});
        displayOpts.SetBondStereoStyle(${bondStereoStyle});
        displayOpts.SetAtomStereoStyle(${atomStereoStyle});
        displayOpts.SetSuperAtomStyle(${superAtomStyle});
        displayOpts.SetHydrogenStyle(${hydrogenStyle});
        displayOpts.SetAtomLabelFontScale(${atomFontScale});
        displayOpts.SetAtomColorStyle(${colorStyle});

        OE2DMolDisplay display2d(mol, displayOpts);

        <#if queryLen != 0>
        OESubSearch ss;
        if (!ss.Init(ssquery.c_str())) 
            OEThrow.Fatal("Invalid substructure query: %s", ssquery.c_str());

        OEColor color(${redHighlight}, ${greenHighlight}, ${blueHighlight}, ${alphaHighlight});
        OEIter<OEMatchBase> mi;
        for(mi = ss.Match(mol, true); mi; ++mi) 
            OEAddHighlighting(display2d, color, ${highlightStyle}, *mi);

        </#if>
        OERenderMolecule(image, display2d);
        OEWriteImage("${imageName}.png", image);
        cout << "Depiction saved to "<< "${imageName}.png" << endl;
    }

}; 

int main(int argc, char * argv[])
{
    ${imageName} obj;
    obj.makeImage();
}
