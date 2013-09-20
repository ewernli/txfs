package org.ewe.txfs.impl;

import org.ewe.txfs.TxfsConnectionRequestInfo;
import org.ewe.txfs.TxfsConnection;
import org.ewe.txfs.TxfsConnectionFactory;
import org.apache.log4j.Logger;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;
import org.ewe.txfs.TxfsException;

public class TxfsConnectionFactoryImp
        implements TxfsConnectionFactory {

    private static final long serialVersionUID = 100001;
    private static Logger log = Logger.getLogger(TxfsConnectionFactoryImp.class.getName());
    private ConnectionManager manager;
    private ManagedConnectionFactory factory;
    private TxfsConnectionRequestInfo connectionInfo;
    private Reference reference;

    public TxfsConnectionFactoryImp(ConnectionManager manager, ManagedConnectionFactory factory, TxfsConnectionRequestInfo connectionInfo) {
        this.manager = manager;
        this.factory = factory;
        this.connectionInfo = connectionInfo;
    }

    public TxfsConnectionRequestInfo getRequestInfos() {
        return connectionInfo;
    }

    public TxfsConnection getConnection()
            throws TxfsException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("getConnection");
            }

            return (TxfsConnection) manager.allocateConnection(factory, connectionInfo);
        } catch (ResourceException e) {
            throw new TxfsException("Unable to get Connection: " + e);
        }
    }

    public void setReference(Reference reference) {
        if (log.isDebugEnabled()) {
            log.debug("setReference " + this + " reference=" + reference, new Exception("CalledBy:"));
        }
        this.reference = reference;
    }

    public Reference getReference()
            throws NamingException {
        if (log.isDebugEnabled()) {
            log.debug("getReference " + this);
        }
        return reference;
    }
}
