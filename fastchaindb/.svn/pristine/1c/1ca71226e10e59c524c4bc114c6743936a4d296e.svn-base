package com.oschain.fastchaindb.eth;

import com.oschain.fastchaindb.blockchain.core.AbstractFastChainDB;
import com.oschain.fastchaindb.blockchain.core.IFastChainDB;
import com.oschain.fastchaindb.blockchain.dto.FCDBAsset;
import com.oschain.fastchaindb.blockchain.dto.FCDBBlock;
import com.oschain.fastchaindb.blockchain.dto.FCDBPairKey;
import com.oschain.fastchaindb.blockchain.dto.FCDBTransationQuery;
import com.oschain.fastchaindb.eth.service.EthereumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EthereumInstance extends AbstractFastChainDB implements IFastChainDB {

    @Autowired
    EthereumService ethereumService;

    @Override
    public FCDBPairKey createPairKey() {
        return null;
    }

    @Override
    public String sendTransaction(FCDBAsset asset) throws Exception {
        return ethereumService.sendTransaction(asset.getTransactionData());
    }

    @Override
    public FCDBBlock getFCDBByTransactionId(String transactionId) throws Exception {
        return ethereumService.getTransactionByHash(transactionId);
    }

    @Override
    public FCDBBlock getFCDBByBlockHeight(Integer blockHeight) throws Exception {
        return ethereumService.getXChainByBlockHeight(blockHeight);
    }

    @Override
    public FCDBBlock getFCDBByBlockHash(String blockHash) throws Exception {
        return EthereumService.getBlockEthBlockByblockHash(blockHash);
    }

    @Override
    public String getAssetsWithLimit(String searchKey, int limit) throws Exception {
        return null;
    }

    @Override
    public long getFCDBBlockHeight() throws Exception {
        return 0;
    }

    @Override
    public List<FCDBBlock> getFCDBByTransactionData(FCDBTransationQuery transationQuery) throws Exception {
        return null;
    }

    //private static EthereumService ethereumService = SpringContextHolder.getBean(EthereumService.class);

}
