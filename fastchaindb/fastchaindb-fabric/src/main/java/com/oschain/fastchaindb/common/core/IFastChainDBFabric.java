package com.oschain.fastchaindb.common.core;

import com.hyperledger.fabric.sdk.entity.dto.api.PeerNodeDTO;
import com.oschain.fastchaindb.blockchain.core.IFastChainDB;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

public interface IFastChainDBFabric extends IFastChainDB {

    //创建通道
    public void createChannel(String channelName) throws Exception;

    //加入通道
    public void jionChannel(HFClient client, Channel channel, PeerNodeDTO peerNodeDTO1) throws Exception;

    //安装合约
    public void installChaincode(HFClient client, String chaincodeName) throws Exception;

    //初始化合约
    public void initChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception;

    //升级合约
    public void upgradeChaincode(HFClient client,String chaincodeName) throws Exception;

    //智能合约插入数据
    public void invokeChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception;

    //智能合约查询数据
    public void queryChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception;

}
