package org.ewe.txfs.impl.managed.tx;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.transaction.xa.Xid;
import org.apache.log4j.Logger;
import org.ewe.txfs.TxfsConnectionRequestInfo;
import org.ewe.txfs.TxfsException;
import org.ewe.txfs.TxfsFileAlreadyExistsException;

public class InTxDelegate extends NoTxDelegate {

    private static final Logger log = Logger.getLogger(InTxDelegate.class.getName());
    private Set<String> deletedFiles = new HashSet<String>();
    private Set<String> createdFiles = new HashSet<String>();
    private Xid xid;

    public InTxDelegate(TxfsConnectionRequestInfo info, Xid xid) {
        super(info);
        this.xid = xid;
    }

    public void addDeletedFile(String fileName) {
        deletedFiles.add(fileName);
        if (log.isDebugEnabled()) {
            log.debug("Added '" + fileName + " to the list of file to be deleted [xid=" + formatXid(xid) + "]");
        }
    }

    public void addCreatedFile(String fileName) {
        createdFiles.add(fileName);
        if (log.isDebugEnabled()) {
            log.debug("Added '" + fileName + " to the list of created files [xid=" + formatXid(xid) + "]");
        }
    }

    ///////////////////////////////
    @Override
    public boolean exists(String fileName)
            throws TxfsException {
        if (deletedFiles.contains(fileName)) {
            return false;
        } else {
            return super.exists(fileName);
        }
    }

    @Override
    public void deleteFile(String fileName)
            throws TxfsException {
        addDeletedFile(fileName);
    }

    @Override
    public void createFile(String destFileName, InputStream data)
            throws TxfsFileAlreadyExistsException, TxfsException {

        // I can not use a finally block because
        // if failure happens because file already existed
        // I must not add it the list

        try {
            super.createFile(destFileName, data);
            // we add the file if creation succeeds
            addCreatedFile(destFileName);
        } catch (TxfsFileAlreadyExistsException e) {
            // we don't add the file if it already exists
            throw e;
        } catch (Throwable e) {
            // we add the file in any other case
            addCreatedFile(destFileName);
        }

    }

    @Override
    public InputStream getInputStream(String fileName)
            throws TxfsException {

        if (deletedFiles.contains(fileName)) {
            throw new TxfsException("File '" + fileName + "' was deleted in transaction");
        } else {
            return super.getInputStream(fileName);
        }
    }

    ////////////////////
    public void commitWork() throws TxfsException {
        for (String s : deletedFiles) {
            if (log.isDebugEnabled()) {
                log.debug("Commit xid=" + formatXid(xid) + " and remove deleted file '" + s + "'");
            }
            doDeleteFile(s);
        }
    }

    public void rollbackWork() throws TxfsException {
        for (String s : createdFiles) {
            // It may happen that file was added to list but not written in case
            // of error in createFile.
            if (doExistsFile(s)) {
                log.info("Rollback xid=" + formatXid(xid) + " and remove new file '" + s + "'");
                doDeleteFile(s);
            } else {
                log.info("Rollback xid=" + formatXid(xid) + ", file '" + s + "' does not exists");
            }
        }
    }

    public String formatXid(Xid xid) {
        if (xid == null) {
            return null;
        } else {
            return "[format=" + xid.getFormatId() + ",hashcode=" + xid.hashCode() + "]";
        }
    }
}
