package com.oschain.fastchaindb.blockchain.core;

public class AbstractFastChainDB {
    /**
     * 公钥
     */
    private String publicKey;
    /**
     * 私钥
     */
    private String privateKey;

    public AbstractFastChainDB(){
    }

    public AbstractFastChainDB(String publicKey, String privateKey){
        this.publicKey=publicKey;
        this.privateKey=privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
