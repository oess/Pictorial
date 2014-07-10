from __future__ import print_function
from openeye.oechem import *
from openeye.oedepict import *

def makeImage():
    molTitle = "${molTitle}"
    mol = OEGraphMol()
    if not oechem.OESmilesToMol(mol, "${smiles}"):
        raise Exception("invalid smiles string ${smiles}")
    image = OEImage(${imageWidth}, ${imageHeight})
    bondPen = OEPen(OEBlack, OEBlack, OEFill_On, ${penSize})

    mol.SetTitle(molTitle)
    OEPrepareDepiction(mol, True, True)
    displayOpts = OE2DMolDisplayOptions(${imageWidth}, ${imageHeight}, OEScale_AutoScale)
    displayOpts.SetDefaultBondPen(bondPen)
    displayOpts.SetAromaticStyle(${aromaticStyle})
    displayOpts.SetBondStereoStyle(${bondStereoStyle})
    displayOpts.SetAtomStereoStyle(${atomStereoStyle})
    displayOpts.SetSuperAtomStyle(${superAtomStyle})
    displayOpts.SetHydrogenStyle(${hydrogenStyle})
    displayOpts.SetAtomLabelFontScale(${atomFontScale})
    displayOpts.SetAtomColorStyle(${colorStyle})

    if len(molTitle):
        titleFont = OEFont()
        titleFont.SetSize(${fontSize})
        displayOpts.SetTitleFont(titleFont)
        displayOpts.SetTitleLocation(${titleLoc})

    display2d = OE2DMolDisplay(mol, displayOpts)

    ssquery = "${substructure}";
    if len(ssquery):
        subsearch = OESubSearch(ssquery)
        if not subsearch.IsValid():
            raise Exception("Invalid substructure query")

        color = OEColor(${redHighlight}, ${greenHighlight}, ${blueHighlight})

        for match in subsearch.Match(mol, True):
            OEAddHighlighting(display2d, color, OEHighlightStyle_Stick, match)

    OERenderMolecule(image, display2d)
    OEWriteImage("${imageName}.png", image)
    print("Depiction saved to ${imageName}.png")

if __name__ == "__main__":
    makeImage()
