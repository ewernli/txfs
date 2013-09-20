package org.ewe.txfs.impl.managed;

import org.ewe.txfs.impl.managed.tx.Delegate;
import java.io.File;
import org.ewe.txfs.impl.*;
import org.ewe.txfs.TxfsConnectionRequestInfo;
import org.ewe.txfs.TxfsException;
import org.apache.log4j.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.*;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.ewe.txfs.TxfsFileAlreadyExistsException;

public class TxfsManagedConnection
        implements ManagedConnection {

    private static Logger log = Logger.getLogger(TxfsManagedConnection.class.getName());
    private ArrayList<ConnectionEventListener> listeners = new ArrayList<ConnectionEventListener>();
    private TxfsConnectionRequestInfo info;
    private List<TxfsConnectionImpl> handles = new ArrayList<TxfsConnectionImpl>();
    private Delegate delegate = null;

    /**
     * Creates new WebdavManagedConnection.
     */
    public TxfsManagedConnection(Subject subject, TxfsConnectionRequestInfo connectionInfo) throws ResourceException {
        info = connectionInfo;
        verifyRequestInfo(info);
        delegate = getDelegate();
    }

    public TxfsConnectionRequestInfo getRequestInfo() {
        return info;
    }

    private void verifyRequestInfo(TxfsConnectionRequestInfo info) throws ResourceException {

        String rootPath = info.getRootPath();

        File dir = new File(rootPath);

        if ((rootPath == null) ||
                (rootPath.length() == 0) ||
                !dir.exists()) {
            throw new ResourceException(
                    "The root path is either not set" +
                    " or does not exist on file system");
        }
    }

    public Delegate getDelegate()
            throws ResourceException {
        if (delegate == null) {
            delegate = new Delegate(info);
            if (log.isDebugEnabled()) {
                log.debug("Delegate created [managedConn=" + this + "]");
            }
        }
        return delegate;
    }

    public void addConnectionEventListener(ConnectionEventListener connectionEventListener) {
        if (log.isDebugEnabled()) {
            log.debug("addConnectionEventListener, listener=" + connectionEventListener);
        }
        listeners.add(connectionEventListener);
    }

    public void removeConnectionEventListener(ConnectionEventListener connectionEventListener) {
        if (log.isDebugEnabled()) {
            log.debug("removeConnectionEventListener, listener=" + connectionEventListener);
        }
        listeners.remove(connectionEventListener);
    }

    public void associateConnection(Object obj)
            throws ResourceException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("associateConnection, obj=" + obj);
            }
            TxfsConnectionImpl conn = (TxfsConnectionImpl) obj;
            conn.setManagedConnection(this, info);
            handles.add(conn);

        } catch (TxfsException e) {
            throw new ResourceException("Can't associate connection.", e);
        }
    }

    // Application server calls this method to force any cleanup on the ManagedConnection instance.
    // Cleans up connection handles so they can't be used again But the physical connection is kept open 
    public void cleanup()
            throws ResourceException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("cleanup");
            }

            // nothing to clean in delegate
            // delegate.cleanup ();

            for (TxfsConnectionImpl conn : handles) {
                conn.invalidate();
            }

            handles.clear();
        } catch (TxfsException e) {
            throw new ResourceException("Could not cleanup managed connection", e);
        }
    }

    public void destroy()
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("destroy");
        }
    }

    public Object getConnection(Subject subject, ConnectionRequestInfo connectionInfo)
            throws ResourceException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("getConnection, subject=" + subject + ", Info=" + connectionInfo);
            }

            // we use the info of the managed connection, not the one in
            // parameter (should be the same anyway)
            TxfsConnectionImpl conn = new TxfsConnectionImpl(this, info);

            handles.add(conn);

            return conn;
        } catch (TxfsException e) {
            log.error("Error getting connection", e);
            throw new ResourceException("Can't get a connection.", e);
        }
    }

    public LocalTransaction getLocalTransaction()
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("getLocalTransaction");
        }
        return null;
    }

    public ManagedConnectionMetaData getMetaData()
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("getMetaData");
        }
        return new TxfsManagedConnectionMetaData();
    }

    public XAResource getXAResource()
            throws ResourceException {
        if (log.isDebugEnabled()) {
            log.debug("getXAResource [managedConn=" + this + "]");
        }

        return getDelegate();
    }

    public PrintWriter getLogWriter()
            throws ResourceException {
        return null;
    }

    public void setLogWriter(PrintWriter out)
            throws ResourceException {
    }

    // Notifies the app. server that a connection handle has been closed
    public void close(TxfsConnectionImpl conn) {
        if (log.isDebugEnabled()) {
            log.debug("Close connection " + this + " handle [handle=" + conn + ", managedConn=" + this + "]");
        }
        ConnectionEvent ce = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        ce.setConnectionHandle(conn);
        fireConnectionEvent(ce);
        handles.remove(conn);

    }

    protected void fireConnectionEvent(ConnectionEvent evt) {
        for (int i = listeners.size() - 1; i >= 0; i--) {
            ConnectionEventListener listener = listeners.get(i);
            if (evt.getId() == ConnectionEvent.CONNECTION_CLOSED) {
                listener.connectionClosed(evt);
            } else if (evt.getId() == ConnectionEvent.CONNECTION_ERROR_OCCURRED) {
                listener.connectionErrorOccurred(evt);
            }
        }
    }

    public void deleteDirectory(String path)
            throws TxfsException {
        delegate.deleteDirectory(path);
    }

    public long getLastModified(String fileName)
            throws TxfsException {
        return delegate.getLastModified(fileName);
    }

    public long getSize(String fileName)
            throws TxfsException {
        return delegate.getSize(fileName);
    }

    public boolean isDirectory(String fileName)
            throws TxfsException {
        return delegate.isDirectory(fileName);
    }

    public void deleteFile(String fileName)
            throws TxfsException {
        delegate.deleteFile(fileName);
    }

    public boolean exists(String fileName)
            throws TxfsException {
        return delegate.exists(fileName);
    }

    public InputStream getInputStream(String fileName)
            throws TxfsException {
        return delegate.getInputStream(fileName);
    }

    public void createFile(String destFileName, InputStream data)
            throws TxfsFileAlreadyExistsException, TxfsException {
        delegate.createFile(destFileName, data);

    }
}
