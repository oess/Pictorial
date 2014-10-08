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
           _mol.Clear(); 
            success = oeiupac.OEParseIUPACName(_mol, smiles);
            if (!success) {
                throw new RuntimeException("Failed to parse " + smiles);
            }
        }

        // assign 2d coordinates
        success = oedepict.OEPrepareDepiction(_mol, true, true);
        if (!success) {
            throw new RuntimeException("PrepareDepiction failed");
        }

        _mol.SetTitle(s.getMolTitle());

        rotateAndFlip(s);

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

    private void rotateAndFlip(Settings s) {
        float flipX = s.getFlipX();
        float flipY = s.getFlipY();
        boolean flip =  flipX != 1.0f || flipY != 1.0f;
        float rotate = s.getRotation();
        if (rotate != 0.0f || flip) {
            float cos = (float) Math.cos(rotate);
            float sin = (float) Math.sin(rotate);
            OEFloatArray matrix = new OEFloatArray(8);
            matrix.setItem(0, flipY * cos);   matrix.setItem(1, flipY * sin);    matrix.setItem(2, 0.0f);
            matrix.setItem(3, -flipX * sin);  matrix.setItem(4, flipX * cos);    matrix.setItem(5, 0.0f);
            matrix.setItem(6, 0.0f);          matrix.setItem(7, 0.0f);          matrix.setItem(8, 0.0f);

            oechem.OECenter(_mol);
            oechem.OERotate(_mol, matrix);
        }

        if (flip)
            oechem.OEMDLPerceiveBondStereo(_mol);
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
