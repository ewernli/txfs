package org.ewe.txfs.impl.managed;

import org.ewe.txfs.impl.*;
import org.ewe.txfs.TxfsConnectionRequestInfo;
import org.apache.log4j.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Set;

public class TxfsManagedConnectionFactory
        implements ManagedConnectionFactory, Serializable {

    private static final long serialVersionUID = 100000;
    private static Logger log = Logger.getLogger(TxfsManagedConnectionFactory.class.getName());
    private String rootPath;

    public TxfsManagedConnectionFactory() {
        if (log.isDebugEnabled()) {
            log.debug("ctor FsManagedConnectionFactory " + this);
        }
    }

    public Object createConnectionFactory()
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("createConnectionFactory");
        }
        throw new UnsupportedOperationException("Cannot be used in unmanaed env");
    }

    public Object createConnectionFactory(ConnectionManager cm)
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("createConnectionFactory, cm=" + cm);
        }

        TxfsConnectionRequestInfo info = new TxfsConnectionRequestInfo(rootPath);
        return new TxfsConnectionFactoryImp(cm, this, info);
    }

    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo info)
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("createManagedConnection, subject=" + subject + ", fsInfo=" + info);
        }
        TxfsConnectionRequestInfo txfsInfo = (TxfsConnectionRequestInfo) info;
        return new TxfsManagedConnection(subject, txfsInfo);
    }

    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo info)
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("matchManagedConnections, connectionSet=" + connectionSet + ", subject=" + subject + ", fsInfo=" + info);
        }

        // info should be a TxfsConnectionRequestInfo, if not, no match found
        if ((info != null) && !(info instanceof TxfsConnectionRequestInfo)) {
            return null;
        }

        for (ManagedConnection mc : (Set<ManagedConnection>) connectionSet) {
            if ((mc instanceof TxfsManagedConnection) &&
                    ((TxfsManagedConnection) mc).getRequestInfo().equals(info)) {
                if (log.isDebugEnabled()) {
                    log.debug("matchManagedConnections: match found!");
                }
                return mc;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("matchManagedConnections: no match found");
        }
        return null;
    }

    public PrintWriter getLogWriter()
            throws ResourceException {
        return null;
    }

    public void setLogWriter(PrintWriter out)
            throws ResourceException {
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
