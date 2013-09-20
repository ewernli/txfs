package org.ewe.txfs.impl;

import org.ewe.txfs.impl.managed.TxfsManagedConnection;
import org.ewe.txfs.TxfsConnectionRequestInfo;
import org.ewe.txfs.TxfsException;
import org.ewe.txfs.TxfsConnection;
import org.apache.log4j.Logger;

import java.io.InputStream;
import org.ewe.txfs.TxfsFileAlreadyExistsException;

public class TxfsConnectionImpl
        implements TxfsConnection {

    private static final Logger log = Logger.getLogger(TxfsConnectionImpl.class.getName());
    private TxfsManagedConnection mc;
    private boolean valid = true;

    public TxfsConnectionImpl(TxfsManagedConnection mc, TxfsConnectionRequestInfo info)
            throws TxfsException {
        setManagedConnection(mc, info);
    }

    public void setManagedConnection(TxfsManagedConnection mc, TxfsConnectionRequestInfo info)
            throws TxfsException {
        this.mc = mc;
    }

    public TxfsManagedConnection getManagedConnection() {
        return mc;
    }

    public void invalidate()
            throws TxfsException {
        valid = false;
        if (log.isDebugEnabled()) {
            log.debug("Invalidate " + this);
        }
    }

    private void verifyValidity() throws TxfsException {
        if (valid == false) {
            throw new TxfsException("Connection handle is not valid");
        }
    }

    public void close()
            throws TxfsException {
        verifyValidity();

        if (log.isDebugEnabled()) {
            log.debug("close" + this);
        }

        mc.close(this);
    }

    public void deleteDirectory(String path)
            throws TxfsException {
        verifyValidity();

        mc.deleteDirectory(path);
    }

    public long getLastModified(String fileName)
            throws TxfsException {
        verifyValidity();

        return mc.getLastModified(fileName);
    }

    public long getSize(String fileName)
            throws TxfsException {
        verifyValidity();

        return mc.getSize(fileName);
    }

    public boolean isDirectory(String fileName)
            throws TxfsException {
        verifyValidity();

        return mc.isDirectory(fileName);
    }

    public void deleteFile(String fileName)
            throws TxfsException {
        verifyValidity();

        mc.deleteFile(fileName);
    }

    public boolean exists(String fileName)
            throws TxfsException {
        verifyValidity();

        return mc.exists(fileName);
    }

    public InputStream getInputStream(String fileName)
            throws TxfsException {
        verifyValidity();

        return mc.getInputStream(fileName);
    }

    public void createFile(String destFileName, InputStream data)
            throws TxfsFileAlreadyExistsException, TxfsException {
        verifyValidity();

        mc.createFile(destFileName, data);
    }
}
