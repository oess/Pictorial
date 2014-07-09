/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OESuperAtomStyle;

public enum SuperAtomStyle {
    OFF(OESuperAtomStyle.Off, "OESuperAtomStyle::Off"),
    CARBON(OESuperAtomStyle.Carbon, "OESuperAtomStyle::Carbon"),
    OXYGEN(OESuperAtomStyle.Oxygen,"OESuperAtomStyle::Oxygen"),
    NITROGEN(OESuperAtomStyle.Nitrogen,"OESuperAtomStyle::Nitrogen"),
    OXYGEN_AND_NITROGEN(OESuperAtomStyle.OxygenAndNitrogen,"OESuperAtomStyle::OxygenAndNitrogen"),
    SULPHUR(OESuperAtomStyle.Sulfur,"OESuperAtomStyle::Sulfur"),
    PHOSPHORUS(OESuperAtomStyle.Phosphorus, "OESuperAtomStyle::Phosphorus"),
    HALIDES(OESuperAtomStyle.Halide, "OESuperAtomStyle::Halide"),
    ETHER(OESuperAtomStyle.Ether,"OESuperAtomStyle::Ether"),
    ALL(OESuperAtomStyle.All,"OESuperAtomStyle::All");

    private int _value;
    private String _source;
    private SuperAtomStyle(int val, String source) {
        _source = source;
        _value = val;
    }

    public int getValue() {
        return _value;
    }

    @Override
    public String toString() {
        return _source;
    }

}
