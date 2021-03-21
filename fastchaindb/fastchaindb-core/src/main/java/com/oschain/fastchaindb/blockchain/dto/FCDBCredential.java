package com.oschain.fastchaindb.blockchain.dto;

import java.io.Serializable;

public class FCDBCredential implements Serializable {
    /**
     * ID
     */
    private String accessId;
    /**
     * Key
     */
    private String accessKey;
    /**
     * 区块链版本
     */
    private String chainType="fabric";


    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }
}
