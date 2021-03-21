package com.oschain.fastchaindb.blockchain.core;

import com.oschain.fastchaindb.blockchain.dto.*;

import java.util.HashMap;
import java.util.List;

public interface IFastChainDB {

    /**
     * 创建密钥
     * @return
     */
    public FCDBPairKey createPairKey();

    /**
     * 创建区块
     * @param asset
     * @return
     */
    public String sendTransaction(FCDBAsset asset) throws Exception;

    /**
     * 创建区块
     * @param asset
     * @return
     */
    public FCDBBlock getFCDBByTransactionId(String transactionId) throws Exception;


    /**
     * 查询块
     * @param height
     * @return
     */
    public FCDBBlock getFCDBByBlockHeight(Integer blockHeight) throws Exception;

    /**
     * 查询块
     * @param height
     * @return
     */
    public FCDBBlock getFCDBByBlockHash(String blockHash) throws Exception;


    /**
     * 查询资产,限制条数
     * @param param
     * @param limit
     * @return
     */
    public String getAssetsWithLimit(String searchKey,int limit) throws Exception;

    /**
     * 查询块高度
     * @param height
     * @return
     */
    public long getFCDBBlockHeight() throws Exception;


    /**
     * 创建区块
     * @param asset
     * @return
     */
    public List<FCDBBlock> getFCDBByTransactionData(FCDBTransationQuery transationQuery) throws Exception;


}
