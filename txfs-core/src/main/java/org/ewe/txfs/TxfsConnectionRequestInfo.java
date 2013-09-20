package org.ewe.txfs;

import java.io.Serializable;
import javax.resource.spi.ConnectionRequestInfo;

public class TxfsConnectionRequestInfo
        implements ConnectionRequestInfo, Serializable {

    private static final long serialVersionUID = 1L;
    private String rootPath;

    public TxfsConnectionRequestInfo(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TxfsConnectionRequestInfo other = (TxfsConnectionRequestInfo) obj;
        if ((this.rootPath == null) ? (other.rootPath != null) : !this.rootPath.equals(other.rootPath)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.rootPath != null ? this.rootPath.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return "Txfs connection request info [rootPath=" + rootPath + "]";
    }
}
