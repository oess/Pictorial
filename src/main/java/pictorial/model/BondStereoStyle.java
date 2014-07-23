/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OEBondStereoStyle;

public enum BondStereoStyle {
    DEFAULT(OEBondStereoStyle.Default, "OEBondStereoStyle::Default"),
    CIPBONDSTEREO(OEBondStereoStyle.Display.CIPBondStereo, "OEBondStereoStyle::CIPBondStereo"),
    HIDDEN(OEBondStereoStyle.Hidden, "OEBondStereoStyle::Hidden"),
    ALL(OEBondStereoStyle.Display.All,  "OEBondStereoStyle::Display::CIPBondStereo + OEBondStereoStyle::Display::BondStereo");

    private int _value;
    private String _source;
    BondStereoStyle(int val, String source) {
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
