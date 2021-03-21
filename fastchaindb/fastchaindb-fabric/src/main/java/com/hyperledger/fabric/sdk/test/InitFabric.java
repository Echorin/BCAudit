package com.hyperledger.fabric.sdk.test;

import com.hyperledger.fabric.sdk.entity.dto.api.*;
import com.hyperledger.fabric.sdk.handler.ApiHandler;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;

import java.util.ArrayList;
import java.util.Collection;

import static com.hyperledger.fabric.sdk.common.Config.*;
import static com.hyperledger.fabric.sdk.common.Config.PEER1_ORG1_EVENT_URL;
import static com.hyperledger.fabric.sdk.common.Config.PEER1_ORG1_GRPC_URL;
import static com.hyperledger.fabric.sdk.logger.Logger.debug;

public class InitFabric {
    public static void main(String[] args) throws Exception {
        String cxtPath = APITest.class.getClassLoader().getResource("").getPath()+"fabric/";
        debug("cxtPath: " + cxtPath);
        /* 通道名称 */
        String channelName = "mychannel";

        /* 智能合约配置信息 */
        String chaincodeName = "hashstore";
        String chaincodeVersion = "1.0";
        String chaincodePath = "github.com/testchaincode";
        ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(chaincodeName).setVersion(chaincodeVersion).setPath(chaincodePath).build();


        // 1. 初始化客户端
        System.out.println("第一步");
        String mspPath = cxtPath+"crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp/";
        BuildClientDTO buildClientDTO = new BuildClientDTO.Builder()
                .name("org1.example.com").mspId("Org1MSP").mspPath(mspPath).build();
        HFClient client = ApiHandler.clientBuild(buildClientDTO);

        // 2. 创建通道
        System.out.println("第二步");
        Collection<OrderNodeDTO> orderNodeDTOS = new ArrayList<>();
        orderNodeDTOS.add(new OrderNodeDTO(ORDER_NAME, ORDER_GRPC_URL));

        Collection<PeerNodeDTO> peerNodeDTOS = new ArrayList<>();
        peerNodeDTOS.add(new PeerNodeDTO(PEER0_ORG1_NAME, PEER0_ORG1_GRPC_URL, PEER0_ORG1_EVENT_URL));
        peerNodeDTOS.add(new PeerNodeDTO(PEER1_ORG1_NAME, PEER1_ORG1_GRPC_URL, PEER1_ORG1_EVENT_URL));

        CreateChannelDTO createChannelDTO = new CreateChannelDTO();
        createChannelDTO.setChannelConfigPath(cxtPath + "channel-artifacts/channel.tx");
        createChannelDTO.setOrderNodeDTOS(orderNodeDTOS);
        createChannelDTO.setPeerNodeDTOS(peerNodeDTOS);
        Channel channel = ApiHandler.createChannel(client, channelName);// createChannelDTO, false


        // 3. 安装智能合约
        System.out.println("第三步");
        InstallCCDTO installCCDTO = new InstallCCDTO.Builder().chaincodeID(chaincodeID).chaincodeSourceLocation(cxtPath + "chaincodes/sample").peerNodeDTOS(peerNodeDTOS).build();
        ApiHandler.installChainCode(client, installCCDTO);


        // 5. 初始化智能合约
        System.out.println("第四步");
        ExecuteCCDTO initCCDTO = new ExecuteCCDTO.Builder().funcName("init").params(new String[] {""}).chaincodeID(chaincodeID).policyPath(cxtPath + "policy/chaincodeendorsementpolicy.yaml").build();
        ApiHandler.initializeChainCode(client, channel, initCCDTO);



        //下面为重复操作

        // 6. 测试智能合约invoke
        System.out.println("第五步");
        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[] {"hn2", "haonan"}).chaincodeID(chaincodeID).build();
        ApiHandler.invokeChainCode(client, channel, invokeCCDTO);


        // 7. 测试智能合约query
        System.out.println("第六步");
        ExecuteCCDTO invokeCCDTO1 = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"hn2"}).chaincodeID(chaincodeID).build();
        ApiHandler.queryChainCode(client, channel, invokeCCDTO1);
    }


}
