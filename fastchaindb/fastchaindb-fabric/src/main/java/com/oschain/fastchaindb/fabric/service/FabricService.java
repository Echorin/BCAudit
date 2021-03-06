package com.oschain.fastchaindb.fabric.service;

import com.hyperledger.fabric.sdk.entity.dto.api.*;
import com.hyperledger.fabric.sdk.handler.ApiHandler;
import com.oschain.fastchaindb.blockchain.constants.TransactionType;
import com.oschain.fastchaindb.blockchain.dto.*;
import com.oschain.fastchaindb.chainsql.model.CertBlock;
import com.oschain.fastchaindb.chainsql.service.CertBlockService;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.common.config.FabricStartConfig;
import com.oschain.fastchaindb.common.utils.*;
import com.oschain.fastchaindb.fabric.Handler.FabricHandler;
import com.oschain.fastchaindb.fabric.dto.TransactionResult;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.helper.Config;
import org.omg.PortableInterceptor.INACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.hyperledger.fabric.sdk.common.Config.*;
import static com.hyperledger.fabric.sdk.common.Config.PEER0_ORG1_EVENT_URL;
import static com.hyperledger.fabric.sdk.logger.Logger.debug;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;

@Service
public class FabricService {
    private static final Logger logger = LoggerFactory.getLogger(FabricService.class);
    public static String serverTag = "0";

    @Autowired
    private RedisUtil redisUtil;

//    @Autowired
//    private ChaincodeID chaincodeID;

    @Autowired
    private HFClient hfClient;
    @Autowired
    CertBlockService certBlockService;

    @Value("${fastchaindb.async}")
    private boolean asyncSend = false;

//    @Value("${server.tag}")
//    public String serverTag;

    @Value("${server.tag}")
    public void setServerTag(String serverTag) {
        FabricService.serverTag = serverTag;
    }


    /**
     * ????????????
     *
     * @param transactionData
     * @return
     * @throws Exception
     */
    public FCDBTransaction sendTransactionProposal(FCDBAsset asset) throws Exception {

        Channel channel = hfClient.getChannel(FabricStartConfig.channelName);

        // 6. ??????????????????invoke
        System.out.println("?????????");

        String uuid = UUIDUtil.randomUUID32();
        String uuid2 = String.valueOf(System.currentTimeMillis());
        System.out.println(asset.getTransactionData());
        //System.out.println(asset.toString());
        //System.out.println(asset.getData());
        String data = asset.getTransactionData();
        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[]{JSONObject.fromObject(data).getString("fileId"), JSONObject.fromObject(data).getString("fileHash")}).chaincodeID(FabricStartConfig.chaincodeID).build();

        //ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[] {"a", "b", "1"}).chaincodeID(chaincodeID).build();
        TransactionResult transactionResult = FabricHandler.sendTransactionProposal(hfClient, channel, invokeCCDTO, asyncSend);

        if (transactionResult == null) {
            logger.error("?????????????????????");
            return null;
        }

