package com.oschain.fastchaindb.blockchain.dto;

import java.io.Serializable;

public class FCDBAssetKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private String blockBody;
    private String blockKey1;
    private String blockKey2;
    private String blockKey3;
    private String blockKey4;
    private String blockKey5;


    public String getBlockBody() {
        return blockBody;
    }

    public void setBlockBody(String blockBody) {
        this.blockBody = blockBody;
    }

    public String getBlockKey1() {
        return blockKey1;
    }

    public void setBlockKey1(String blockKey1) {
        this.blockKey1 = blockKey1;
    }

    public String getBlockKey2() {
        return blockKey2;
    }

    public void setBlockKey2(String blockKey2) {
        this.blockKey2 = blockKey2;
    }

    public String getBlockKey3() {
        return blockKey3;
    }

    public void setBlockKey3(String blockKey3) {
        this.blockKey3 = blockKey3;
    }

    public String getBlockKey4() {
        return blockKey4;
    }

    public void setBlockKey4(String blockKey4) {
        this.blockKey4 = blockKey4;
    }

    public String getBlockKey5() {
        return blockKey5;
    }

    public void setBlockKey5(String blockKey5) {
        this.blockKey5 = blockKey5;
    }
}
