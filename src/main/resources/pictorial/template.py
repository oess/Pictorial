from __future__ import print_function
from openeye.oechem import *
from openeye.oedepict import *
from openeye.oeiupac import *
import math

<#assign titleLen = molTitle?length>
<#assign queryLen = substructure?length>
<#if rotation != "0.0" || flipX != "1.0" || flipY != "1.0">
def rotateAndFlip(mol):
    flipX = ${flipX}
    flipY = ${flipY}
    cosine = math.cos(${rotation});
    sine = math.sin(${rotation});
    matrix = OEDoubleArray([ flipY * cosine, flipY * sine,   0.0,
                             flipX * -sine,  flipX * cosine, 0.0,
                             0.0,            0.0,            0.0])

    OECenter(mol);  # this must be called prior to OERotate
    OERotate(mol, matrix);

    if flipX == -1.0 or flipY == -1.0:
        OEMDLPerceiveBondStereo(mol)

</#if>
def makeImage():
    <#if titleLen != 0>
    molTitle = "${molTitle}"
    </#if>
    smiles = "${smiles}"
    mol = OEGraphMol()
    success = OESmilesToMol(mol, smiles)
    if not success:
        mol.Clear();
        success = OEParseIUPACName(mol, smiles)
        if not success:
            raise Exception("invalid smiles string " + smiles)

    image = OEImage(${imageWidth}, ${imageHeight})
    bondPen = OEPen(OEBlack, OEBlack, OEFill_On, ${penSize})

    <#if titleLen != 0>
    mol.SetTitle(molTitle)

    </#if>
    OEPrepareDepiction(mol, True, True) # set the 2d coordinates for the molecule
    <#if rotation != "0.0" || flipX != "1.0" || flipY != "1.0">
    rotateAndFlip(mol)

    </#if>
    displayOpts = OE2DMolDisplayOptions(${imageWidth}, ${imageHeight}, OEScale_AutoScale)
    displayOpts.SetDefaultBondPen(bondPen)
    displayOpts.SetAromaticStyle(${aromaticStyle})
    displayOpts.SetBondStereoStyle(${bondStereoStyle})
    displayOpts.SetAtomStereoStyle(${atomStereoStyle})
    displayOpts.SetSuperAtomStyle(${superAtomStyle})
    displayOpts.SetHydrogenStyle(${hydrogenStyle})
    displayOpts.SetAtomLabelFontScale(${atomFontScale})
    displayOpts.SetAtomColorStyle(${colorStyle})

    <#if titleLen != 0>
    # set how the title should be displayed
    titleFont = OEFont()
    titleFont.SetSize(${fontSize})
    displayOpts.SetTitleFont(titleFont)
    displayOpts.SetTitleLocation(${titleLoc})
    <#else>
    displayOpts.SetTitleLocation(OETitleLocation_Hidden)
    </#if>

    display2d = OE2DMolDisplay(mol, displayOpts)

    <#if queryLen != 0 >
    # add substructure matching to the depiction
    subsearch = OESubSearch("${substructure}")
    if not subsearch.IsValid():
        raise Exception("Invalid substructure query")

    color = OEColor(${redHighlight}, ${greenHighlight}, ${blueHighlight})

    for match in subsearch.Match(mol, True):
        OEAddHighlighting(display2d, color, ${highlightStyle}, match)

    </#if>
    OERenderMolecule(image, display2d)
    OEWriteImage("${imageName}.png", image)
    print("Depiction saved to ${imageName}.png")

if __name__ == "__main__":
    makeImage()
