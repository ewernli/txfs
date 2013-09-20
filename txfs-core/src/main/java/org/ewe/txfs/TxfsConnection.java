package org.ewe.txfs;

import java.io.InputStream;

public interface TxfsConnection {

    public boolean isDirectory(String fileName)
            throws TxfsException;

    public long getLastModified(String fileName)
            throws TxfsException;

    public long getSize(String fileName)
            throws TxfsException;

    public void deleteDirectory(String path)
            throws TxfsException;

    public boolean exists(String fileName)
            throws TxfsException;

    public void deleteFile(String fileName)
            throws TxfsException;

    public void createFile(String destFileName, InputStream data)
            throws TxfsFileAlreadyExistsException, TxfsException;

    public InputStream getInputStream(String fileName)
            throws TxfsException;

    public void close()
            throws TxfsException;
}
