package com.oschain.fastchaindb.blockchain.dto;

import com.oschain.fastchaindb.client.common.utils.GsonUtil;

public class TransactionQueryDTO extends PairKey {
    private static final long serialVersionUID = 1L;

    private String transactionId;
    private String transactionData;
    private String transactionType;
    private long blockHeight;
    private String blockHash;

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(String transactionData) {
        this.transactionData = transactionData;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(long blockHeight) {
        this.blockHeight = blockHeight;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    @Override
    public String toString() {
        return GsonUtil.gsonString(this);
    }
}
