/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OEAtomColorStyle;

public enum ColorStyle {
    WHITE_CPK(OEAtomColorStyle.WhiteCPK, "OEAtomColorStyle::WhiteCPK"),
    WHITE_MONOCHROME(OEAtomColorStyle.WhiteMonochrome, "OEAtomColorStyle::WhiteMonochrome"),
    BLACK_CPK(OEAtomColorStyle.BlackCPK, "OEAtomColorStyle::BlackCPK"),
    BLACK_MONOTONE(OEAtomColorStyle.BlackMonochrome, "OEAtomColorStyle::BlackMonochrome");

    private int _value;
    private String _source;
    private ColorStyle(int val, String source) {
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
