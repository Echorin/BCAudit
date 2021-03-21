package com.hyperledger.fabric.sdk.handler;

import static com.hyperledger.fabric.sdk.logger.Logger.*;
import static com.hyperledger.fabric.sdk.logger.Logger.debug;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getFile;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getPrivateKeyFromBytes;
import static com.hyperledger.fabric.sdk.common.Constants.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.protobuf.InvalidProtocolBufferException;
import com.oschain.fastchaindb.common.utils.*;
import com.hyperledger.fabric.sdk.common.Constants;
import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.*;
import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import com.hyperledger.fabric.sdk.utils.HFSDKUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.protos.peer.Chaincode;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kevin on 2018-09-03 11:16
 *
 * 操作区块链链码API
 *
 * 单元测试类: APITest
 */
public class ApiHandler {

//    @Value("${redis.host}")
//    private static String host;
//
//    @Value("${redis.port}")
//    private static int port;

    /** 超时时间, 单位: S */
    private static final Integer TIME_OUT = 50;
    /** channel redis中的key的前缀 */
    private static final String CHANNEL_CACHE = REDIS_PREFIX + "channel:";
    private static final String REDIS_SEPERATOR = "@";


//    @Autowired
//    private static JedisPool jedisPool;


//    @Autowired
//    private static RedisUtil redisUtil;
//
    private static Jedis jedis;
    static {
        jedis =new Jedis(Constants.REDIS_IP, Constants.REDIS_PORT);

        jedis.select(REDIS_INDEX);

        if (StringUtils.isNotEmpty(REDIS_PASSWD)) {
            jedis.auth(REDIS_PASSWD);
        }
    }


    /**
     * Star - 构建客户端实例
     * @param buildClientDTO {@link BuildClientDTO}
     * @return HFClient
     * @throws Exception e
     * */
    public static HFClient clientBuild(BuildClientDTO buildClientDTO) throws Exception {
        debug("构建Hyperledger Fabric客户端实例 Start...");
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        String mspPath = buildClientDTO.getMspPath();

        File certFile, keyFile;
        if (StringUtils.isNotEmpty(mspPath)) {
            certFile = getFile(mspPath + "signcerts", null);
            keyFile = getFile(mspPath + "keystore", "_sk");
        } else {
            certFile = new File(buildClientDTO.getCertPath());
            keyFile = new File(buildClientDTO.getKeyPath());
        }

        InputStream certFileIS = new FileInputStream(certFile);
        String cert = new String(IOUtils.toByteArray(certFileIS), UTF_8);

        InputStream keyFileIS = new FileInputStream(keyFile);
        PrivateKey key = getPrivateKeyFromBytes(IOUtils.toByteArray(keyFileIS));

        String name = buildClientDTO.getName();
        String mspid = buildClientDTO.getMspId();
        Enrollment enrollmentDTO = new EnrollmentDTO(cert, key);

        User user = new UserContextDTO(name, mspid, enrollmentDTO);
        client.setUserContext(user);
        debug("构建Hyperledger Fabric客户端实例 End!!!");
        return client;
    }


    /**
     * Star - 创建通道 - 使用缓存 channel 对象
     * @param client 客户端实例
     * @param channelName 通道名称
     * @return {@link Channel}
     * @throws Exception e
     * */
    public static Channel createChannel(HFClient client, String channelName) throws Exception {
        return createChannel(client, channelName, null, true);
    }


