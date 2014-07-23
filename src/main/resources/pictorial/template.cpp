#include <string.h>

#include <openeye.h>
#include <oeplatform.h>
#include <oesystem.h>
#include <oechem.h>
#include <oedepict.h>
#include <oeiupac.h>

using namespace OEPlatform;
using namespace OESystem;
using namespace OEChem;
using namespace OEDepict;
using namespace std;

<#assign titleLen = molTitle?length>
<#assign queryLen = substructure?length>
class ${imageName} 
{
    <#if titleLen != 0>
    string molTitle = "${molTitle}";
    </#if>
    string smiles = "${smiles}";

    <#if queryLen != 0>
    string ssquery = "${substructure}";
    </#if>
    OEPen bondPen(getOEBlack(), getOEBlack(), OEFill.On, ${penSize});
    int imageWidth = ${imageWidth};
    int imageHeight= ${imageHeight};

    void makeImage() 
    {
        OEGraphMol mol;

        // parse the smiles string
        boolean success = OESmilesToMol(mol, smiles);
        if (!success) 
        { 
            success = OEParseIUPACName(mol, smiles);
            if (!success)
                OEThrow.Fatal("Invalid smiles string" + smiles);
        }

        OEImage image(imageWidth, imageHeight);
        <#if titleLen != 0>
        mol.SetTitle(molTitle);
        </#if>
        OEPrepareDepiction(mol, true, true);

        <#if rotation != "0.0"> 
        double[] angles = {${rotation}, 0.0f, 0.0f};
        OEEulerRotate(mol, angles);

        </#if>
        OE2DMolDisplayOptions displayOpts(imageWidth, imageHeight, OEScale.AutoScale);
        displayOpts.SetDefaultBondPen(bondPen);
        displayOpts.SetAromaticStyle(${aromaticStyle});
        displayOpts.SetBondStereoStyle(${bondStereoStyle});
        displayOpts.SetAtomStereoStyle(${atomStereoStyle});
        displayOpts.SetSuperAtomStyle(${superAtomStyle});
        displayOpts.SetHydrogenStyle(${hydrogenStyle});
        displayOpts.SetAtomLabelFontScale(${atomFontScale});
        displayOpts.SetAtomColorStyle(${colorStyle});

        <#if titleLen != 0>
        OEFont titleFont;
        titleFont.SetSize(${fontSize});
        displayOpts.SetTitleFont(titleFont);
        <#else>
        displayOpts.SetTitleLocation(OETitleLocation::Hidden);
        </#if>

        OE2DMolDisplay display2d(mol, displayOpts);

        <#if queryLen != 0>
        OESubSearch subsearch = new OESubSearch(ssquery);
        if (!subsearch.IsValid())
            OEThrow.Fatal("Invalid substructure query: " + ssquery);

        OEColor color = new OEColor(${redHighlight}, ${greenHighlight}, ${blueHighlight});
        for (OEMatchBase match: subsearch.Match(mol, true)) 
            OEAddHighlighting(display2d, color, ${highlightStyle}, match);

        </#if>
        OERenderMolecule(image, display2d);
        OEWriteImage("${imageName}.png", image);
        cout << "Depiction saved to "<< "${imageName}.png" << endl;
    }

    int main(int argc, char * argv[])
    {
        ${imageName} obj;
        obj.makeImage();
    }
} 
