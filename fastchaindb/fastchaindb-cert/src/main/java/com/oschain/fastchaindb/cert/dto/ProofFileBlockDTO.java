package com.oschain.fastchaindb.cert.dto;

import java.io.Serializable;

public class ProofFileBlockDTO  implements Serializable {

    private static final long serialVersionUID = 1L;

    //文件ID
    private String fileId;
    //区块链ID
    private String fileHash;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
