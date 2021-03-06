package com.hyperledger.fabric.sdk.test;

import com.hyperledger.fabric.sdk.entity.dto.EnrollmentDTO;
import com.hyperledger.fabric.sdk.entity.dto.UserContextDTO;
import com.hyperledger.fabric.sdk.exception.FabricSDKException;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.security.PrivateKey;
import java.security.Security;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.hyperledger.fabric.sdk.utils.FileUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Created by Kevin on 2018-08-28 09:27
 */
public class SDKClient {
    static Jedis jedis = new Jedis("127.0.0.1");
    static String cxtPath = SDKClient.class.getClassLoader().getResource("").getPath();
    static {
        jedis.select(0);
        Security.addProvider(new BouncyCastleProvider());
        System.setProperty(Config.ORG_HYPERLEDGER_FABRIC_SDK_CONFIGURATION, cxtPath + "config.properties");
    }

    static String chaincodeName = "mycc";
    static String chaincodeVersion = "1.0";
    static String chaincodePath = "github.com/chaincode_example02";
    static ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();

    public static void main(String[] args) {
        try {
            HFClient client = clientBuild();

//            Channel channel = createChannel(client);
//
//            jedis.set("mychannel".getBytes(), channel.serializeChannel());
//
//            installChainCode(client);
//
//            instantiateChainCode(client);
//
//            queryChainCode(client, channel);

//            channel.serializeChannel(new File("C:\\soft\\channel.txt"));



           /* Channel channel = client.deSerializeChannel(new File("C:\\soft\\channel.txt"));
            System.out.println(channel.getName());
            channel.initialize();
            queryChainCode(client, channel);*/


           Channel channel = client.deSerializeChannel(jedis.get("mychannel".getBytes()));
            channel.initialize();
            queryChainCode(client, channel);
            invokeChainCode(client, channel);
            queryChainCode(client, channel);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void queryChainCode(HFClient client, Channel channel) throws Exception {
        QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
        queryByChaincodeRequest.setArgs(new String[] {"b"});
        queryByChaincodeRequest.setFcn("query");
        queryByChaincodeRequest.setChaincodeID(chaincodeID);

        Collection<ProposalResponse> queryProposals = channel.queryByChaincode(queryByChaincodeRequest, channel.getPeers());

        for (ProposalResponse proposalResponse: queryProposals) {
            String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
            System.out.println("response status: " + proposalResponse.getStatus() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName() + ", payload: " + payload);

            if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                System.out.println("failed message: " + proposalResponse.getMessage() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());
            }
        }
        System.out.println("??????????????????B?????????????????????!!!");
    }

    public static void invokeChainCode(HFClient client, Channel channel) throws Exception {
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setProposalWaitTime(30000);
        transactionProposalRequest.setArgs(new String[] {"a", "b", "5"});
        /**Map<String, byte[]> tm2 = new HashMap<>();
         tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
         tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
         tm2.put("result", ":)".getBytes(UTF_8));
         transactionProposalRequest.setTransientMap(tm2);*/
        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse proposalResponse: transactionPropResp) {
            System.out.println("response status: " + proposalResponse.getStatus() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());

            if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                System.out.println("failed message: " + proposalResponse.getMessage() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());
            }
        }
        // TODO??? ???????????????????????????failed??????

