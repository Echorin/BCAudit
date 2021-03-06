package com.oschain.fastchaindb.common.config;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.entity.dto.api.*;
import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import com.hyperledger.fabric.sdk.handler.ApiHandler;
import com.oschain.fastchaindb.chainsql.service.CertBlockService;
import com.oschain.fastchaindb.common.utils.PropertiesUtil;
import com.oschain.fastchaindb.fabric.dao.FabricMonitorMapper;
import com.oschain.fastchaindb.fabric.dao.FabricMonitorPeerMapper;
import com.oschain.fastchaindb.fabric.dao.FabricMonitorOrderMapper;
import com.oschain.fastchaindb.fabric.dto.FabricMonitorDto;
import com.oschain.fastchaindb.fabric.model.FabricMonitor;
import com.oschain.fastchaindb.fabric.model.FabricMonitorOrder;
import com.oschain.fastchaindb.fabric.model.FabricMonitorPeer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.*;

import static com.hyperledger.fabric.sdk.common.Constants.REDIS_PREFIX;
import static com.hyperledger.fabric.sdk.logger.Logger.debug;
import static com.hyperledger.fabric.sdk.logger.Logger.warn;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getFile;
import static com.hyperledger.fabric.sdk.utils.FileUtils.getPrivateKeyFromBytes;
import static java.nio.charset.StandardCharsets.UTF_8;

@Configuration
public class FabricStartConfig {
    private static final Logger logger = LoggerFactory.getLogger(FabricStartConfig.class);


    @Autowired
    CertBlockService certBlockService;

    @Autowired
    FabricMonitorMapper fabricMonitorMapper;

    @Autowired
    FabricMonitorPeerMapper fabricMonitorPeerMapper;

    @Autowired
    FabricMonitorOrderMapper fabricMonitorOrderMapper;

    /**
     * ????????????, ??????: S
     */
    private static final Integer TIME_OUT = 50;
    /**
     * channel redis??????key?????????
     */
    private static final String CHANNEL_CACHE = REDIS_PREFIX + "channel:";
    private static final String REDIS_SEPERATOR = "@";


//    @Value("${redis.host}")
//    private String host;
//
//    @Value("${redis.port}")
//    private int port;

    public static ChaincodeID chaincodeID;
    public static String channelName;
    public static String chaincodeName;


    private static String clientName;
    private static String channelConfigPath;
    private static String chaincodeVersion;
    private static String chaincodePath;
    private static String chaincodeSource;
    private static String chaincodePolicy;
    private static String mspId;
    private static String mspPath;
    private static String peerName;
    private static String peerIp;
    private static String orderName;
    private static String orderIp;

    private static Map<String,String> channelPeer=new HashMap<>();
    private static Map<String,String> channelOrder=new HashMap<>();
    private static Map<String,String> channelChaincode=new HashMap<>();


    private static String cxtPath = FabricStartConfig.class.getClassLoader().getResource("").getPath() + "fabric/";

    /* ???????????? */
//    public static String channelName = "mychannel";
//    public static String chaincodeName = "hashstore";//"mycc";//


    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private DataSource dataSource;

