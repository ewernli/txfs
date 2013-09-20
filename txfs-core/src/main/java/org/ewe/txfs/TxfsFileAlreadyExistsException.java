package org.ewe.txfs;

public class TxfsFileAlreadyExistsException
        extends TxfsException {

    public TxfsFileAlreadyExistsException() {
    }

    public TxfsFileAlreadyExistsException(String message) {
        super(message);
    }

    public TxfsFileAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TxfsFileAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