    /**
     * Star - 创建通道, 包含了加入的peer节点
     * @param client 客户端实例
     * @param channelName 通道名称
     * @param createChannelDTO {@link CreateChannelDTO}
     * @param useCache 是否优先使用已缓存的通道对象
     * @return {@link Channel}
     * @throws Exception e
     * */
    public static Channel createChannel(HFClient client, String channelName, CreateChannelDTO createChannelDTO, boolean useCache) throws Exception {
        debug("创建通道 Start, channelName: %s.", channelName);
        String mspId = client.getUserContext().getMspId();
        if (StringUtils.isEmpty(mspId)) throw new FabricSDKException("mspId must not to be empty.");
        String redisKey = CHANNEL_CACHE + channelName + REDIS_SEPERATOR + mspId;

        Channel channel;
        // 优先使用缓存中已缓存的通道对象
        if (useCache) {

            byte[] bytes = jedis.get(redisKey.getBytes());
            if (bytes != null) {
                warn("缓存中已存在通道名称为: %s 的通道对象, 使用缓存中的通道对象, 缓存Key: %s.", channelName, redisKey);
                channel = client.deSerializeChannel(bytes);
                if (!channel.isInitialized()) channel.initialize();
                debug("创建通道 End, channelName: %s, isInitialized: %b.", channelName, channel.isInitialized());
                return channel;
            }
            //throw new FabricSDKException("缓存数据为空, 操作缓存失败. key=" + redisKey);
        }

        File channelFile = new File(createChannelDTO.getChannelConfigPath());
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(channelFile);

        Collection<OrderNodeDTO> orderNodeDTOS = createChannelDTO.getOrderNodeDTOS();
        OrderNodeDTO orderNodeDTO = orderNodeDTOS.iterator().next();
        Orderer orderer = client.newOrderer(orderNodeDTO.getNodeName(), orderNodeDTO.getGrpcUrl(), orderNodeDTO.getProperties());
        channel = client.newChannel(channelName, orderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, client.getUserContext()));

        // 把 orderer 节点加入通道
        channel.addOrderer(orderer);
        debug("order节点: %s 已成功加入通道.", orderer.getName());

        // 把 peer 节点加入通道
        Collection<PeerNodeDTO> peerNodeDTOS = createChannelDTO.getPeerNodeDTOS();
        joinPeers(client, channel, peerNodeDTOS);

        debug("创建通道 End, channelName: %s, isInitialized: %b.", channelName, channel.isInitialized());
        // 如果已连接redis, 则将 channel 对象存储进缓存
//        JedisUtils.set(redisKey.getBytes(), channel.serializeChannel());
        if (jedis.isConnected()) {
            jedis.set(redisKey.getBytes(), channel.serializeChannel());
            info("通道对象已放入redis缓存, 缓存key: %s.", redisKey);
        }



