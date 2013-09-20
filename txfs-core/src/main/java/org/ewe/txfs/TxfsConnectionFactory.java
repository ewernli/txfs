package org.ewe.txfs;

import java.io.Serializable;

import javax.resource.Referenceable;

public interface TxfsConnectionFactory
        extends Referenceable, Serializable {

    public TxfsConnection getConnection() throws TxfsException;

    public TxfsConnectionRequestInfo getRequestInfos();
}
