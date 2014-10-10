/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import java.text.DecimalFormat;
import java.util.Hashtable;

public class Settings {
    private static final DecimalFormat df1place = new DecimalFormat("0.0");
    private int imageWidth = 750;
    private int imageHeight = 750;
    private int penSize = 2;
    private int fontSize = 40;
    private int redHighlight = 0x66;
    private int greenHighlight = 0x99;
    private int blueHighlight = 0x99;
    private float atomFontScale = 1.0f;
    private float rotation = 0.0f;
    private String imageName = "pictorial";
    private String smiles = "O=C(O)C(N)CC[S+](C)C[C@H]3O[C@@H](n2cnc1c(ncnc12)N)[C@H](O)[C@@H]3O";
    private String subSearchQuery = "";
    private String molTitle = "S-Adenosyl Methionine";
    private TitleLocation titleLocation = TitleLocation.TOP;
    private AromaticStyle aromaticStyle = AromaticStyle.KEKULE;
    private AtomStereoStyle atomStereoStyle = AtomStereoStyle.DEFAULT;
    private BondStereoStyle bondStereoStyle = BondStereoStyle.DEFAULT;
    private ColorStyle colorStyle = ColorStyle.WHITE_CPK;
    private HydrogenStyle hydrogenStyle = HydrogenStyle.DEFAULT;
    private SuperAtomStyle superAtomStyle = SuperAtomStyle.OFF;
    private HighlightStyle highlightStyle = HighlightStyle.COLOR;
    private float flipX = 0.0f;
    private float flipY = 0.0f;

    public float getFlipX() {
        return flipX;
    }

    public void setFlipX(boolean enable) {
        if (enable)
            this.flipX = -1.0f;
        else
            this.flipX = 1.0f;
    }

    public float getFlipY() {
        return flipY;
    }

    public void setFlipY(boolean enable) {
        if (enable)
            this.flipY = -1.0f;
        else
            this.flipY = 1.0f;
    }

    public SuperAtomStyle getSuperAtomStyle() {
        return superAtomStyle;
    }

    public void setSuperAtomStyle(SuperAtomStyle superAtomStyle) {
        this.superAtomStyle = superAtomStyle;
    }

    public static DecimalFormat getDecimalFormat() {
        return df1place;
    }

    public void setAtomFontScale(float atomFontScale) {
        this.atomFontScale = atomFontScale;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getPenSize() {
        return penSize;
    }

    public void setPenSize(int penSize) {
        this.penSize = penSize;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getRedHighlight() {
        return redHighlight;
    }

    public void setRedHighlight(int redHighlight) {
        this.redHighlight = redHighlight;
    }

    public int getGreenHighlight() {
        return greenHighlight;
    }

    public void setGreenHighlight(int greenHighlight) {
        this.greenHighlight = greenHighlight;
    }

    public int getBlueHighlight() {
        return blueHighlight;
    }

    public void setBlueHighlight(int blueHighlight) {
        this.blueHighlight = blueHighlight;
    }

    public float getAtomFontScale() {
        return atomFontScale;
    }

    public void setAtomFloatScale(float atomFloatScale) {
        this.atomFontScale = atomFloatScale;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        // escape any backslashes so generated code
        // will compile properly
        this.smiles = smiles.replace("\\", "\\\\");
    }

    public String getSubSearchQuery() {
        return subSearchQuery;
    }

    public void setSubSearchQuery(String subSearchQuery) {
        this.subSearchQuery = subSearchQuery;
    }

    public String getMolTitle() {
        return molTitle;
    }

    public void setMolTitle(String molTitle) {
        this.molTitle = molTitle;
    }

    public TitleLocation getTitleLocation() {
        return titleLocation;
    }

    public void setTitleLocation(TitleLocation titleLocation) {
        this.titleLocation = titleLocation;
    }

    public AromaticStyle getAromaticStyle() {
        return aromaticStyle;
    }

    public void setAromaticStyle(AromaticStyle aromaticStyle) {
        this.aromaticStyle = aromaticStyle;
    }

    public AtomStereoStyle getAtomStereoStyle() {
        return atomStereoStyle;
    }

    public void setAtomStereoStyle(AtomStereoStyle atomStereoStyle) {
        this.atomStereoStyle = atomStereoStyle;
    }

    public BondStereoStyle getBondStereoStyle() {
        return bondStereoStyle;
    }

    public void setBondStereoStyle(BondStereoStyle bondStereoStyle) {
        this.bondStereoStyle = bondStereoStyle;
    }

    public ColorStyle getColorStyle() {
        return colorStyle;
    }

    public void setColorStyle(ColorStyle colorStyle) {
        this.colorStyle = colorStyle;
    }

    public HighlightStyle getHiglightStyle() {
        return highlightStyle;
    }

    public void setHiglightStyle(HighlightStyle highlight) {
        this.highlightStyle = highlight;
    }

    public HydrogenStyle getHydrogenStyle() {
        return hydrogenStyle;
    }

    public void setHydrogenStyle(HydrogenStyle hydrogenStyle) {
        this.hydrogenStyle = hydrogenStyle;
    }

    public void setImageName(String name) {
        this.imageName = name.replace(' ', '_');
    }

    public String getImageName() {
        return imageName;
    }

    public interface LanguageFormat {
        public String format(String cppString);
    }

    public float getRotation() {
        return (float)Math.toRadians(rotation);
    }

    public void setRotation(float rotate) {
        this.rotation = rotate;
    }

    public Hashtable<String, String> getHashTable(LanguageFormat formatter) {
        Hashtable<String, String> ht = new Hashtable<>();
        ht.put("imageWidth", String.valueOf(imageWidth));
        ht.put("imageHeight", String.valueOf(imageHeight));
        ht.put("imageName", imageName);
        ht.put("penSize", String.valueOf(penSize));
        ht.put("fontSize", String.valueOf(fontSize));
        ht.put("redHighlight", String.valueOf(redHighlight));
        ht.put("greenHighlight", String.valueOf(greenHighlight));
        ht.put("blueHighlight", String.valueOf(blueHighlight));
        ht.put("atomFontScale", df1place.format(atomFontScale));
        ht.put("smiles", smiles);
        ht.put("substructure", subSearchQuery);
        ht.put("molTitle", molTitle);
        ht.put("titleLoc", formatter.format(titleLocation.toString()));
        ht.put("aromaticStyle", formatter.format(aromaticStyle.toString()));
        ht.put("atomStereoStyle", formatter.format(atomStereoStyle.toString()));
        ht.put("bondStereoStyle", formatter.format(bondStereoStyle.toString()));
        ht.put("colorStyle", formatter.format(colorStyle.toString()));
        ht.put("superAtomStyle", formatter.format(superAtomStyle.toString()));
        ht.put("hydrogenStyle", formatter.format(hydrogenStyle.toString()));
        ht.put("rotation", df1place.format(getRotation()));
        ht.put("highlightStyle", formatter.format(highlightStyle.toString()));
        ht.put("flipX", df1place.format(getFlipX()));
        ht.put("flipY", df1place.format(getFlipY()));

        return ht;
    }
}
