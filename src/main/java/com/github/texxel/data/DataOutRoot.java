package com.github.texxel.data;

import java.util.HashMap;

/**
 * The DataOutRoot is the class that contains a bunch of other DataOut classes. All DataOut classes belong to a single
 * DataOutRootClass. The DataOutRoot class is the only class that is able to convert from a Data structure to a PData
 * structure.
 */
public class DataOutRoot extends DataOut {

    public DataOutRoot() {
        super(new String[0], new PData(), new HashMap<Long, DataOut>(), new HashMap<Long, PData>(), null, 0);
    }

    /**
     * Gets a snapshot of this data and converts it into PData. Altering the returned data will have no effect on this
     * current structure
     * @return the primitive data structure
     */
    public PData toPrimitiveData() {
        return pData.copy();
    }

}
