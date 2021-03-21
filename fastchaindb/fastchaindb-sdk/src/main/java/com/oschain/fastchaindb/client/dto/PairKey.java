package com.oschain.fastchaindb.client.dto;

public class PairKey {

    /**
     * 公钥
     */
    private String accessId;
    /**
     * 私钥
     */
    private String accessKey;


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
}
