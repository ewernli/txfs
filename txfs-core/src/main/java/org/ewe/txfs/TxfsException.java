package org.ewe.txfs;

public class TxfsException
        extends Exception {

    public TxfsException() {
    }

    public TxfsException(String message) {
        super(message);
    }

    public TxfsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TxfsException(Throwable cause) {
        super(cause);
    }
}
