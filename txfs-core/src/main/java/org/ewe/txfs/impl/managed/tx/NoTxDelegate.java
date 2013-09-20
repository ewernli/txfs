package org.ewe.txfs.impl.managed.tx;

import org.ewe.txfs.TxfsConnectionRequestInfo;
import org.ewe.txfs.TxfsException;
import org.ewe.txfs.TxfsConnection;
import org.apache.log4j.Logger;

import java.io.*;
import org.apache.commons.io.IOUtils;
import org.ewe.txfs.TxfsFileAlreadyExistsException;

public class NoTxDelegate implements TxfsConnection {

    private static final Logger log = Logger.getLogger(NoTxDelegate.class.getName());
    protected TxfsConnectionRequestInfo info;

    public NoTxDelegate(TxfsConnectionRequestInfo info) {
        this.info = info;
    }

    public boolean exists(String fileName)
            throws TxfsException {
        return new File(info.getRootPath(), fileName).exists();
    }

    public void deleteFile(String fileName)
            throws TxfsException {
        doDeleteFile(fileName);
    }

    public void createFile(String destFileName, InputStream data)
            throws TxfsFileAlreadyExistsException, TxfsException {

        if (log.isDebugEnabled()) {
            log.debug("Create new file " + this);
        }
        File f = new File(info.getRootPath(), destFileName);
        if (f.exists()) {
            throw new TxfsFileAlreadyExistsException("Error creating in file (file already exist):" + destFileName);
        }

        try {


            try {

                File dir = f.getParentFile();
                if (!f.exists()) {
                    // make dire is not always working, there is bug in JDK
                    dir.mkdirs();
                }

                IOUtils.copy(data,
                        new FileOutputStream(f));

            } finally {
                IOUtils.closeQuietly(data);
            }
        } catch (IOException e) {
            throw new TxfsException("Error writing in file:" + destFileName, e);
        }

    }

    public InputStream getInputStream(String fileName)
            throws TxfsException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(new File(info.getRootPath(), fileName));
        } catch (FileNotFoundException e) {
            throw new TxfsException("Error reading file (file not found):" + fileName, e);
        }
        bis = new BufferedInputStream(fis);
        return bis;
    }

    public OutputStream getOutputStream(String filename)
            throws TxfsException {
        return null;
    }

    public boolean isDirectory(String fileName)
            throws TxfsException {
        return new File(info.getRootPath(), fileName).isDirectory();
    }

    public long getLastModified(String fileName)
            throws TxfsException {
        return new File(info.getRootPath(), fileName).lastModified();
    }

    public long getSize(String fileName)
            throws TxfsException {
        return new File(info.getRootPath(), fileName).length();
    }

    public void deleteDirectory(String fileName)
            throws TxfsException {

        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new File(info.getRootPath(), fileName));
        } catch (IOException e) {
            throw new TxfsException("Error deleting directory: " + fileName, e);
        }

    }

    public void close()
            throws TxfsException {
        if (log.isDebugEnabled()) {
            log.debug("FsDelegate close");
        }
    }

    public void cleanup()
            throws TxfsException {
        if (log.isDebugEnabled()) {
            log.debug("FsDelegate cleanup");
        }
    }

    // For transactions
    public void doDeleteFile(String fileName)
            throws TxfsException {
        File f = new File(info.getRootPath(), fileName);
        if (!f.delete()) {
            throw new TxfsException("Error deleting file:" + fileName);
        }
    }

    public boolean doExistsFile(String fileName)
            throws TxfsException {
        return new File(info.getRootPath(), fileName).exists();
    }
}
