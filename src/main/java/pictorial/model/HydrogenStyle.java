/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OEHydrogenStyle;

public enum HydrogenStyle {
    DEFAULT(OEHydrogenStyle.Default, "OEHydrogenStyle::Default"),
    EXPLICIT_ALL(OEHydrogenStyle.ExplicitAll, "OEHydrogenStyle::ExplicitAll"),
    EXPLICT_HETERO(OEHydrogenStyle.ExplicitHetero, "OEHydrogenStyle::ExplicitHetero"),
    EXPLICIT_TERMINAL(OEHydrogenStyle.ExplicitTerminal, "OEHydrogenStyle::ExplicitTerminal"),
    IMPLICIT_ALL(OEHydrogenStyle.ImplicitAll, "OEHydrogenStyle::ImplicitAll"),
    IMPLICIT_HETERO(OEHydrogenStyle.ImplicitHetero, "OEHydrogenStyle::ImplicitHetero"),
    IMPLICIT_TERMINAL(OEHydrogenStyle.ImplicitTerminal, "OEHydrogenStyle::ImplicitTerminal"),
    HIDDEN(OEHydrogenStyle.Hidden, "OEHydrogenStyle::Hidden");

    private int _value;
    private String _source;
    private HydrogenStyle(int val, String source) {
        _value = val;
        _source = source;
    }

    public int getValue() {
        return _value;
    }

    @Override
    public String toString() {
        return _source;
    }
}
