import openeye.oechem.*;
import openeye.oedepict.*;

public class ${imageName} {

    private String molTitle = "${molTitle}";
    private String smiles = "${smiles}";
    private String ssquery = "${substructure}";
    private OEPen bondPen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.On, ${penSize});
    private boolean stripSalts = ${stripSalts};
    private int imageWidth = ${imageWidth};
    private int imageHeight= ${imageHeight};

    public void makeImage() {

        OEGraphMol mol = new OEGraphMol();
        if (!oechem.OESmilesToMol(mol, smiles)) {
            throw new RuntimeException("invalid smiles string" + smiles);
        }

        OEImage image = new OEImage(imageWidth, imageHeight);

        if (stripSalts) {
            oechem.OETheFunctionFormerlyKnownAsStripSalts(mol);
        }

        mol.SetTitle(molTitle);
        oedepict.OEPrepareDepiction(mol, true, true);
        OE2DMolDisplayOptions displayOpts = new OE2DMolDisplayOptions(imageWidth, imageHeight, OEScale.AutoScale);
        displayOpts.SetDefaultBondPen(bondPen);
        displayOpts.SetAromaticStyle(${aromaticStyle});
        displayOpts.SetBondStereoStyle(${bondStereoStyle});
        displayOpts.SetAtomStereoStyle(${atomStereoStyle});
        displayOpts.SetSuperAtomStyle(${superAtomStyle});
        displayOpts.SetHydrogenStyle(${hydrogenStyle});
        displayOpts.SetAtomLabelFontScale(${atomFontScale});
        displayOpts.SetAtomColorStyle(${colorStyle});

        if (!molTitle.isEmpty()) {
            OEFont titleFont = new OEFont();
            titleFont.SetSize(${fontSize});
            displayOpts.SetTitleFont(titleFont);
            displayOpts.SetTitleLocation(${titleLoc});
        }

        OE2DMolDisplay display2d = new OE2DMolDisplay(mol, displayOpts);

        if (!ssquery.isEmpty()) {
            OESubSearch subsearch = new OESubSearch(ssquery);
            if (!subsearch.IsValid()) {
                throw new RuntimeException("Invalid substructure query: " + ssquery);
            }

            OEColor color = new OEColor((int)(255*${redHighlight}),
                                        (int)(255*${greenHighlight}),
                                        (int)(255*${blueHighlight}));
            for (OEMatchBase match: subsearch.Match(mol, true)) {
                oedepict.OEAddHighlighting(display2d, color, OEHighlightStyle.Stick, match);
            }
        }

        oedepict.OERenderMolecule(image, display2d);
        oedepict.OEWriteImage("${imageName}.png", image);
        System.out.println("Depiction saved to ${imageName}.png");
    }

    public static void main(String[] args) {
        ${imageName} obj = new ${imageName}();
        obj.makeImage();
    }
} 
