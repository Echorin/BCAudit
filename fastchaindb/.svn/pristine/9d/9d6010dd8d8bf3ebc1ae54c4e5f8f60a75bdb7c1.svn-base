package com.oschain.fastchaindb.client;

import com.oschain.fastchaindb.blockchain.dto.TransactionQueryDTO;
import com.oschain.fastchaindb.blockchain.dto.TransactionSendDTO;
import com.oschain.fastchaindb.blockchain.dto.BlockChain;
import com.oschain.fastchaindb.client.common.utils.HttpCallback;
import com.oschain.fastchaindb.client.dto.PairKey;
import com.oschain.fastchaindb.client.dto.ResultDTO;

public interface IFastChainDB {

    public PairKey createPairKey(String args);

    public ResultDTO sendTransaction(TransactionSendDTO transactionSendDTO);

    public ResultDTO<BlockChain> getChainByTransactionId(TransactionQueryDTO transactionQueryDTO);

    public void sendTransaction(String args,final HttpCallback callback);

    public ResultDTO getBlockHeight();


}