        Collection<ProposalResponse> proposalResponseList = transactionResult.getProposalResponse();
        if (proposalResponseList.isEmpty()) {
            logger.error("????????????????????????");
            return null;
        } else {

            ProposalResponse proposalResponse = proposalResponseList.iterator().next();

            FCDBTransaction transaction = new FCDBTransaction();
            transaction.setTransactionId(proposalResponse.getTransactionID());
            transaction.setTransactionData(asset.getTransactionData());
            transaction.setTransactionType(asset.getTransactionType());
            transaction.setTransactionTime(new Date());

            //KEY
            String cacheKey = proposalResponse.getTransactionID();
            //????????????
            if (asyncSend) {
                EHCacheUtil.put(FabricStartConfig.chaincodeName, cacheKey, proposalResponseList);
                //??????Redis??????
                //redisUtil.in(FabricConfig.chaincodeName + serverTag, transaction);
                FabricHandler.concurrentLinkedQueue.offer(transaction);
            } else {
                //?????????????????????
                CertBlock certBlock = new CertBlock();

                //????????????
                BlockEvent.TransactionEvent transactionEvent = transactionResult.getTransactionEvent();
                if (transactionEvent != null) {
                    String dataHash = Hex.encodeHexString(transactionEvent.getBlockEvent().getDataHash());
                    certBlock.setBlockHash(dataHash);
                    certBlock.setBlockTime(transactionEvent.getTimestamp());
                    certBlock.setBlockStatus(1);
                } else {
                    certBlock.setBlockStatus(-1);
                }

                certBlock.setBlockBody(transaction.getTransactionData());
                certBlock.setTransactionId(transaction.getTransactionId());
                certBlock.setCreateTime(transaction.getTransactionTime());//new Date(proposalResponse.getProposalResponse().getTimestamp().getSeconds() * 1000)
                certBlock.setWriteMode(0);
                System.out.println(certBlock);
                System.out.println(transaction);
                splitBlock(certBlock,transaction);

                certBlockService.save(certBlock);
            }

            return transaction;
        }
    }

    /**
     * ??????????????????
     *
     * @param transaction
     * @throws Exception
     */
    public void sendTransaction(FCDBTransaction transaction) throws Exception {



        try {
            String cacheKey = transaction.getTransactionId();
            Collection<ProposalResponse> proposalResponseList = (Collection<ProposalResponse>) EHCacheUtil.get(FabricStartConfig.chaincodeName, cacheKey);

            Channel channel = hfClient.getChannel(FabricStartConfig.channelName);
            BlockEvent.TransactionEvent transactionEvent = FabricHandler.sendTransaction(channel, proposalResponseList);


            //??????????????????????????????
            if (transactionEvent != null) {
                EHCacheUtil.remove(FabricStartConfig.chaincodeName, cacheKey);

                String transactionID = transactionEvent.getTransactionID();
                BlockEvent blockEvent = transactionEvent.getBlockEvent();
                //int envelopeCount = blockEvent.getEnvelopeCount();
                //long num = blockEvent.getBlockNumber();
                String dataHash = Hex.encodeHexString(blockEvent.getDataHash());
                //String s2 = Hex.encodeHexString(blockEvent.getPreviousHash());

                CertBlock certBlock = new CertBlock();
                certBlock.setTransactionId(transactionID);
                certBlock.setBlockStatus(1);
                certBlock.setBlockHash(dataHash);
                certBlock.setBlockTime(transactionEvent.getTimestamp());
                certBlock.setBlockBody(transaction.getTransactionData());
                certBlock.setCreateTime(transaction.getTransactionTime());
                certBlock.setWriteMode(1);
                splitBlock(certBlock,transaction);

                certBlockService.save(certBlock);
                //certBlockService.update(certBlock);
            } else {

                //????????????5?????????????????????????????????????????????
                int sendNum=transaction.getSendNum();
                if(sendNum>=5){
                    CertBlock certBlock = new CertBlock();
                    certBlock.setTransactionId(transaction.getTransactionId());
                    certBlock.setBlockStatus(-2);
                    certBlock.setBlockBody(transaction.getTransactionData());
                    certBlock.setCreateTime(transaction.getTransactionTime());
                    certBlock.setWriteMode(1);
                    splitBlock(certBlock,transaction);

                    certBlockService.save(certBlock);
                }else{
                    //?????????????????????Redis??????
                    //redisUtil.in(FabricConfig.chaincodeName, transaction);
                    transaction.setSendNum(sendNum+1);
                    FabricHandler.concurrentLinkedQueue.offer(transaction);
                }

            }
        }catch (Exception ex){
            CertBlock certBlock = new CertBlock();
            certBlock.setTransactionId(transaction.getTransactionId());
            certBlock.setBlockStatus(-1);
            certBlock.setBlockBody(transaction.getTransactionData());
            certBlock.setCreateTime(transaction.getTransactionTime());
            certBlock.setWriteMode(1);
            splitBlock(certBlock,transaction);

            certBlockService.save(certBlock);
        }

    }

    private void splitBlock(CertBlock certBlock, FCDBTransaction transaction){
        System.out.println("splitBlock");
        System.out.println(transaction.getTransactionType());
        //??????????????????????????????
        if(transaction.getTransactionType().equals(TransactionType.ARCHIVES.toString())) {
            FCDBAssetKey fcdbAssetKey = GsonUtil.gsonToBean(transaction.getTransactionData(), FCDBAssetKey.class);
            certBlock.setBlockKey1(fcdbAssetKey.getBlockKey1());
            certBlock.setBlockKey2(fcdbAssetKey.getBlockKey2());
            certBlock.setBlockKey3(fcdbAssetKey.getBlockKey3());
            certBlock.setBlockKey4(fcdbAssetKey.getBlockKey4());
            certBlock.setBlockKey5(fcdbAssetKey.getBlockKey5());
        }
    }

    public FCDBBlock getXChainByTransactionId(FCDBTransationQuery transationQuery) throws Exception {

        Channel channel = hfClient.getChannel(FabricStartConfig.channelName);

        logger.debug("???????????????"+channel.queryBlockchainInfo().getHeight());

        BlockInfo blockInfo = channel.queryBlockByTransactionID(transationQuery.getTransactionId());
        HashMap<String, String> transactionIdMap = new HashMap<String, String>();
        transactionIdMap.put(transationQuery.getTransactionId(), "");

        return getXChainBlock(blockInfo, transactionIdMap);

//        byte[] currentDataHash = blockInfo.getDataHash();
//        xChainBlock.setBlockHash(Hex.encodeHexString(currentDataHash));
//        xChainBlock.setPrevBlockHash(Hex.encodeHexString(blockInfo.getPreviousHash()));
//        xChainBlock.setBlockHeight(blockInfo.getBlockNumber());
//        xChainBlock.setTransactionCount(blockInfo.getTransactionCount());
//        //xChainBlock.setBlockTime(blockInfo);
//
//        for (BlockInfo.EnvelopeInfo envelopeInfo: blockInfo.getEnvelopeInfos()) {
//
//            /* TRANSACTION_ENVELOPE: ?????????, ENVELOPE: ???????????? */
//            if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
//                //???????????????????????????????????????????????????????????????TransactionId
//                if (envelopeInfo.getTransactionID().contains(transactionId)) {
//                    BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
//                    for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
//
//                        /* ???????????????????????? */
//                        logger.debug("----------------------- ??????????????????????????????  -----------------------");
//                        for (int i = 0; i < transactionActionInfo.getChaincodeInputArgsCount(); i++) {
//                            logger.debug( new String(transactionActionInfo.getChaincodeInputArgs(i), UTF_8));
//
//                            //logger.debug("????????????: %d, ?????????: %s", i, new String(transactionActionInfo.getChaincodeInputArgs(i), UTF_8));
//                        }
//                    }
//                }
//            }
//        }

        //logger.debug("current block data hash: %s.", Hex.encodeHexString(currentDataHash));
        //logger.debug("previous block hash: %s.", Hex.encodeHexString(blockInfo.getPreviousHash()));
        //logger.debug("blockNumber: %d, envelopeCount: %d, channelId: %s.", blockInfo.getBlockNumber(), blockInfo.getEnvelopeCount(), blockInfo.getChannelId());

//        return xChainBlock;
    }

    /**
     * ????????????????????????????????????????????????????????????
     * @param transationQuery
     * @return
     * @throws Exception
     */
    public List<FCDBBlock> getXChainByTransactionData(FCDBTransationQuery transationQuery) throws Exception {

        CertBlock certBlock = new CertBlock();
        //????????????????????????(??????????????????)
        if(transationQuery.getTransactionType().equals(TransactionType.ARCHIVES.toString())){
            FCDBAssetKey fcdbAssetKey = GsonUtil.gsonToBean(transationQuery.getTransactionData(),FCDBAssetKey.class);
            //certBlock.setBlockBody(fcdbAssetKey.getBlockBody());
            certBlock.setBlockKey1(fcdbAssetKey.getBlockKey1());
            certBlock.setBlockKey2(fcdbAssetKey.getBlockKey2());
            certBlock.setBlockKey3(fcdbAssetKey.getBlockKey3());
            certBlock.setBlockKey4(fcdbAssetKey.getBlockKey4());
            certBlock.setBlockKey5(fcdbAssetKey.getBlockKey5());
        }
        else{
            certBlock.setBlockBody(transationQuery.getTransactionData());
        }


        int pageIndex=0;
        if(transationQuery.getPageIndex()!=null){
            pageIndex=transationQuery.getPageIndex();
        }

        PageResult<CertBlock> page = certBlockService.listCertBlock(pageIndex,20,certBlock);
        if(page==null){
            logger.error("????????????????????????");
            return null;
        }

        //????????????transationID
        BlockInfo blockInfo;
        HashMap<String, String> transactionIdMap;
        List<FCDBBlock> transactionList = new ArrayList<>();
        Channel channel = hfClient.getChannel(FabricStartConfig.channelName);

        List<CertBlock> list=page.getData();
        for (CertBlock certBlockMod : list) {
            blockInfo = channel.queryBlockByTransactionID(certBlockMod.getTransactionId());
            transactionIdMap = new HashMap<String, String>();
            transactionIdMap.put(certBlockMod.getTransactionId(), "");
            FCDBBlock fcdbBlock = getXChainBlock(blockInfo, transactionIdMap);
            transactionList.add(fcdbBlock);
        }

        return transactionList;
    }

    private FCDBBlock getXChainBlock(BlockInfo blockInfo, Map<String, String> transactionIdMap) {

        FCDBBlock xChainBlock = new FCDBBlock();
        byte[] currentDataHash = blockInfo.getDataHash();
        xChainBlock.setBlockHash(Hex.encodeHexString(currentDataHash));
        xChainBlock.setPrevBlockHash(Hex.encodeHexString(blockInfo.getPreviousHash()));
        xChainBlock.setBlockHeight(blockInfo.getBlockNumber());
        xChainBlock.setTransactionCount(blockInfo.getTransactionCount());
        //xChainBlock.setBlockTime(blockInfo.);

        FCDBTransaction transaction;
        List<FCDBTransaction> transactionList = new ArrayList<FCDBTransaction>();
        Map<String, String> transactionMap = new HashMap<>();

        for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {

            /* TRANSACTION_ENVELOPE: ?????????, ENVELOPE: ???????????? */
            if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {

                //???????????????????????????????????????????????????????????????TransactionId
                BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = null;
                if (transactionIdMap == null) {
                    transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                } else {
                    if (transactionIdMap.containsKey(envelopeInfo.getTransactionID())) {
                        transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                    }
                }

                if (transactionEnvelopeInfo == null) {
                    continue;
                }

                for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
                    /* ???????????????????????? */
                    logger.debug("----------------------- ??????????????????????????????  -----------------------");
                    for (int i = 0; i < transactionActionInfo.getChaincodeInputArgsCount(); i++) {
                        logger.debug(new String(transactionActionInfo.getChaincodeInputArgs(i), UTF_8));
                        //logger.debug("????????????: %d, ?????????: %s", i, new String(transactionActionInfo.getChaincodeInputArgs(i), UTF_8));
                        if (!transactionMap.containsKey(envelopeInfo.getTransactionID())) {
                            transaction = new FCDBTransaction();
                            transaction.setTransactionId(envelopeInfo.getTransactionID());
                            transaction.setTransactionData(new String(transactionActionInfo.getChaincodeInputArgs(2), UTF_8));
                            transaction.setTransactionTime(envelopeInfo.getTimestamp());

                            xChainBlock.setBlockTime(envelopeInfo.getTimestamp());


                            transactionList.add(transaction);
                            transactionMap.put(envelopeInfo.getTransactionID(), "");
                        }
                    }


                }
            }
        }
        xChainBlock.setTransactionList(transactionList);
        //????????????
        //certBlockService.updateLastTime(transactionList);
        return xChainBlock;
    }

    public long getBlockHeight() throws Exception {
        Channel channel = hfClient.getChannel(FabricStartConfig.channelName);

        logger.debug("???????????????"+channel.queryBlockchainInfo().getHeight());

        return channel.queryBlockchainInfo().getHeight();
    }


    /********************************** ?????????????????? ***************************************************/


    //????????????
    public void createChannel(String channelName) throws Exception {
        String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";

        // ??????????????????
        System.setProperty(Config.ORG_HYPERLEDGER_FABRIC_SDK_CONFIGURATION, cxtPath + "config.properties");


        /* ???????????? */
        //String channelName = "mychannel";

        /* ???????????????????????? */
        String chaincodeName = "hashstore";
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/testchaincode";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        // 1. ??????????????????
        System.out.println("?????????");
        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();

        FabricStartConfig fabricConfig = new FabricStartConfig();

        HFClient client = null;
        try {
            client = fabricConfig.clientBuild(buildClientDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 2. ????????????
        System.out.println("?????????");
        Collection<OrderNodeDTO> orderNodeDTOS = new ArrayList<>();
        orderNodeDTOS.add(new OrderNodeDTO(ORDER_NAME, ORDER_GRPC_URL));

        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO(PEER0_ORG1_NAME, PEER0_ORG1_GRPC_URL, PEER0_ORG1_EVENT_URL));
        //peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG1_NAME, PEER1_ORG1_GRPC_URL, PEER1_ORG1_EVENT_URL));

        CreateChannelDTO createChannelDTO = new CreateChannelDTO();
        //??????????????????????????????????????????
        createChannelDTO.setChannelConfigPath(cxtPath + "channel-artifacts/"+channelName+".tx");
        createChannelDTO.setOrderNodeDTOS(orderNodeDTOS);
        createChannelDTO.setPeerNodeDTOS(peerNodeDTOS);
        Channel channel = ApiHandler.createChannel(client, channelName,createChannelDTO,false);// createChannelDTO, false
        System.out.println("????????????????????????,?????????:"+channel.getName());
    }

    //????????????
    //public void jionChannel(HFClient client, Channel channel, PeerNodeDTO peerNodeDTO1) throws Exception {
    public void jionChannel() throws Exception {
        String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";
        FabricStartConfig fabricConfig = new FabricStartConfig();
        // 1. ??????????????????
        System.out.println("?????????");
        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = null;
        try {
            client = fabricConfig.clientBuild(buildClientDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2.??????peer
        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG1_NAME, PEER1_ORG1_GRPC_URL, PEER1_ORG1_EVENT_URL));
        //Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        //peerNodeDTOS.add(peerNodeDTO1);
        //3.??????channel
        Channel channel = ApiHandler.createChannel(client, "mychannel");
        for (PeerNodeDTO peerNodeDTO : peerNodeDTOS) {
            Peer peer = client.newPeer(peerNodeDTO.getNodeName(), peerNodeDTO.getGrpcUrl(), peerNodeDTO.getProperties());
            channel.joinPeer(peer);
            debug("peer??????: %s ?????????????????????.", peerNodeDTO.getNodeName());
        }
        if (!channel.isInitialized()) {
            channel.initialize();
        }
    }
    //????????????
    //public void installChaincode(HFClient client, String chaincodeName) throws Exception {
    public void installChaincode(String chaincodeName) throws Exception {
        String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";
        FabricStartConfig fabricConfig = new FabricStartConfig();
        // 1. ??????????????????
        System.out.println("?????????");
        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = null;
        try {
            client = fabricConfig.clientBuild(buildClientDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ChaincodeID chaincodeID = null;
        String chaincodeVersion = "1.0";
        if(chaincodeName.equals("mycc")){
            String chaincodePath = "github.com/chaincode_example02";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        }else {
            String chaincodePath = "github.com/testchaincode";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();
        }

        //String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";
        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG1_NAME, PEER1_ORG1_GRPC_URL, PEER1_ORG1_EVENT_URL));
        // 3. ??????????????????
        System.out.println("?????????");
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);
    }

    //???????????????
    public void initChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception {
        String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";
        ChaincodeID chaincodeID = null;
        String chaincodeVersion = "1.0";
        if(chaincodeName.equals("mycc")){
            String chaincodePath = "github.com/chaincode_example02";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        }else {
            String chaincodePath = "github.com/testchaincode";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();
        }
        // ?????????????????????
        ExecuteCCDTO initCCDTO = new ExecuteCCDTO.Builder().funcName("init").params(str).chaincodeID(chaincodeID).policyPath(cxtPath + "policy/chaincodeendorsementpolicy.yaml").build();
        ApiHandler.initializeChainCode(client, channel, initCCDTO);
    }

    //????????????
    public void upgradeChaincode(String chaincodeName) throws Exception {
        String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";
        FabricStartConfig fabricConfig = new FabricStartConfig();
        // 1. ??????????????????
        System.out.println("?????????");
        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = null;
        try {
            client = fabricConfig.clientBuild(buildClientDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* ???????????????????????? */
        //String chaincodeName = "mycc";
        ChaincodeID chaincodeID = null;
        String chaincodeVersion = "1.2";    // ?????????????????????
        if(chaincodeName.equals("mycc")){
            String chaincodePath = "github.com/chaincode_example02";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        }else {
            String chaincodePath = "github.com/testchaincode";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();
        }

        // 2. ???????????????????????????
        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO(PEER0_ORG1_NAME, PEER0_ORG1_GRPC_URL, PEER0_ORG1_EVENT_URL));
        //peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG1_NAME, PEER1_ORG1_GRPC_URL, PEER1_ORG1_EVENT_URL));
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);
    }

    //????????????
    //public void invokeChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception {
    public void invokeChaincode(String chaincodeName, String[] str) throws Exception {
        String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";
        /* ???????????? */
        //String channelName = "mychannel";
        FabricStartConfig fabricConfig = new FabricStartConfig();
        // 1. ??????????????????
        System.out.println("?????????");
        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = null;
        try {
            client = fabricConfig.clientBuild(buildClientDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Channel channel = ApiHandler.createChannel(client, "mychannel");
        /* ???????????????????????? */
        //String chaincodeName = "mycc";
        ChaincodeID chaincodeID = null;
        String chaincodeVersion = "1.0";
        if(chaincodeName.equals("mycc")){
            String chaincodePath = "github.com/chaincode_example02";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();
        }else if(chaincodeName.equals("hashstore")){
            String chaincodePath = "github.com/testchaincode";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        }
        // 1. ??????????????????
//        String mspPath = "crypto-config/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp/";
//        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
//                .name("org2.example.com").mspId("Org2MSP").mspPath(mspPath).build();
        //HFClient client = ApiHandler.clientBuild(buildClientDTO);

        // 2. ????????????
        //Channel channel = ApiHandler.createChannel(client, channelName);

        // 3. invoke
        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(str).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, invokeCCDTO);

    }

    //????????????
    //public void queryChaincode(HFClient client, Channel channel, String chaincodeName, String[] str) throws Exception {
    public void queryChaincode(String chaincodeName, String[] str) throws Exception {
        String cxtPath = FabricService.class.getClassLoader().getResource("").getPath()+"fabric/";
        /* ???????????? */
        //String channelName = "mychannel";
        FabricStartConfig fabricConfig = new FabricStartConfig();
        // 1. ??????????????????
        System.out.println("?????????");
        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = null;
        try {
            client = fabricConfig.clientBuild(buildClientDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Channel channel = ApiHandler.createChannel(client, "mychannel");
        /* ???????????????????????? */
        ChaincodeID chaincodeID = null;
        String chaincodeVersion = "1.0";
        //??????????????????????????????????????? ELSE IF?????????
        //??????chaincodeName????????????????????????
        if(chaincodeName.equals("mycc")){

            String chaincodePath = "github.com/chaincode_example02";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        }else if(chaincodeName.equals("hashstore")){

            String chaincodePath = "github.com/testchaincode";
            chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

        }

        ExecuteCCDTO querybCCDTO = new ExecuteCCDTO.Builder().funcName("query").params(str).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, querybCCDTO);
    }





}