    @Bean(name = "hfClient")
    public HFClient hfClient() throws Exception {

        //????????????
        clientName = PropertiesUtil.getProperties("client.name");

        channelName = PropertiesUtil.getProperties("channel.name");
        channelConfigPath = PropertiesUtil.getProperties("channel.config.path");

        chaincodeName = PropertiesUtil.getProperties("chaincode.name");
        chaincodeVersion = PropertiesUtil.getProperties("chaincode.version");
        chaincodePath = PropertiesUtil.getProperties("chaincode.path");
        chaincodeSource = PropertiesUtil.getProperties("chaincode.source");
        chaincodePolicy = PropertiesUtil.getProperties("chaincode.policy");

        mspId = PropertiesUtil.getProperties("msp.id");
        mspPath = PropertiesUtil.getProperties("msp.path");

        peerName = PropertiesUtil.getProperties("peer.name");
        peerIp = PropertiesUtil.getProperties("peer.ip");

        orderName = PropertiesUtil.getProperties("order.name");
        orderIp = PropertiesUtil.getProperties("order.ip");


        // ????????????????????????
        String cxtPath = FabricStartConfig.class.getClassLoader().getResource("").getPath() + "fabric/";
        System.out.println("--------------------");
        System.out.println("cxtPath"+cxtPath);
        // ??????????????????
        System.setProperty(Config.ORG_HYPERLEDGER_FABRIC_SDK_CONFIGURATION, cxtPath + "config.properties");


//        /* ???????????? */
//        String channelName = "mychannel";
//        /* ???????????????????????? */
//        String chaincodeName = "hashstore";
//        String chaincodeVersion = "1.0";
//        String chaincodePath = "github.com/testchaincode";

        // 1. ??????????????????
        System.out.println("?????????");
        //String mspPath = cxtPath +mspPath;// "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name(clientName).mspId(mspId).mspPath(cxtPath + mspPath).build();

        HFClient client = this.clientBuild(buildClientDTO);

        // 2. ????????????
        System.out.println("?????????");

        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        //peerNodeDTOS.add(new PeerNodeDTO(PEER2_ORG1_NAME, PEER2_ORG1_GRPC_URL, PEER2_ORG1_EVENT_URL));
        String[] peerNames = peerName.split(",");
        String[] peerIps = peerIp.split(",");
        for (int i = 0; i < peerNames.length; i++) {
            peerNodeDTOS.add(new PeerNodeDTO(peerNames[i], "grpc://" + peerIps[i] + ":7051", "grpc://" + peerIps[i] + ":7053", peerIps[i]));
        }

        Collection<OrderNodeDTO> orderNodeDTOS = new ArrayList<>();
        //orderNodeDTOS.add(new OrderNodeDTO(ORDER_NAME, ORDER_GRPC_URL));
        String[] orderNames = peerName.split(",");
        String[] orderIps = peerIp.split(",");
        for (int i = 0; i < orderNames.length; i++) {
            orderNodeDTOS.add(new OrderNodeDTO(orderNames[i], "grpc://" + orderIps[i] + ":7050",orderIps[i]));
        }

        String mspId = client.getUserContext().getMspId();
        if (StringUtils.isEmpty(mspId)) throw new FabricSDKException("mspId must not to be empty.");


        int fabricMonitorId = 0;
        Map<String, Object> mapField;

        FabricMonitor fabricMonitor;
        fabricMonitor = new FabricMonitor();
        fabricMonitor.setChannelName(channelName);

        List<FabricMonitorDto> list = fabricMonitorMapper.getFabricMonitorList(fabricMonitor);

        //???????????????
        String channelPeerKey;
        String channelOrderKey;
        String channelChaincodeKey;

        Channel channel;
        Orderer orderer;
        if (list.size() == 0) {
            channelConfigPath = cxtPath + channelConfigPath;//"channel-artifacts/channel.tx";
            File channelFile = new File(channelConfigPath);
            ChannelConfiguration channelConfiguration = new ChannelConfiguration(channelFile);

            //???????????????Order??????
            OrderNodeDTO orderNodeDTO = orderNodeDTOS.iterator().next();
            orderer = client.newOrderer(orderNodeDTO.getNodeName(), orderNodeDTO.getGrpcUrl(), orderNodeDTO.getProperties());
            channel = client.newChannel(channelName, orderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, client.getUserContext()));

            //????????????
            fabricMonitor = new FabricMonitor();
            fabricMonitor.setChannelName(channelName);
            fabricMonitor.setOrgName(mspId);
            fabricMonitorMapper.insert(fabricMonitor);
            //????????????
            fabricMonitorId = fabricMonitor.getId();

        } else {
            for (FabricMonitorDto fabricMonitorDto : list) {

                //??????+??????
                channelPeerKey = fabricMonitorDto.getChannelName();
                if (fabricMonitorDto.getPeerIp() != null) {
                    channelPeerKey = channelPeerKey + "|" + fabricMonitorDto.getPeerIp();
                }

                //??????+??????+????????????
                channelChaincodeKey = channelPeerKey;
                if (fabricMonitorDto.getChainCodeName() != null) {
                    channelChaincodeKey = channelChaincodeKey + "|" + fabricMonitorDto.getChainCodeName();
                }

                //??????+Order??????
                channelOrderKey=fabricMonitorDto.getChannelName();
                if (fabricMonitorDto.getOrderIp() != null) {
                    channelOrderKey = channelOrderKey + "|" + fabricMonitorDto.getOrderIp();
                }

                channelPeer.put(channelPeerKey, "");
                channelOrder.put(channelOrderKey,"");
                channelChaincode.put(channelChaincodeKey, "");
            }

            fabricMonitorId=list.get(0).getId();
            channel = client.newChannel(channelName);
        }

