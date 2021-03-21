package com.oschain.fastchaindb.fabric.Handler;

import com.hyperledger.fabric.sdk.entity.dto.api.ExecuteCCDTO;
import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import com.oschain.fastchaindb.blockchain.dto.FCDBTransaction;
import com.oschain.fastchaindb.fabric.dto.TransactionResult;
import org.hyperledger.fabric.sdk.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import static com.hyperledger.fabric.sdk.common.Constants.REDIS_PREFIX;
import static com.hyperledger.fabric.sdk.logger.Logger.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class FabricHandler {

    /** 超时时间, 单位: S */
    private static final Integer TIME_OUT = 50;
    /** channel redis中的key的前缀 */
    private static final String CHANNEL_CACHE = REDIS_PREFIX + "channel:";
    private static final String REDIS_SEPERATOR = "@";

    public static ConcurrentLinkedQueue<FCDBTransaction> concurrentLinkedQueue = new ConcurrentLinkedQueue<FCDBTransaction>();


    /**
     * Star - 交易智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param invokeCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static TransactionResult sendTransactionProposal(HFClient client, Channel channel, ExecuteCCDTO invokeCCDTO, boolean asyncSend) throws Exception {

        TransactionResult transactionResult=new TransactionResult();

        debug("交易智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(invokeCCDTO.getChaincodeID());
        transactionProposalRequest.setFcn(invokeCCDTO.getFuncName());
        transactionProposalRequest.setProposalWaitTime(invokeCCDTO.getProposalWaitTime());
        transactionProposalRequest.setArgs(invokeCCDTO.getParams());
        transactionProposalRequest.setTransientMap(transientMap("TransactionProposalRequest"));

        debug("channel peer的数量: %d", channel.getPeers().size());
        Collection<ProposalResponse> invokeProposals = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

        debug("交易智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));

        //是否异步提交(同步执行)
        if(!asyncSend){
            BlockEvent.TransactionEvent transactionEvent = orderConsensus(channel, invokeProposals,false);
            transactionResult.setTransactionEvent(transactionEvent);
        }

        transactionResult.setProposalResponse(invokeProposals);
        //debug("交易智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));


        return transactionResult;
    }

    /**
     * 将响应结果提交到orderer节点进行共识
     * @param channel 通道对象
     * @param proposalResponses 提议响应集
     * @param usePayload 是否需要显示账户余额
     * @throws Exception e
     * */
    private static BlockEvent.TransactionEvent orderConsensus(Channel channel, Collection<ProposalResponse> proposalResponses, boolean usePayload) throws Exception {
        if (checkResult(proposalResponses, usePayload)) {
            debug("提交到orderer节点进行共识 Start...");
            // 将背书结果提交到 orderer 节点进行排序打块
            BlockEvent.TransactionEvent transactionEvent = channel.sendTransaction(proposalResponses).get(TIME_OUT, TimeUnit.SECONDS);

            debug("提交到orderer共识 End, Type: %s, TransactionActionInfoCount: %d, isValid: %b, ValidationCode: %d.",
                    transactionEvent.getType().name(), transactionEvent.getTransactionActionInfoCount(), transactionEvent.isValid(), transactionEvent.getValidationCode());

            return transactionEvent;

        } else {
            error("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
            throw new FabricSDKException("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
        }
    }

    /**
     * 校验链码响应结果
     * @param proposalResponses 提议响应集
     * @param usePayload 是否需要显示账户余额
     * @return flag boolean
     * */
    private static boolean checkResult(Collection<ProposalResponse> proposalResponses, boolean usePayload) {
        info("check proposal response info: response result size: %d.", proposalResponses.size());
        boolean flag = true;
        for (ProposalResponse proposalResponse: proposalResponses) {

//            String payload;
//            if (usePayload) payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
//            else payload = "【nil】";
//            payload = StringUtils.isEmpty(payload) ? "nil" : payload;
//            info("response status: %s, isVerified: %b from peer: %s, payload: %s.", proposalResponse.getStatus(), proposalResponse.isVerified(), proposalResponse.getPeer().getName(), payload);

            if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                info("failed message: %s, isVerified: %b from peer: %s.", proposalResponse.getMessage(), proposalResponse.isVerified(), proposalResponse.getPeer().getName());
                flag = false;
            }
        }
        return flag;
    }

    public static BlockEvent.TransactionEvent sendTransaction(Channel channel, Collection<ProposalResponse> proposalResponses) throws Exception {
        if (checkResult(proposalResponses, true)) {
            debug("提交到orderer节点进行共识 Start...");
            // 将背书结果提交到 orderer 节点进行排序打块
            BlockEvent.TransactionEvent transactionEvent = channel.sendTransaction(proposalResponses).get(TIME_OUT, TimeUnit.SECONDS);
            debug("提交到orderer共识 End, Type: %s, TransactionActionInfoCount: %d, isValid: %b, ValidationCode: %d.",
                    transactionEvent.getType().name(), transactionEvent.getTransactionActionInfoCount(), transactionEvent.isValid(), transactionEvent.getValidationCode());

            return transactionEvent;
        } else {
            error("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
            throw new FabricSDKException("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
        }
    }

    private static Map<String, byte[]> transientMap(String typeName) {
        return new HashMap<String, byte[]>() {
            {
                put("HyperLedgerFabric", String.format("%s:JavaSDK", typeName).getBytes(UTF_8));
                put("method", String.format("%s", typeName).getBytes(UTF_8));
            }
        };
    }
}
