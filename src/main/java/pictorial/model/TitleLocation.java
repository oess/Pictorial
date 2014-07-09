/****************************************************************************
 * Copyright 2013 OpenEye Scientific Software, Inc.
 *****************************************************************************/

package pictorial.model;

import openeye.oedepict.OETitleLocation;

public enum TitleLocation {
    HIDDEN(OETitleLocation.Hidden, "OETitleLocation::Hidden"),
    TOP(OETitleLocation.Top, "OETitleLocation::Top"),
    BOTTOM(OETitleLocation.Bottom, "OETitleLocation::Bottom");

    private int _value;
    String _source;
    TitleLocation(int val, String source) {
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