        if (!channel.isInitialized()) {
            channel.initialize();
        }

        debug("channel.isInitialized() = ", channel.isInitialized());

        //??????orderer
        for (OrderNodeDTO orderNodeDTO : orderNodeDTOS) {
            orderer = client.newOrderer(orderNodeDTO.getNodeName(), orderNodeDTO.getGrpcUrl(), orderNodeDTO.getProperties());
            channel.addOrderer(orderer);
            debug("order??????: %s ?????????????????????.", orderer.getName());

            channelOrderKey = channelName + "|" + orderNodeDTO.getIp();
            if(!channelOrder.containsKey(channelOrderKey)){
                //????????????
                FabricMonitorOrder fabricMonitorOrder = new FabricMonitorOrder();
                fabricMonitorOrder.setFabricMonitorId(fabricMonitorId);
                fabricMonitorOrder.setOrderName(orderNodeDTO.getNodeName());
                fabricMonitorOrder.setOrderIp(orderNodeDTO.getIp());
                fabricMonitorOrderMapper.insert(fabricMonitorOrder);
            }

        }

        //??????peer
        Collection<Peer> chaincodePeers = new ArrayList<>();
        for (PeerNodeDTO peerNodeDTO : peerNodeDTOS) {
            Peer peer = client.newPeer(peerNodeDTO.getNodeName(), peerNodeDTO.getGrpcUrl(), peerNodeDTO.getProperties());

            channelPeerKey = channelName + "|" + peerNodeDTO.getIp();
            channelChaincodeKey = channelName + "|" + peerNodeDTO.getIp() + "|" + chaincodeName;

            if (!channelChaincode.containsKey(channelChaincodeKey)) {
                chaincodePeers.add(peer);
            }

            if (!channelPeer.containsKey(channelPeerKey)) {
                channel.joinPeer(peer);

                if (fabricMonitorId == 0) {
                    throw new FabricSDKException(peerNodeDTO.getNodeName() + "fabricMonitorId??????.");
                }

                //????????????
                FabricMonitorPeer fabricMonitorPeer = new FabricMonitorPeer();
                fabricMonitorPeer.setFabricMonitorId(fabricMonitorId);
                fabricMonitorPeer.setPeerName(peerNodeDTO.getNodeName());
                fabricMonitorPeer.setPeerIp(peerNodeDTO.getIp());
                fabricMonitorPeerMapper.insert(fabricMonitorPeer);

            } else {
                channel.addPeer(peer);
            }

            debug("peer??????: %s ?????????????????????.", peerNodeDTO.getNodeName());
        }

        debug("???????????? End, channelName: %s, isInitialized: %b.", channelName, channel.isInitialized());

        chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();
        if (chaincodePeers.size() > 0) {
            // 3. ??????????????????
//            System.out.println("?????????");
            InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + chaincodeSource).peerNodeDTOS(peerNodeDTOS).build();

