/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import java.text.DecimalFormat;
import java.util.Hashtable;

// TODO: these could be properties?
public class Settings {
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private int imageWidth = 600;
    private int imageHeight = 600;
    private int penSize = 2;
    private int fontSize = 40;
    private float redHighlight = 1.0f;
    private float greenHighlight = 0.0f;
    private float blueHighlight = 0.0f;
    private float atomFontScale = 1.0f;
    private boolean stripSalts = false;
    private String imageName;
    private String smiles = "";
    private String subSearchQuery;
    private String molTitle;
    private TitleLocation titleLocation;
    private AromaticStyle aromaticStyle;
    private AtomStereoStyle atomStereoStyle;
    private BondStereoStyle bondStereoStyle;
    private ColorStyle colorStyle;
    private HydrogenStyle hydrogenStyle;
    private SuperAtomStyle superAtomStyle;

    public SuperAtomStyle getSuperAtomStyle() {
        return superAtomStyle;
    }

    public void setSuperAtomStyle(SuperAtomStyle superAtomStyle) {
        this.superAtomStyle = superAtomStyle;
    }

    public static DecimalFormat getDecimalFormat() {
        return decimalFormat;
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

    public float getRedHighlight() {
        return redHighlight;
    }

    public void setRedHighlight(float redHighlight) {
        this.redHighlight = redHighlight;
    }

    public float getGreenHighlight() {
        return greenHighlight;
    }

    public void setGreenHighlight(float greenHighlight) {
        this.greenHighlight = greenHighlight;
    }

    public float getBlueHighlight() {
        return blueHighlight;
    }

    public void setBlueHighlight(float blueHighlight) {
        this.blueHighlight = blueHighlight;
    }

    public float getAtomFontScale() {
        return atomFontScale;
    }

    public void setAtomFloatScale(float atomFloatScale) {
        this.atomFontScale = atomFloatScale;
    }

    public boolean isStripSalts() {
        return stripSalts;
    }

    public void setStripSalts(boolean stripSalts) {
        this.stripSalts = stripSalts;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
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

    public HydrogenStyle getHydrogenStyle() {
        return hydrogenStyle;
    }

    public void setHydrogenStyle(HydrogenStyle hydrogenStyle) {
        this.hydrogenStyle = hydrogenStyle;
    }

    public void setImageName(String name) {
        this.imageName = name;
    }

    public String getImageName() {
        return imageName;
    }

    public interface LanguageFormat {
        public String format(String cppString);
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
        ht.put("atomFontScale", decimalFormat.format(atomFontScale));
        ht.put("stripSalts", formatter.format(String.valueOf(stripSalts)));
        ht.put("smiles", smiles);
        ht.put("substructure", subSearchQuery);
        ht.put("molTitle", molTitle);
        ht.put("titleLoc", formatter.format(titleLocation.toString()));
        ht.put("aromaticStyle", formatter.format(titleLocation.toString()));
        ht.put("atomStereoStyle", formatter.format(atomStereoStyle.toString()));
        ht.put("bondStereoStyle", formatter.format(bondStereoStyle.toString()));
        ht.put("colorStyle", formatter.format(hydrogenStyle.toString()));
        ht.put("superAtomStyle", formatter.format(superAtomStyle.toString()));
        ht.put("hydrogenStyle", formatter.format(hydrogenStyle.toString()));

        return ht;
    }
}
