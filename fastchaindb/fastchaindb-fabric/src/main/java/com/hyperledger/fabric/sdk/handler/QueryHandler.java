package com.hyperledger.fabric.sdk.handler;

import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;

import java.text.SimpleDateFormat;

import static com.hyperledger.fabric.sdk.logger.Logger.info;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

/**
 * Created by Kevin on 2018-09-04 15:54
 *
 * 链码区块|账本信息查询
 *
 * 单元测试类: BlockChainTest
 */
public class QueryHandler {



    /**
     * 查询通道中区块信息
     * @param channel 通道对象
     * @throws Exception e
     * */
    public static void queryBlockChain(Channel channel) throws Exception {
        SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        BlockchainInfo blockchainInfo = channel.queryBlockchainInfo();
        System.out.println(blockchainInfo.getHeight());

        /* 块号索引为0的为创世纪块 */
        for (long blockNum = 0; blockNum < blockchainInfo.getHeight(); blockNum++) {
            info("==================== Block Number: %s ====================", blockNum);
            BlockInfo blockInfo = channel.queryBlockByNumber(blockNum);

            byte[] currentDataHash = blockInfo.getDataHash();
            info("current block data hash: %s.", Hex.encodeHexString(currentDataHash));
            info("previous block hash: %s.", Hex.encodeHexString(blockInfo.getPreviousHash()));
            info("blockNumber: %d, envelopeCount: %d, channelId: %s.", blockInfo.getBlockNumber(), blockInfo.getEnvelopeCount(), blockInfo.getChannelId());

            /* EnvelopeInfo对象中存储的是具体的交易信息 */
            for (BlockInfo.EnvelopeInfo envelopeInfo: blockInfo.getEnvelopeInfos()) {
                info("----------------------- 【Envelope】 ------------------------");
                info("EnvelopeInfo -> ChannelId: %s.", envelopeInfo.getChannelId());
                info("EnvelopeInfo -> TransactionID: %s.", envelopeInfo.getTransactionID());
                info("EnvelopeInfo -> Type: %s.", envelopeInfo.getType().name());
                info("EnvelopeInfo -> ValidationCode: %s.", String.valueOf(envelopeInfo.getValidationCode()));
                info("EnvelopeInfo -> Timestamp: %s.", SDF.format(envelopeInfo.getTimestamp()));
                info("EnvelopeInfo -> Epoch: %s.", envelopeInfo.getEpoch());

                /* TRANSACTION_ENVELOPE: 交易块, ENVELOPE: 创世纪块 */
                if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
                    BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;

                    for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
                        info("背书节点个数: %d.", transactionActionInfo.getEndorsementsCount());

                        /* 背书节点信息 */
                        info("----------------------- 【背书节点信息】  -----------------------");
                        for (int i = 0; i < transactionActionInfo.getEndorsementsCount(); i++) {
                            BlockInfo.EndorserInfo endorserInfo = transactionActionInfo.getEndorsementInfo(i);
                            info("背书节点: %d, 签名密钥: %s.", i, Hex.encodeHexString(endorserInfo.getSignature()));
                            info("背书节点: %d, 签名的证书信息: %s", i, new String(endorserInfo.getEndorser(), UTF_8));
                        }

                        /* 交易传入参数信息 */
                        info("----------------------- 【交易传入参数信息】  -----------------------");
                        for (int i = 0; i < transactionActionInfo.getChaincodeInputArgsCount(); i++) {
                            info("参数索引: %d, 参数值: %s", i, new String(transactionActionInfo.getChaincodeInputArgs(i), UTF_8));
                        }


                        /* 读写集信息 */
                        TxReadWriteSetInfo txReadWriteSet = transactionActionInfo.getTxReadWriteSet();
                        if (txReadWriteSet != null) {
                            info("----------------------- 【读写集信息】  -----------------------");
                            for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : txReadWriteSet.getNsRwsetInfos()) {
                                String namespace = nsRwsetInfo.getNamespace();
                                KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();

                                for (KvRwset.KVRead kvRead: rws.getReadsList()) {
                                    info("读集 namespace: %s, key: %s, block num: %d.", namespace, kvRead.getKey(), kvRead.getVersion().getBlockNum());
                                }


                                for (KvRwset.KVWrite kvWrite : rws.getWritesList()) {
                                    info("写集 namespace: %s, key: %s, value: %s, isDelete: %b.", namespace, kvWrite.getKey(), new String(kvWrite.getValue().toByteArray(), UTF_8), kvWrite.getIsDelete());
                                }
                            }

                        }



                    }

                }

                System.out.println("\n\n\n");
            }
        }

    }



}