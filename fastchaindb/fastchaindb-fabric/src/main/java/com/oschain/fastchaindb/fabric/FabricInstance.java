package com.oschain.fastchaindb.fabric;

import com.hyperledger.fabric.sdk.entity.dto.api.PeerNodeDTO;
import com.oschain.fastchaindb.blockchain.core.AbstractFastChainDB;
import com.oschain.fastchaindb.blockchain.dto.*;
import com.oschain.fastchaindb.common.core.IFastChainDBFabric;
import com.oschain.fastchaindb.fabric.service.FabricService;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class FabricInstance extends AbstractFastChainDB implements IFastChainDBFabric {

    //private static FabricService fabricService = SpringContextHolder.getBean(FabricService.class);

    @Autowired
    FabricService fabricService;

    @Override
    public void createChannel(String channelName) throws Exception {
        fabricService.createChannel(channelName);
    }

    @Override
    public void jionChannel(HFClient client, Channel channel, PeerNodeDTO peerNodeDTO1) throws Exception {
        fabricService.jionChannel();
    }

    @Override
    public void installChaincode(HFClient client, String chaincodeName) throws Exception {
        fabricService.installChaincode(chaincodeName);
    }

    @Override
    public void initChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception {
        fabricService.initChaincode(client, channel, chaincodeName, str);
    }

    @Override
    public void upgradeChaincode(HFClient client, String chaincodeName) throws Exception {
        fabricService.upgradeChaincode(chaincodeName);
    }

    @Override
    public void invokeChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception {
        fabricService.invokeChaincode(chaincodeName, str);
    }

    @Override
    public void queryChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception {
        fabricService.queryChaincode(chaincodeName, str);
    }

    @Override
    public FCDBPairKey createPairKey() {
        return null;
    }

    @Override
    public String sendTransaction(FCDBAsset asset) throws Exception {
        return fabricService.sendTransactionProposal(asset).getTransactionId();
    }

    @Override
    public FCDBBlock getFCDBByTransactionId(String transationId) throws Exception {

        FCDBTransationQuery  transationQuery =new FCDBTransationQuery();
        transationQuery.setTransactionId(transationId);
        return  fabricService.getXChainByTransactionId(transationQuery);
    }

    @Override
    public FCDBBlock getFCDBByBlockHeight(Integer transationQuery) throws Exception {
        return null;
    }

    @Override
    public FCDBBlock getFCDBByBlockHash(String blockHash) throws Exception {
        return null;
    }

    @Override
    public String getAssetsWithLimit(String searchKey, int limit) throws Exception {
        return null;
    }

    @Override
    public long getFCDBBlockHeight() throws Exception {
        return fabricService.getBlockHeight();
    }

    @Override
    public List<FCDBBlock> getFCDBByTransactionData(FCDBTransationQuery transationQuery) throws Exception {
        return fabricService.getXChainByTransactionData(transationQuery);
    }

}