        HFSDKUtils.serialize2File(channel, mspId);
        return channel;
    }


    /**
     * Star - 将指定peer节点加入通道
     * @param client 客户端实例
     * @param channelName 通道名称
     * @param orderNodeDTOS order节点信息
     * @param peerNodeDTOS peer节点信息
     * @throws Exception e
     * */
    public static void joinPeers(HFClient client, String channelName, Collection<OrderNodeDTO> orderNodeDTOS, Collection<PeerNodeDTO> peerNodeDTOS) throws Exception {
        joinPeers(client, channelName, orderNodeDTOS, peerNodeDTOS, true);
    }


    /**
     * Star - 将指定peer节点加入通道
     * @param client 客户端实例
     * @param channelName 通道名称
     * @param orderNodeDTOS order节点信息
     * @param peerNodeDTOS peer节点信息
     * @param useCache 优先使用缓存
     * @throws Exception e
     * */
    public static void joinPeers(HFClient client, String channelName, Collection<OrderNodeDTO> orderNodeDTOS, Collection<PeerNodeDTO> peerNodeDTOS, boolean useCache) throws Exception {
        debug("往通道加入新节点 Start, channelName: %s.", channelName);
        String mspId = client.getUserContext().getMspId();
        if (StringUtils.isEmpty(mspId)) throw new FabricSDKException("mspId must not to be empty.");

        String redisKey = CHANNEL_CACHE + channelName + REDIS_SEPERATOR + mspId;
        Channel channel = null;
        if (useCache) {
            byte[] bytes = jedis.get(redisKey.getBytes());
//            byte[] bytes = JedisUtils.get(redisKey.getBytes());

            if (bytes != null) {
                warn("缓存中已存在通道名称为: %s 的通道对象, 使用缓存中的通道对象, 缓存Key: %s.", channelName, redisKey);
                channel = client.deSerializeChannel(bytes);
                if (!channel.isInitialized()) channel.initialize();
                debug("创建通道 End, channelName: %s, isInitialized: %b.", channelName, channel.isInitialized());
            }
        }

        if (channel == null || channel.isShutdown() || !channel.isInitialized()) {
            /* 建立通道连接, 通道已建立情况下, 使用该方式返回一个通道实例 */
            channel = client.newChannel(channelName);
            OrderNodeDTO orderNodeDTO = orderNodeDTOS.iterator().next();
            Orderer orderer = client.newOrderer(orderNodeDTO.getNodeName(), orderNodeDTO.getGrpcUrl(), orderNodeDTO.getProperties());
            channel.addOrderer(orderer);
            channel.initialize();
        }

        // 加入新节点
        joinPeers(client, channel, peerNodeDTOS);

        // 将已有通道对象加入缓存
//        JedisUtils.set(redisKey.getBytes(), channel.serializeChannel());
        if (jedis.isConnected()) {
            jedis.set(redisKey.getBytes(), channel.serializeChannel());
            info("通道对象已放入redis缓存, 缓存key: %s.", redisKey);
        }

        HFSDKUtils.serialize2File(channel, mspId);
        debug("往通道加入新节点 End, channelName: %s.", channelName);
    }


    /**
     * Star - 将指定peer节点加入通道
     * @param client 客户端实例
     * @param channel 通道对象
     * @param peerNodeDTOS 节点信息
     * */
    private static void joinPeers(HFClient client, Channel channel, Collection<PeerNodeDTO> peerNodeDTOS) throws Exception {
        for (PeerNodeDTO peerNodeDTO : peerNodeDTOS) {
            Peer peer = client.newPeer(peerNodeDTO.getNodeName(), peerNodeDTO.getGrpcUrl(), peerNodeDTO.getProperties());
            channel.joinPeer(peer);
            debug("peer节点: %s 已成功加入通道.", peerNodeDTO.getNodeName());
        }
        if (!channel.isInitialized()) {
            channel.initialize();
        }
    }


    /**
     * Star - 安装智能合约
     * @param client 客户端实例
     * @param installCCDTO {@link InstallCCDTO}
     * @throws Exception e
     * */
    public static void installChainCode(HFClient client, InstallCCDTO installCCDTO) throws Exception {
        ChaincodeID chaincodeID = installCCDTO.getChaincodeID();
        debug("安装智能合约 Start, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(installCCDTO.getChaincodeID());
        installProposalRequest.setChaincodeSourceLocation(new File(installCCDTO.getChaincodeSourceLocation()));
        installProposalRequest.setChaincodeVersion(installCCDTO.getChaincodeID().getVersion());

        Collection<Peer> peers = new ArrayList<>();
        Collection<PeerNodeDTO> peerNodeDTOS = installCCDTO.getPeerNodeDTOS();
        /* 给指定节点部署智能合约 */
        for (PeerNodeDTO peerNodeDTO : peerNodeDTOS) {
            Peer peer0 = client.newPeer(peerNodeDTO.getNodeName(), peerNodeDTO.getGrpcUrl(), peerNodeDTO.getProperties());
            peers.add(peer0);
        }

        Collection<ProposalResponse> installProposals = client.sendInstallProposal(installProposalRequest, peers);

        if (checkResult(installProposals, false)) {
            debug("安装智能合约 End, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
        } else {
            debug("安装智能合约失败啦, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
        }

    }


    /**
     * Answer - 升级智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param upgradeCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * @apiNote 升级智能合约前需要先安装新的智能合约代码, 即先调用 installChainCode 方法
     * */
    public static void upgradeChainCode(HFClient client, Channel channel, ExecuteCCDTO upgradeCCDTO) throws Exception {
        debug("升级智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), upgradeCCDTO.getFuncName(), Arrays.asList(upgradeCCDTO.getParams()));
        UpgradeProposalRequest upgradeProposalRequest = client.newUpgradeProposalRequest();

        upgradeProposalRequest.setChaincodeID(upgradeCCDTO.getChaincodeID());
        upgradeProposalRequest.setFcn(upgradeCCDTO.getFuncName());
        upgradeProposalRequest.setArgs(upgradeCCDTO.getParams());
        upgradeProposalRequest.setProposalWaitTime(upgradeCCDTO.getProposalWaitTime());
        // 设置背书策略
        String chaincodeEndorsementPolicyPath = upgradeCCDTO.getPolicyPath();
        if (StringUtils.isNotEmpty(chaincodeEndorsementPolicyPath)) {
            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
            chaincodeEndorsementPolicy.fromYamlFile(new File(chaincodeEndorsementPolicyPath));
            upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }

        upgradeProposalRequest.setTransientMap(transientMap("UpgradeProposalRequest"));

        Collection<ProposalResponse> initProposals = channel.sendUpgradeProposal(upgradeProposalRequest, channel.getPeers());

        orderConsensus(channel, initProposals, false);

        debug("升级智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), upgradeCCDTO.getFuncName(), Arrays.asList(upgradeCCDTO.getParams()));
    }


    /**
     * Star - 初始化智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param initCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static void initializeChainCode(HFClient client, Channel channel, ExecuteCCDTO initCCDTO) throws Exception {
        debug("初始化智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), initCCDTO.getFuncName(), Arrays.asList(initCCDTO.getParams()));
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(initCCDTO.getProposalWaitTime());
        instantiateProposalRequest.setChaincodeID(initCCDTO.getChaincodeID());
        instantiateProposalRequest.setFcn(initCCDTO.getFuncName());
        instantiateProposalRequest.setArgs(initCCDTO.getParams());
        // 设置背书策略
        String chaincodeEndorsementPolicyPath = initCCDTO.getPolicyPath();
        if (StringUtils.isNotEmpty(chaincodeEndorsementPolicyPath)) {
            ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
            chaincodeEndorsementPolicy.fromYamlFile(new File(chaincodeEndorsementPolicyPath));
            instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        }
        instantiateProposalRequest.setTransientMap(transientMap("InstantiateProposalRequest"));

        Collection<ProposalResponse> initProposals = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());

        orderConsensus(channel, initProposals, false);

        debug("初始化智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), initCCDTO.getFuncName(), Arrays.asList(initCCDTO.getParams()));
    }


    /**
     * Star - 查询智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param queryCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static void queryChainCode(HFClient client, Channel channel, ExecuteCCDTO queryCCDTO) throws Exception {
        debug("查询智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(queryCCDTO.getParams());
        queryByChaincodeRequest.setFcn(queryCCDTO.getFuncName());
        queryByChaincodeRequest.setChaincodeID(queryCCDTO.getChaincodeID());
        queryByChaincodeRequest.setProposalWaitTime(queryCCDTO.getProposalWaitTime());
        queryByChaincodeRequest.setTransientMap(transientMap("QueryByChaincodeRequest"));

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());

        checkResult(queryProposals);

        debug("查询智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
    }


    /**
     * Star - 交易智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param invokeCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static void invokeChainCode(HFClient client, Channel channel, ExecuteCCDTO invokeCCDTO) throws Exception {
        debug("交易智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(invokeCCDTO.getChaincodeID());
        transactionProposalRequest.setFcn(invokeCCDTO.getFuncName());
        transactionProposalRequest.setProposalWaitTime(invokeCCDTO.getProposalWaitTime());
        transactionProposalRequest.setArgs(invokeCCDTO.getParams());
        transactionProposalRequest.setTransientMap(transientMap("TransactionProposalRequest"));

        Collection<ProposalResponse> invokeProposals = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());



        orderConsensus(channel, invokeProposals);

        debug("交易智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));
    }

    private static void orderConsensus(Channel channel, Collection<ProposalResponse> proposalResponses) throws Exception {
        orderConsensus(channel, proposalResponses, true);
    }
    /**
     * 将响应结果提交到orderer节点进行共识
     * @param channel 通道对象
     * @param proposalResponses 提议响应集
     * @param usePayload 是否需要显示账户余额
     * @throws Exception e
     * */
    private static void orderConsensus(Channel channel, Collection<ProposalResponse> proposalResponses, boolean usePayload) throws Exception {
        if (checkResult(proposalResponses, usePayload)) {
            debug("提交到orderer节点进行共识 Start...");
            // 将背书结果提交到 orderer 节点进行排序打块
            BlockEvent.TransactionEvent transactionEvent = channel.sendTransaction(proposalResponses).get(TIME_OUT, TimeUnit.SECONDS);


//            channel.q
            //channel.queryBlockByTransactionID();
            //BlockInfo blockInfo = channel.queryBlockByNumber(blockNum);

//            try {
//                transactionEvent = channel.sendTransaction(proposalResponses).get(TIME_OUT, TimeUnit.SECONDS);
//            }catch(Exception ex){
//                ex.printStackTrace();
//             String s =  ex.getMessage();
//             //if(s.contains())
//            }

//            BlockEvent blockEvent = transactionEvent.getBlockEvent();
//
//            int envelopeCount = blockEvent.getEnvelopeCount();
//            long num = blockEvent.getBlockNumber();
//
//
//            String s1 =   Hex.encodeHexString(blockEvent.getBlock().getHeader().getDataHash().toByteArray());
//            String s2 =   Hex.encodeHexString(blockEvent.getPreviousHash());
//
//
//
//            BlockInfo blockInfo = channel.queryBlockByNumber(32);
//
//            String s3 =   Hex.encodeHexString(blockInfo.getDataHash());
//            String s4 =   Hex.encodeHexString(blockInfo.getPreviousHash());
//
//
//            System.out.println(num);
//            System.out.println("s1:"+s1);
//            System.out.println("s2:"+s2);
//            System.out.println("s3:"+s3);
//            System.out.println("s4:"+s4);


            debug("提交到orderer共识 End, Type: %s, TransactionActionInfoCount: %d, isValid: %b, ValidationCode: %d.",
                    transactionEvent.getType().name(), transactionEvent.getTransactionActionInfoCount(), transactionEvent.isValid(), transactionEvent.getValidationCode());
        } else {
            error("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
            throw new FabricSDKException("操作智能合约操作失败, 交易响应的结果集存在异常响应.");
        }
    }


    private static boolean checkResult(Collection<ProposalResponse> proposalResponses) {
        return checkResult(proposalResponses, true);
    }

    /**
     * 校验链码响应结果
     * @param proposalResponses 提议响应集
     * @param usePayload 是否需要显示账户余额
     * @return flag boolean
     * */
    public static boolean checkResult(Collection<ProposalResponse> proposalResponses, boolean usePayload) {
        info("check proposal response info: response result size: %d.", proposalResponses.size());
        boolean flag = true;
        for (ProposalResponse proposalResponse: proposalResponses) {
            String payload;
            if (usePayload) payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
            else payload = "【nil】";
            payload = StringUtils.isEmpty(payload) ? "nil" : payload;
            info("response status: %s, isVerified: %b from peer: %s, payload: %s.", proposalResponse.getStatus(), proposalResponse.isVerified(), proposalResponse.getPeer().getName(), payload);

            if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                info("failed message: %s, isVerified: %b from peer: %s.", proposalResponse.getMessage(), proposalResponse.isVerified(), proposalResponse.getPeer().getName());
                flag = false;
            }
        }
        return flag;
    }


    private static Map<String, byte[]> transientMap(String typeName) {
        return new HashMap<String, byte[]>() {
            {
                put("HyperLedgerFabric", String.format("%s:JavaSDK", typeName).getBytes(UTF_8));
                put("method", String.format("%s", typeName).getBytes(UTF_8));
            }
        };
    }


    /**************************************** 新加方法 ******************************************************/

    /**
     * Star - 查询智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param queryCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static Collection<ProposalResponse> queryChainCode2(HFClient client, Channel channel, ExecuteCCDTO queryCCDTO) throws Exception {
        debug("查询智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(queryCCDTO.getParams());
        queryByChaincodeRequest.setFcn(queryCCDTO.getFuncName());
        queryByChaincodeRequest.setChaincodeID(queryCCDTO.getChaincodeID());
        queryByChaincodeRequest.setProposalWaitTime(queryCCDTO.getProposalWaitTime());
        queryByChaincodeRequest.setTransientMap(transientMap("QueryByChaincodeRequest"));

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());


        debug("查询智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), queryCCDTO.getFuncName(), Arrays.asList(queryCCDTO.getParams()));
        return queryProposals;
    }


    /**
     * Star - 交易智能合约
     * @param client 客户端实例
     * @param channel 通道对象
     * @param invokeCCDTO {@link ExecuteCCDTO}
     * @throws Exception e
     * */
    public static Collection<ProposalResponse> sendTransactionProposal(HFClient client, Channel channel, ExecuteCCDTO invokeCCDTO,boolean asyncSend) throws Exception {
        debug("交易智能合约 Start, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(invokeCCDTO.getChaincodeID());
        transactionProposalRequest.setFcn(invokeCCDTO.getFuncName());
        transactionProposalRequest.setProposalWaitTime(invokeCCDTO.getProposalWaitTime());
        transactionProposalRequest.setArgs(invokeCCDTO.getParams());
        transactionProposalRequest.setTransientMap(transientMap("TransactionProposalRequest"));

        Collection<ProposalResponse> invokeProposals = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());

        debug("交易智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));

        //是否异步提交
        if(!asyncSend){
            orderConsensus(channel, invokeProposals);
        }

        //debug("交易智能合约 End, channelName: %s, fcn: %s, args: %s", channel.getName(), invokeCCDTO.getFuncName(), Arrays.asList(invokeCCDTO.getParams()));
        return invokeProposals;
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

//    public static void createChaincodeID(String chaincodeName, ChaincodeID chaincodeID){
//
//        org.hyperledger.fabric.protos.peer.Chaincode.ChaincodeID chaincodeIDNew = null;
//
//        String redisKey = chaincodeName;
//        // 将已有通道对象加入缓存
//        JedisUtils.set(redisKey.getBytes(), chaincodeID.getFabricChaincodeID().toByteArray());
//        info("通道对象已放入redis缓存, 缓存key: %s.", redisKey);
//
//        byte[] bytes = JedisUtils.get(redisKey.getBytes());
//
//        try {
//            chaincodeIDNew = Chaincode.ChaincodeID.parseFrom(bytes);
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//
//        //return ;
//    }
//
//
//    /**
//     * 序列化对象
//     *
//     * @param object
//     * @return
//     */
//    public static byte[] serialize(Object object) {
//        ObjectOutputStream oos = null;
//        ByteArrayOutputStream baos = null;
//        try {
//            if (object != null) {
//                baos = new ByteArrayOutputStream();
//                oos = new ObjectOutputStream(baos);
//                oos.writeObject(object);
//                return baos.toByteArray();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 反序列化对象
//     *
//     * @param bytes
//     * @return
//     */
//    public static Object unserialize(byte[] bytes) {
//        ByteArrayInputStream bais = null;
//        try {
//            if (bytes != null && bytes.length > 0) {
//                bais = new ByteArrayInputStream(bytes);
//                ObjectInputStream ois = new ObjectInputStream(bais);
//                return ois.readObject();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


//    public static <T>  byte[]  serialize(T obj) throws IOException, InvalidArgumentException {
//
//        Class<T> cls = (Class<T>) obj.getClass();
//
//        ObjectOutputStream out = null;
//
//        byte[] var3;
//        try {
//            ByteArrayOutputStream bai = new ByteArrayOutputStream();
//            out = new ObjectOutputStream(bai);
//            out.writeObject(cls);
//            out.flush();
//            var3 = bai.toByteArray();
//        } finally {
//            if (null != out) {
//                try {
//                    out.close();
//                } catch (IOException var10) {
//                }
//            }
//
//        }
//
//        return var3;
//    }
//
//
//    public static <T> T deSerialize(byte[] data,Class<T> clazz) throws IOException, ClassNotFoundException, InvalidArgumentException {
//        ObjectInputStream in = null;
//
//        T t;
//        try {
//            in = new ObjectInputStream(new ByteArrayInputStream(data));
//            t = (T)in.readObject();
//
//        } finally {
//            try {
//                if (in != null) {
//                    in.close();
//                }
//            } catch (IOException var13) {
//            }
//        }
//
//        return t;
//    }

}
