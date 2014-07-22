/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OEHighlightStyle;

public enum HighlightStyle {
    COLOR(OEHighlightStyle.Color, "OEHighlightStyle::Color"),
    STICK(OEHighlightStyle.Stick, "OEHighlightStyle::Stick"),
    BALL_AND_STICK(OEHighlightStyle.BallAndStick, "OEHighlightStyle::BallAndStick"),
    COGWHEEL(OEHighlightStyle.Cogwheel, "OEHighlightStyle::Cogwheel");

    private int _value;
    private String _source;
    private HighlightStyle(int val, String source) {
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
