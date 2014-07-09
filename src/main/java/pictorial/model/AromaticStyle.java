/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OEAromaticStyle;

public enum AromaticStyle {
    KEKULE(OEAromaticStyle.Kekule, "OEAromaticStyle::Kekule"),
    CIRCLE(OEAromaticStyle.Circle, "OEAromaticStyle::Circle"),
    DASH(OEAromaticStyle.Dash, "OEAromaticStyle::Dash");

    private String _source;
    private int _value;
    private AromaticStyle(int val, String source) {
        _source =  source;
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
