package com.oschain.fastchaindb.blockchain.dto;

public class FCDBAsset extends FCDBPairKey {

    private String id;
    private String transactionData;
    private String transactionType;
    private String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(String transactionData) {
        this.transactionData = transactionData;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getData() { return data; }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
