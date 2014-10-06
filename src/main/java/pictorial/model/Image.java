package pictorial.model;

import openeye.oechem.*;
import openeye.oedepict.*;
import openeye.oeiupac.oeiupac;

import java.io.File;

public class Image {
    final private OEGraphMol _mol = new OEGraphMol();
    final private OEPen _bondPen = new OEPen(oechem.getOEBlack(), oechem.getOEBlack(), OEFill.On, 2.0);
    final private OEFont _titleFont = new OEFont();


    // Most of the OpenEye API calls are demonstrated in this method
    // To create a depiction from a smiles string:
    // 1) Parse the smiles
    // 2) Prepare the molecule
    // 3) Prepare the depiction
    //    - Set the appropriate depict settings
    //    - Create an OEImage to render to
    //    - Create a OEDisplay
    // 4) Render image
    private OEImage drawImage(final Settings s) {
        _mol.Clear();  // allow us to reuse the same molecule object

        final String smiles = s.getSmiles();
        boolean success = oechem.OESmilesToMol(_mol, smiles);
        if (!success) {
            success = oeiupac.OEParseIUPACName(_mol, smiles);
            if (!success) {
                throw new RuntimeException("Failed to parse " + smiles);
            }
        }

        success = oedepict.OEPrepareDepiction(_mol, true, true);
        if (!success) {
            throw new RuntimeException("PrepareDepiction failed");
        }

        _mol.SetTitle(s.getMolTitle());

        // rotate the molecule and flip the molecule
        if (s.getRotation() != 0.0f || s.getFlipX() != 0.0f || s.getFlipY() != 0.0f) {
            double[] angles = new double[3];
            angles[0] = s.getRotation();
            angles[1] = s.getFlipX();
            angles[2] = s.getFlipY();
            oechem.OEEulerRotate(_mol, angles);
        }

        // create an OEImage to render to
        int width = s.getImageWidth();
        int height = s.getImageHeight();
        final OEImage img = new OEImage(width, height);
        // set the display options
        OE2DMolDisplayOptions opts = new OE2DMolDisplayOptions(width, height, OEScale.AutoScale);
        _bondPen.SetLineWidth(s.getPenSize());
        opts.SetDefaultBondPen(_bondPen);
        _titleFont.SetSize(s.getFontSize());
        opts.SetTitleFont(_titleFont);
        opts.SetTitleLocation(s.getTitleLocation().getValue());
        opts.SetAromaticStyle(s.getAromaticStyle().getValue());
        opts.SetBondStereoStyle(s.getBondStereoStyle().getValue());

        opts.SetAtomStereoStyle(s.getAtomStereoStyle().getValue());
        opts.SetSuperAtomStyle(s.getSuperAtomStyle().getValue());

        opts.SetHydrogenStyle(s.getHydrogenStyle().getValue());
        opts.SetTitleLocation(s.getTitleLocation().getValue());
        opts.SetAtomColorStyle(s.getColorStyle().getValue());
        opts.SetAtomLabelFontScale(s.getAtomFontScale());

        OE2DMolDisplay display = new OE2DMolDisplay(_mol, opts);

        // substructure search
        String sss = s.getSubSearchQuery();
        if(!sss.isEmpty()) {
            OESubSearch ss = new OESubSearch(sss);
            OEColor color = new OEColor(s.getRedHighlight(),
                    s.getGreenHighlight(),
                    s.getBlueHighlight());
            int highlightStyle = s.getHiglightStyle().getValue();
            for (OEMatchBase match : ss.Match(_mol, true))
                oedepict.OEAddHighlighting(display, color, highlightStyle, match);
        }

        // render the molecule
        success = oedepict.OERenderMolecule(img, display);
        if(!success) {
            throw new RuntimeException("OERenderMolecule failed");
        }
        return img;
    }

    public byte[] drawSvgImage(Settings settings) {
        OEImage img = drawImage(settings);
        byte[] ret = oedepict.OEWriteImageToByteArray("bsvg", img);
        return ret;

    }

    public void saveImage(Settings settings, File path) {
        OEImage img = drawImage(settings);
       if (!oedepict.OEWriteImage(path.getAbsolutePath(), img)) {
           throw new RuntimeException("OEWriteImage failed");
       }
    }
}