        System.out.println("????????????: A->B???150??????!!!");
        // ?????????orderer??????
        channel.sendTransaction(transactionPropResp).get(50, TimeUnit.SECONDS);
    }

    /**
     * ?????????????????????
     * */
    public static void instantiateChainCode(HFClient client) throws Exception {
        System.out.println("????????????????????? Start...");

        Map<String, byte[]> map = new HashMap<String, byte[]>() {
            {
                put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
                put("method", "InstantiateProposalRequest".getBytes(UTF_8));
            }
        };

        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        // ????????????????????????
        instantiateProposalRequest.setProposalWaitTime(30000);
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setFcn("init");
        instantiateProposalRequest.setArgs(new String[] {"a", "500", "b", "" + 200});
        instantiateProposalRequest.setTransientMap(map);
        /**ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File(cxtPath + "chaincodeendorsementpolicy.yaml"));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);*/
        Channel channel = client.getChannel("mychannel");
        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiateProposalRequest, channel.getPeers());

        Collection<ProposalResponse> failed = new ArrayList<>();

        for (ProposalResponse response: responses) {
            System.out.println("response status: " + response.getStatus() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());

            if (response.getStatus() == ProposalResponse.Status.FAILURE) {
                System.out.println("failed message: " + response.getMessage() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());
                failed.add(response);
            }
        }
        System.out.println("???????????????a??????: 500, ??????b??????: 200.");
        System.out.println("????????????????????? End...");

        if (failed.size() > 0) {
            throw new FabricSDKException("instantiateChainCode failed.");
        }

        System.out.println("?????????????????????orderer???????????? Start...");
        channel.sendTransaction(responses).thenApply(transactionEvent -> {

            try {
                System.out.println("????????????????????????: A->B???150...");
                TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
                transactionProposalRequest.setChaincodeID(chaincodeID);
                transactionProposalRequest.setFcn("invoke");
                transactionProposalRequest.setProposalWaitTime(30000);
                transactionProposalRequest.setArgs(new String[] {"a", "b", "150"});
                /**Map<String, byte[]> tm2 = new HashMap<>();
                tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
                tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
                tm2.put("result", ":)".getBytes(UTF_8));
                transactionProposalRequest.setTransientMap(tm2);*/
                Channel channel1 = client.getChannel("mychannel");
                Collection<ProposalResponse> transactionPropResp = channel1.sendTransactionProposal(transactionProposalRequest, channel1.getPeers());
                for (ProposalResponse proposalResponse: transactionPropResp) {
                    System.out.println("response status: " + proposalResponse.getStatus() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());

                    if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                        System.out.println("failed message: " + proposalResponse.getMessage() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());
                    }
                }
                // TODO??? ???????????????????????????failed??????

                System.out.println("????????????: A->B???150??????!!!");
                // ?????????orderer??????
                return channel.sendTransaction(transactionPropResp).get(50, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return  null;
        }).thenApply(transactionEvent -> {
            String transactionID = transactionEvent.getTransactionID();
            System.out.println("????????????, ???????????????TXID: " + transactionID);

            // ??????????????????
            try {
                System.out.println("????????????????????????B???????????????...");
                QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
                queryByChaincodeRequest.setArgs(new String[] {"b"});
                queryByChaincodeRequest.setFcn("query");
                queryByChaincodeRequest.setChaincodeID(chaincodeID);

                /**Map<String, byte[]> param = new HashMap<String, byte[]>() {
                    {
                        put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
                        put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
                    }
                };
                queryByChaincodeRequest.setTransientMap(param);*/
                Channel channel1 = client.getChannel("mychannel");
                Collection<ProposalResponse> queryProposals = channel1.queryByChaincode(queryByChaincodeRequest, channel1.getPeers());

                for (ProposalResponse proposalResponse: queryProposals) {
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    System.out.println("response status: " + proposalResponse.getStatus() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName() + ", payload: " + payload);

                    if (proposalResponse.getStatus() == ProposalResponse.Status.FAILURE) {
                        System.out.println("failed message: " + proposalResponse.getMessage() + ", isVerified: " + proposalResponse.isVerified() + " from peer: " + proposalResponse.getPeer().getName());
                    }
                }
                System.out.println("??????????????????B?????????????????????!!!");
            } catch (Exception e) {
                System.out.println("????????????????????????," + e.getMessage());
                e.printStackTrace();
            }
            return null;

        }).exceptionally(e -> {
            e.printStackTrace();

            return null;
        }).get(50, TimeUnit.SECONDS);

    }


    /**
     * ??????????????????
     * */
    public static void installChainCode(HFClient client) throws Exception {
        System.out.println("?????????????????? Start...");
        /** ????????????????????????????????? */
        Collection<Peer> peers = new ArrayList<>();
        Properties peerProperties0 = new Properties();
//        File peerFile0 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
//        peerProperties0.setProperty("pemFile", peerFile0.getAbsolutePath());
        peerProperties0.setProperty("hostnameOverride", "peer0.org1.example.com");
        peerProperties0.setProperty("trustServerCertificate", "true");
        peerProperties0.setProperty("sslProvider", "openSSL");
        peerProperties0.setProperty("negotiationType", "TLS");
        peerProperties0.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
        Peer peer0 = client.newPeer("peer0.org1.example.com", "grpc://119.23.XXX.XXX:7051", peerProperties0);
        peers.add(peer0);

        Properties peerProperties1 = new Properties();
//        File peerFile1 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt");
//        peerProperties1.setProperty("pemFile", peerFile1.getAbsolutePath());
        peerProperties1.setProperty("hostnameOverride", "peer1.org1.example.com");
        peerProperties1.setProperty("trustServerCertificate", "true");
        peerProperties1.setProperty("sslProvider", "openSSL");
        peerProperties1.setProperty("negotiationType", "TLS");
        peerProperties1.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);
        Peer peer1 = client.newPeer("peer1.org1.example.com", "grpc://119.23.XXX.XXX:8051", peerProperties1);
        peers.add(peer1);

        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(new File(cxtPath + "chaincodes/sample"));
        installProposalRequest.setChaincodeVersion(chaincodeID.getVersion());

        Collection<ProposalResponse> responses = client.sendInstallProposal(installProposalRequest, peers);

        for (ProposalResponse response: responses) {
            System.out.println("response status: " + response.getStatus() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());

            if (response.getStatus() == ProposalResponse.Status.FAILURE) {
                System.out.println("failed message: " + response.getMessage() + ", isVerified: " + response.isVerified() + " from peer: " + response.getPeer().getName());
            }
        }
        System.out.println("?????????????????? End...");
    }


    /**
     * ?????????????????????
     * */
    public static HFClient clientBuild() throws Exception {
        System.out.println("????????????????????? Start...");
        HFClient client = HFClient.createNewInstance();
        client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());

        String mspPath = "crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        InputStream certFileIS = new FileInputStream(getFile(mspPath + "signcerts", null));
        String cert = new String(IOUtils.toByteArray(certFileIS), UTF_8);

        InputStream keyFileIS = new FileInputStream(getFile(mspPath + "keystore", "_sk"));
        PrivateKey key = getPrivateKeyFromBytes(IOUtils.toByteArray(keyFileIS));

        String orgName = "org1.example.com";
        String mspid = "Org1MSP";
        Enrollment enrollmentDTO = new EnrollmentDTO(cert, key);

        User user = new UserContextDTO(orgName, mspid, enrollmentDTO);
        client.setUserContext(user);

        System.out.println("????????????????????? End...");
        return client;
    }



    /**
     * ????????????
     *  channel.tx
     *  userContext
     *  orderer node
     * */
    public static Channel createChannel(HFClient client) throws Exception {
        System.out.println("???????????? Start...");
        /**
         * ############################################################################################################################################################
         *                                          ????????????
         * ############################################################################################################################################################
         * */
        File channelFile = new File(cxtPath + "channel-artifacts/channel.tx");
        ChannelConfiguration channelConfiguration = new ChannelConfiguration(channelFile);

        String channelName = "mychannel";
        // orderer ???????????????????????????
        Properties orderProperties = new Properties();
//        File ordererFile = new File(cxtPath + "crypto-config/ordererOrganizations/example.com/orderers/orderer.example.com/tls/server.crt");
//        orderProperties.setProperty("pemFile", ordererFile.getAbsolutePath());
        orderProperties.setProperty("hostnameOverride", "orderer.example.com");
        orderProperties.setProperty("trustServerCertificate", "true");
        orderProperties.setProperty("sslProvider", "openSSL");
        orderProperties.setProperty("negotiationType", "TLS");
        orderProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        orderProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
        Orderer orderer = client.newOrderer("orderer.example.com", "grpc://119.23.XXX.XXX:7050", orderProperties);

        Channel channel = client.newChannel(channelName, orderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, client.getUserContext()));
        System.out.println("??????????????????");

        // ?????? peer ??????
        Properties peerProperties0 = new Properties();
