package org.ewe.txfs.impl.managed;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionMetaData;

public class TxfsManagedConnectionMetaData
        implements ManagedConnectionMetaData {

    public TxfsManagedConnectionMetaData() {
    }

    public String getEISProductName()
            throws ResourceException {
        return "Txfs adapter";
    }

    public String getEISProductVersion()
            throws ResourceException {
        return "0.1";
    }

    public int getMaxConnections()
            throws ResourceException {
        return 0;    // unknown, not specified
    }

    public String getUserName()
            throws ResourceException {
        return "";
    }
}
