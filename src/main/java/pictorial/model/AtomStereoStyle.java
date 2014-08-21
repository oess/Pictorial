/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OEAtomStereoStyle;

public enum AtomStereoStyle {
    DEFAULT(OEAtomStereoStyle.Default, "OEAtomStereoStyle::Default"),
    CIPATOMSTEREO(OEAtomStereoStyle.Display.CIPAtomStereo, "OEAtomStereoStyle::Display::CIPAtomStereo"),
    HIDDEN(OEAtomStereoStyle.Hidden, "OEAtomStereoStyle::Hidden"),
    ALL(OEAtomStereoStyle.Display.All, "OEAtomStereoStyle::Display::CIPAtomStereo + OEAtomStereoStyle::Display::AtomStereo");

    private int _value;
    private String _source;
    AtomStereoStyle(int val, String source) {
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
