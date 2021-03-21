package com.oschain.fastchaindb.blockchain.dto;

import com.oschain.fastchaindb.client.common.utils.GsonUtil;

public class TransactionSendDTO extends PairKey{
    private static final long serialVersionUID = 1L;
    private Object data;
    private String transactionType;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setTransactionType(String transactionType){
        this.transactionType = transactionType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuilder stringBuilder=new StringBuilder();
        //{\"accessId\":1,\"accessKey\":2,\"data\":\"" + GsonUtil.gsonString(certFileBlockDTO) + "\"}"
        stringBuilder.append("{\"accessId\":\"").append(accessId)
                .append("\",\"accessKey\":\"").append(accessKey)
                .append("\",\"transactionType\":\"").append(transactionType)
                .append("\",\"transactionData\":").append(GsonUtil.gsonString(data)).append("}");

        return stringBuilder.toString();
    }

}