//        File peerFile0 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
//        peerProperties0.setProperty("pemFile", peerFile0.getAbsolutePath());
        peerProperties0.setProperty("hostnameOverride", "peer0.org1.example.com");
        peerProperties0.setProperty("trustServerCertificate", "true");
        peerProperties0.setProperty("sslProvider", "openSSL");
        peerProperties0.setProperty("negotiationType", "TLS");
        peerProperties0.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

        Peer peer0 = client.newPeer("peer0.org1.example.com", "grpc://119.23.XXX.XXX:7051", peerProperties0);
        channel.joinPeer(peer0);
        System.out.println("peer0 join channel");

        Properties peerProperties1 = new Properties();
//        File peerFile1 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt");
//        peerProperties1.setProperty("pemFile", peerFile1.getAbsolutePath());
        peerProperties1.setProperty("hostnameOverride", "peer1.org1.example.com");
        peerProperties1.setProperty("trustServerCertificate", "true");
        peerProperties1.setProperty("sslProvider", "openSSL");
        peerProperties1.setProperty("negotiationType", "TLS");
        peerProperties1.put("grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000);

        Peer peer1 = client.newPeer("peer1.org1.example.com", "grpc://119.23.XXX.XXX:8051", peerProperties1);
        channel.joinPeer(peer1);
        System.out.println("peer1 join channel");


        // ??? order ??????????????????
        channel.addOrderer(orderer);


        // ?????? peer - eventHub ??????
        Properties peerHubProperties0 = new Properties();
//        File peerHubFile0 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/server.crt");
//        peerHubProperties0.setProperty("pemFile", peerHubFile0.getAbsolutePath());
        peerHubProperties0.setProperty("hostnameOverride", "peer0.org1.example.com");
        peerHubProperties0.setProperty("trustServerCertificate", "true");
        peerHubProperties0.setProperty("sslProvider", "openSSL");
        peerHubProperties0.setProperty("negotiationType", "TLS");
        peerHubProperties0.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        peerHubProperties0.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});

        EventHub peerHub0 = client.newEventHub("peer0.org1.example.com", "grpc://119.23.XXX.XXX:7053", peerHubProperties0);
        channel.addEventHub(peerHub0);
        System.out.println("peer0 eventHub join channel");

        Properties peerHubProperties1 = new Properties();
//        File peerHubFile1 = new File(cxtPath + "crypto-config/peerOrganizations/org1.example.com/peers/peer1.org1.example.com/tls/server.crt");
//        peerHubProperties1.setProperty("pemFile", peerHubFile1.getAbsolutePath());
        peerHubProperties1.setProperty("hostnameOverride", "peer1.org1.example.com");
        peerHubProperties1.setProperty("trustServerCertificate", "true");
        peerHubProperties1.setProperty("sslProvider", "openSSL");
        peerHubProperties1.setProperty("negotiationType", "TLS");
        peerHubProperties1.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
        peerHubProperties1.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});

        EventHub peerHub1 = client.newEventHub("peer1.org1.example.com", "grpc://119.23.XXX.XXX:8053", peerHubProperties1);
        channel.addEventHub(peerHub1);
        System.out.println("peer1 eventHub join channel");


        channel.initialize();
        System.out.println("?????????????????????");
        /** ############################################################################################################################################################ */

        System.out.println("???????????? End...");
        return channel;
    }




}