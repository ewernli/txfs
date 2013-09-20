package org.ewe.txfs.impl.managed.tx;

import java.io.InputStream;
import org.ewe.txfs.TxfsConnectionRequestInfo;
import org.ewe.txfs.TxfsException;
import org.ewe.txfs.TxfsConnection;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import javax.resource.ResourceException;
import javax.resource.cci.LocalTransaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.ewe.txfs.TxfsFileAlreadyExistsException;

public class Delegate
        implements TxfsConnection, XAResource, LocalTransaction {

    private static final Logger log = Logger.getLogger(Delegate.class.getName());
    private TxfsConnectionRequestInfo info;

    public Delegate(TxfsConnectionRequestInfo info) {
        this.info = info;
    }

    public void close()
            throws TxfsException {
        // managed connectino doesn't forward the call to
        // close, it here just for the interface
    }
    //////
    private Xid currentTransaction;
    private Map<Xid, InTxDelegate> transactions = new HashMap<Xid, InTxDelegate>();

    protected boolean isInTransaction() {
        return currentTransaction != null;
    }

    protected TxfsConnection getDelegate() {
        if (isInTransaction()) {
            return transactions.get(currentTransaction);
        } else {
            return new NoTxDelegate(info);
        }
    }

    public void commit(Xid xid, boolean b)
            throws XAException {
        try {
            InTxDelegate work = transactions.get(xid);
            work.commitWork();
        } catch (TxfsException e) {
            e.printStackTrace();
            throw new XAException("Failed to prepare transaction. See the log for " +
                    "details. [xid=" + currentTransaction + "]");
        }
        transactions.remove(xid);
        if (log.isDebugEnabled()) {
            log.debug("commit [xid=" + formatXid(xid) + "]");
        }
    }

    public void end(Xid xid, int i)
            throws XAException {
        currentTransaction = null;
        if (log.isDebugEnabled()) {
            log.debug("end [xid=" + formatXid(xid) + "]");
        }
    }

    public void forget(Xid xid)
            throws XAException {
        transactions.remove(xid);
        if (log.isDebugEnabled()) {
            log.debug("forget [xid=" + formatXid(xid) + "]");
        }
    }

    public int getTransactionTimeout()
            throws XAException {
        if (log.isDebugEnabled()) {
            log.debug("getTransactionTimeout");
        }
        return 0;
    }

    public boolean isSameRM(XAResource xaResource)
            throws XAException {
        if (log.isDebugEnabled()) {
            log.debug("isSameRM [xa=" + xaResource + "]");
        }
        return false;
    }

    public int prepare(Xid xid)
            throws XAException {
        if (log.isDebugEnabled()) {
            log.debug("prepare [xid=" + formatXid(xid) + "]");
        }
        return 0;
    }

    public Xid[] recover(int i)
            throws XAException {
        if (log.isDebugEnabled()) {
            log.debug("recover");
        }
        return new Xid[0];
    }

    public void rollback(Xid xid)
            throws XAException {
        try {
            InTxDelegate work = transactions.get(xid);
            work.rollbackWork();
        } catch (TxfsException e) {
            e.printStackTrace();
            throw new XAException("Failed to rollback transaction. See log for details." +
                    " [xid=" + currentTransaction + "]");
        }

        transactions.remove(xid);
        if (log.isDebugEnabled()) {
            log.debug("rollback [xid=" + formatXid(xid) + "]");
        }
    }

    public boolean setTransactionTimeout(int i)
            throws XAException {
        return false;
    }

    public void start(Xid xid, int i)
            throws XAException {
        InTxDelegate work = new InTxDelegate(info, xid);
        transactions.put(xid, work);
        currentTransaction = xid;

        if (log.isDebugEnabled()) {
            log.debug("start [xid=" + formatXid(xid) + "]");
        }
    }

    public String formatXid(Xid xid) {
        if (xid == null) {
            return null;
        } else {
            return "[format=" + xid.getFormatId() + ",hashcode=" + xid.hashCode() + "]";
        }
    }

    ///////////////////////
    public void begin()
            throws ResourceException {
        try {
            start(DummyXidImpl.LOCAL_XID, 0);
        } catch (XAException e) {
            throw new ResourceException("Could not begin local transaction", e);
        }
    }

    public void commit()
            throws ResourceException {
        try {
            commit(DummyXidImpl.LOCAL_XID, true);
        } catch (XAException e) {
            throw new ResourceException("Could not commit local transaction", e);
        }
    }

    public void rollback()
            throws ResourceException {
        try {
            rollback(DummyXidImpl.LOCAL_XID);
        } catch (XAException e) {
            throw new ResourceException("Could not rollback local transaction", e);
        }
    }

    public void createFile(String destFileName, InputStream data) throws TxfsFileAlreadyExistsException, TxfsException {
        getDelegate().createFile(destFileName, data);
    }

    public void deleteDirectory(String path) throws TxfsException {
        getDelegate().deleteDirectory(path);
    }

    public void deleteFile(String fileName) throws TxfsException {
        getDelegate().deleteFile(fileName);
    }

    public boolean exists(String fileName) throws TxfsException {
        return getDelegate().exists(fileName);
    }

    public InputStream getInputStream(String fileName) throws TxfsException {
        return getDelegate().getInputStream(fileName);
    }

    public long getLastModified(String fileName) throws TxfsException {
        return getDelegate().getLastModified(fileName);
    }

    public long getSize(String fileName) throws TxfsException {
        return getDelegate().getSize(fileName);
    }

    public boolean isDirectory(String fileName) throws TxfsException {
        return getDelegate().isDirectory(fileName);
    }
    ///////////////////
}
