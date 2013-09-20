package org.ewe.txfs.impl.managed.tx;

import javax.transaction.xa.Xid;

public class DummyXidImpl
        implements Xid, java.io.Serializable {

    public static Xid LOCAL_XID = new DummyXidImpl();

    private DummyXidImpl() {
    }

    public int getFormatId() {
        return 0;
    }

    public byte[] getGlobalTransactionId() {
        return new byte[0];
    }

    public byte[] getBranchQualifier() {
        return new byte[0];
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "LocalXid";
    }
}
