package com.oschain.fastchaindb.eth.api;

import com.oschain.fastchaindb.blockchain.dto.*;
import com.oschain.fastchaindb.common.ResultDTO;
import com.oschain.fastchaindb.common.utils.UUIDUtil;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.eth.EthereumInstance;
import com.oschain.fastchaindb.eth.service.EthereumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1")
public class ApiV1Controller {
    private static final Logger logger = LoggerFactory.getLogger(ApiV1Controller.class);

    @Autowired
    EthereumInstance ethereumInstance;

    /**
     * 生成帐号
     */
    @RequestMapping("/createAccount")
    @ResponseBody
    public JsonResult createAccount(@RequestBody String password) {
        FCDBCredential credential=new FCDBCredential();//GsonUtil.gsonToBean(args,Credential.class);
        return JsonResult.ok("ok").put("pairkey", ethereumInstance.createPairKey());
    }

    /**
     * 创建交易
     */
    @ResponseBody
    @RequestMapping("/sendTransaction")
    public ResultDTO<String> sendTransaction() throws Exception {
        FCDBAsset asset = new FCDBAsset();
        asset.setTransactionData("0x360cce7cc580f5edfa20a765583be6b2cf8228c1638ca4c1b6810ed86bbf3b06");
//      logger.debug(body);
        String transactionid = ethereumInstance.sendTransaction(asset);
        //TransactionData transactionData=GsonUtil.gsonToBean(args,TransactionData.class);

        return new ResultDTO(transactionid);//JsonResult.ok("ok").put("transactionid",transactionid);
    }

    /**
     * 通过TransactionId查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getChainByTransactionId", method = RequestMethod.GET)
    public ResultDTO<FCDBBlock> getChainByTransactionId() throws Exception  {
        //XChainBlock xChainBlock = XChainFactory.create("eth").getXChainByTransactionId(transationQuery);
        FCDBBlock xChainBlock = ethereumInstance.getFCDBByBlockHash("0xd5e1812a413d5433dc734e2d3e0115b008baa41b16202fc9f9e1f35508a2274a");
        System.out.println(xChainBlock.getBlockHash());
        return new ResultDTO(xChainBlock.getBlockHash());
    }

    /**
     * 通过BlockHeight查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getChainByBlockHeight", method = RequestMethod.GET)
    public ResultDTO<FCDBBlock> getChainByBlockHeight() throws Exception  {
        //XChainBlock xChainBlock = XChainFactory.create("eth").getXChainByBlockHeight(transationQuery);

        FCDBBlock xChainBlock = ethereumInstance.getFCDBByBlockHeight(734);
        return new ResultDTO(xChainBlock);
    }

    /**
     * 通过BlockHash查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getChainByBlockHash", method = RequestMethod.GET)
    public ResultDTO<FCDBBlock> getChainByBlockHash() throws Exception  {
        // XChainBlock xChainBlock = XChainFactory.create("eth").getXChainByBlockHeight(transationQuery);
        FCDBBlock xChainBlock = EthereumService.getBlockEthBlockByblockHash("0x6ec2f5b80633a59da6abbe04c281faa26f6d49d24d61b4a1476abf35fa62038a");
        return new ResultDTO(xChainBlock);
    }

    /**
     * 查询所有交易
     */
    @ResponseBody
    @RequestMapping(value = "/getChainInfo", method = RequestMethod.GET)
    public ResultDTO<FCDBBlock> getChainInfo(@RequestBody FCDBTransationQuery transationQuery) throws Exception  {
        FCDBBlock xChainBlock = ethereumInstance.getFCDBByBlockHeight(transationQuery.getBlockHeight());
        return new ResultDTO(xChainBlock);
    }


    /**
     * 创建资产
     */
    @ResponseBody
    @RequestMapping("/invoke")
    public ResultDTO<String> invoke() throws Exception  {

//        String key = "key";
//        String value = "hello";
//        EHCacheUtil.put("mytest", key, value);
//        System.out.println(EHCacheUtil.get("mytest", key));
//
//
//        Channel channel =hfClient.getChannel(FabricConfig.channelName);
//
//        // 6. 测试智能合约invoke
//        System.out.println("第六步");
//        ExecuteCCDTO invokeCCDTO = new ExecuteCCDTO.Builder().funcName("invoke").params(new String[] {"hn2", "haonan"}).chaincodeID(chaincodeID).build();
//        ApiHandler.invokeChainCode(hfClient, channel, invokeCCDTO);
//
//
//        // 7. 测试智能合约query
//        System.out.println("第七步");
//        ExecuteCCDTO invokeCCDTO1 = new ExecuteCCDTO.Builder().funcName("query").params(new String[] {"hn2"}).chaincodeID(chaincodeID).build();
//        ApiHandler.queryChainCode(hfClient, channel, invokeCCDTO1);

        //TransactionData transactionData=GsonUtil.gsonToBean(args,TransactionData.class);

//        TransactionData transactionData=new TransactionData();
//        transactionData.setDataKey("test");
//        transactionData.setDataHash("hello fabric");

        FCDBAsset asset=new FCDBAsset();
        asset.setTransactionData(UUIDUtil.randomUUID32());
        String transactionid = ethereumInstance.sendTransaction(asset);

        return new ResultDTO(transactionid);//JsonResult.ok("ok").put("transactionid",transactionid);
    }


    /**
     * 查询
     */
    @ResponseBody
    @RequestMapping("/query")
    public ResultDTO<FCDBBlock> query(String transactionid) throws Exception {
        FCDBTransationQuery transationQuery=new FCDBTransationQuery();
        transationQuery.setTransactionId(transactionid);
        FCDBBlock xChainBlock = ethereumInstance.getFCDBByTransactionId(transactionid);
        return new ResultDTO(xChainBlock);
    }
}