            debug("?????????????????? Start, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
            InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
            installProposalRequest.setChaincodeID(installCCDTO.getChaincodeID());
            installProposalRequest.setChaincodeSourceLocation(new File(installCCDTO.getChaincodeSourceLocation()));
            installProposalRequest.setChaincodeVersion(installCCDTO.getChaincodeID().getVersion());

            Collection<ProposalResponse> installProposals = client.sendInstallProposal(installProposalRequest, chaincodePeers);
            if (ApiHandler.checkResult(installProposals, false)) {
                debug("?????????????????? End, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());

                //????????????
                FabricMonitorPeer fabricMonitorPeer = new FabricMonitorPeer();
                fabricMonitorPeer.setChainCodeName(chaincodeName);

                Wrapper<FabricMonitorPeer> wrapper;
                for (Peer peer : chaincodePeers) {
                    wrapper = new EntityWrapper<>();
                    wrapper.eq("fabric_monitor_id", fabricMonitorId);
                    wrapper.eq("peer_name", peer.getName());

                    fabricMonitorPeerMapper.update(fabricMonitorPeer, wrapper);
                }
            } else {
                debug("???????????????????????????, chaincode name: %s, chaincode path: %s.", chaincodeID.getName(), chaincodeID.getPath());
            }

            // 5. ?????????????????????
            System.out.println("?????????");
            ExecuteCCDTO initCCDTO = new ExecuteCCDTO.Builder().funcName("init").params(new String[]{""}).chaincodeID(chaincodeID).policyPath(cxtPath + chaincodePolicy).build();
            ApiHandler.initializeChainCode(client, channel, initCCDTO);
        }


        // 6. ??????????????????invoke
        System.out.println("?????????");
        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[]{"test", "hello blockchain"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, invokeCCDTO);

        // 7. ??????????????????query
        System.out.println("?????????");
        ExecuteCCDTO invokeCCDTO1 = new ExecuteCCDTO.Builder().funcName("query").params(new String[]{"test"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, invokeCCDTO1);

        return client;
    }

//    @Bean(name = "chaincodeID")
//    @DependsOn("hfClient")
//    public ChaincodeID chaincodeID() {
//
//        /* ???????????????????????? */
//        String chaincodeVersion = "1.0";
//        String chaincodePath = "github.com/testchaincode";
//        //String chaincodePath = "github.com/chaincode_example02";
//        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();
//
//        return chaincodeID;
//    }

//    @Bean(name = "hfClient")
//    @DependsOn("fabricInit")
//    public HFClient hfClient() {
//
//        //Jedis jedis = jedisPool().getResource();
//
//        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
//        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
//                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
//
//        HFClient client = null;
//        try {
//            client = this.clientBuild(buildClientDTO);
//            this.createChannel(client, channelName,null,true);//????????????
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return client;
//    }

    /**
     * Star - ?????????????????????
     *
     * @param buildClientDTO {@link BuildClientDTO}
     * @return HFClient
     * @throws Exception e
     */
    public HFClient clientBuild(BuildClientDTO buildClientDTO) throws Exception {
        debug("??????Hyperledger Fabric??????????????? Start...");
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

//        String mspPath = buildClientDTO.getMspPath();
//        Modify by Mrsu //?????????????????????????????????
//        String mspPath = "/root/fabric/msp/";


        String relativelyPath=System.getProperty("user.dir");
        String mspPath = relativelyPath + "/fastchaindb-fabric/msp/";
        System.out.println("mspPath???"+mspPath);

        File certFile, keyFile;
        if (StringUtils.isNotEmpty(mspPath)) {
            debug("*******************");
            certFile = getFile(mspPath + "signcerts", null);
            debug("certFile" + certFile);
            keyFile = getFile(mspPath + "keystore", "_sk");
            debug("keyFile" + keyFile);
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
        debug("??????Hyperledger Fabric??????????????? End!!!");
        return client;
    }


    /**
     * ????????????
     *
     * @param key ???
     * @return ???
     */
    public byte[] get(byte[] key) {
        byte[] value = null;
        Jedis jedis = null;
        try {
            jedis = this.getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("get {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * ????????????
     *
     * @param key          ???
     * @param value        ???
     * @param cacheSeconds ???????????????0????????????
     * @return
     */
    public String set(byte[] key, byte[] value) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = this.getResource();
            result = jedis.set(key, value);
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("set {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * ????????????
     *
     * @return
     * @throws JedisException
     */
    public Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
//			logger.debug("getResource.", jedis);
        } catch (JedisException e) {
            logger.warn("getResource.", e);
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    /**
     * ????????????
     *
     * @param jedis
     */
    public void returnBrokenResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnBrokenResource(jedis);
        }
    }

    /**
     * ????????????
     *
     * @param jedis
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

}