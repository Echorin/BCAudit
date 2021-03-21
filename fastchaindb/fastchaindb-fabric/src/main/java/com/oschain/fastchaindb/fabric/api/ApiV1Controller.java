package com.oschain.fastchaindb.fabric.api;

import com.oschain.fastchaindb.blockchain.constants.TransactionType;
import com.oschain.fastchaindb.blockchain.dto.*;
import com.oschain.fastchaindb.common.ResultDTO;
import com.oschain.fastchaindb.common.config.FabricStartConfig;
import com.oschain.fastchaindb.common.utils.GsonUtil;
import com.oschain.fastchaindb.common.utils.UUIDUtil;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.fabric.FabricInstance;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/api/v1")
public class ApiV1Controller {
    private static final Logger logger = LoggerFactory.getLogger(ApiV1Controller.class);

    //@Autowired
    //FabricService fabricService;

    @Autowired
    FabricInstance fabricInstance;


    /**
     * 生成帐号
     */
    @RequestMapping("/createPairKey")
    @ResponseBody
    public JsonResult createPairKey(String args) {

        ChaincodeID chaincodeID = FabricStartConfig.chaincodeID;

        FCDBCredential credential=new FCDBCredential();//GsonUtil.gsonToBean(args,Credential.class);
        return JsonResult.ok("ok").put("pairkey",fabricInstance.createPairKey());
    }

    /**
     * 创建交易
     */
    @ResponseBody
    @RequestMapping("/sendTransaction")
    public ResultDTO<String> sendTransaction(@RequestBody FCDBAsset asset) throws Exception {

//      logger.debug(body);
        logger.debug(asset.getId());
        logger.debug(asset.getTransactionType());
        logger.debug(asset.getTransactionData());
        logger.debug(asset.getAccessKey());
        String transactionid = fabricInstance.sendTransaction(asset);
        //TransactionData transactionData=GsonUtil.gsonToBean(args,TransactionData.class);

        return new ResultDTO(transactionid);//JsonResult.ok("ok").put("transactionid",transactionid);
    }

    /**
     * 通过TransactionId查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getChainByTransactionId", method = RequestMethod.POST)
    public ResultDTO<FCDBBlock> getChainByTransactionId(@RequestBody FCDBTransationQuery transationQuery) throws Exception  {
        FCDBBlock xChainBlock = fabricInstance.getFCDBByTransactionId(transationQuery.getTransactionId());
        return new ResultDTO(xChainBlock);
    }

    /**
     * 通过TransactionData查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getChainByTransactionData", method = RequestMethod.POST)
    public ResultDTO<List<FCDBBlock>> getChainByTransactionData(@RequestBody FCDBTransationQuery transationQuery) throws Exception  {
        List<FCDBBlock> fcdbBlockList = fabricInstance.getFCDBByTransactionData(transationQuery);
        return new ResultDTO(fcdbBlockList);
    }

    /**
     * 通过BlockHeight查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getChainByBlockHeight", method = RequestMethod.POST)
    public ResultDTO<FCDBBlock> getChainByBlockHeight(@RequestBody FCDBTransationQuery transationQuery) throws Exception  {
        FCDBBlock xChainBlock = fabricInstance.getFCDBByBlockHeight(transationQuery.getBlockHeight());
        return new ResultDTO(xChainBlock);
    }

    /**
     * 通过BlockHash查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getChainByBlockHash", method = RequestMethod.POST)
    public ResultDTO<FCDBBlock> getChainByBlockHash(@RequestBody FCDBTransationQuery transationQuery) throws Exception  {
        FCDBBlock xChainBlock = fabricInstance.getFCDBByBlockHash(transationQuery.getBlockHash());
        return new ResultDTO(xChainBlock);
    }


    /******************************************** 以下为测试代码 ************************************************************************/

    /**
     * 创建通道
     */
    @ResponseBody
    @RequestMapping("/createChannel")
    public String createChannel(String channelName) throws Exception {

        fabricInstance.createChannel(channelName);
        return "success";

    }

    /**
     * 加入通道
     */
    @ResponseBody
    @RequestMapping("/join")
    public String joinChannel(String channelName) throws Exception {

        fabricInstance.jionChannel(null,null,null);
        return "success";

    }

    /**
     * 安装合约
     */
    @ResponseBody
    @RequestMapping("/install")
    public String installChaincode(String channelName) throws Exception {

        fabricInstance.installChaincode(null,channelName);
        return "success";

    }

    /**
     * 执行合约
     */
    @ResponseBody
    @RequestMapping("/invokeCC")
    public String invokeChaincode(String channelName) throws Exception {
        String[] str = {"hao","nan"};
        fabricInstance.invokeChaincode(null,null,channelName,str);
        return "success";

    }

    /**
     * 查询合约
     */
    @ResponseBody
    @RequestMapping("/queryCC")
    public String queryChaincode(String channelName) throws Exception {
        String[] str = {"hao"};
        fabricInstance.queryChaincode(null,null,channelName,str);
        return "success";

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
        String transactionid = fabricInstance.sendTransaction(asset);

        return new ResultDTO(transactionid);//JsonResult.ok("ok").put("transactionid",transactionid);
    }


    /**
     * 查询
     */
    @ResponseBody
    @RequestMapping("/query")
    public ResultDTO<FCDBBlock> query(String transactionid) throws Exception {
//        FCDBTransationQuery transationQuery=new FCDBTransationQuery();
//        transationQuery.setTransactionId(transactionid);
        FCDBBlock xChainBlock = fabricInstance.getFCDBByTransactionId(transactionid);
        return new ResultDTO(xChainBlock);

    }


    /**
     * 查询
     */
    @ResponseBody
    @RequestMapping("/getBlockHeight")
    public ResultDTO<Long> getBlockHeight() throws Exception {
//        FCDBTransationQuery transationQuery=new FCDBTransationQuery();
//        transationQuery.setTransactionId(transactionid);
        long blockHeight = fabricInstance.getFCDBBlockHeight();
        return new ResultDTO(blockHeight);

    }


    /**
     * 创建交易
     */
    @ResponseBody
    @RequestMapping("/sendTest")
    public ResultDTO<String> sendTest(String data) throws Exception {

//      logger.debug(body);
        FCDBAsset asset=new FCDBAsset();
        //asset.setTransactionData(data);
        asset.setId("test");
        asset.setTransactionType(TransactionType.ARCHIVES.toString());

        FCDBAssetKey fcdbAssetKey=new FCDBAssetKey();
        fcdbAssetKey.setBlockBody("aa");
        fcdbAssetKey.setBlockKey1("key1");
        fcdbAssetKey.setBlockKey2("key2");
        fcdbAssetKey.setBlockKey3("key3");
        fcdbAssetKey.setBlockKey4("key4");
        fcdbAssetKey.setBlockKey5("key5");

        asset.setTransactionData(GsonUtil.gsonString(fcdbAssetKey));



        String transactionid = fabricInstance.sendTransaction(asset);
        //TransactionData transactionData=GsonUtil.gsonToBean(args,TransactionData.class);

        return new ResultDTO(transactionid);//JsonResult.ok("ok").put("transactionid",transactionid);
    }

    /**
     * 通过TransactionId查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getTest")
    public ResultDTO<FCDBBlock> getTest(String transactionId) throws Exception  {

//        FCDBTransationQuery transationQuery=new FCDBTransationQuery();
//        transationQuery.setTransactionId(transactionId);

        FCDBBlock xChainBlock = fabricInstance.getFCDBByTransactionId(transactionId);
        return new ResultDTO(xChainBlock);
    }

    /**
     * 通过TransactionId查询交易区块信息
     */
    @ResponseBody
    @RequestMapping(value = "/getTestList")
    public ResultDTO<List<FCDBBlock>> getTestList(String transactionData) throws Exception  {

        FCDBTransationQuery transationQuery=new FCDBTransationQuery();
        transationQuery.setTransactionType(TransactionType.ARCHIVES.toString());

        FCDBAssetKey fcdbAssetKey=new FCDBAssetKey();
        fcdbAssetKey.setBlockBody("aa");
//        fcdbAssetKey.setBlockKey1("key1");
//        fcdbAssetKey.setBlockKey2("key2");
//        fcdbAssetKey.setBlockKey3("key3");
//        fcdbAssetKey.setBlockKey4("key4");
        fcdbAssetKey.setBlockKey5("key5");

        transationQuery.setTransactionData(GsonUtil.gsonString(fcdbAssetKey));



        List<FCDBBlock> xChainBlock = fabricInstance.getFCDBByTransactionData(transationQuery);

        return new ResultDTO(xChainBlock);
    }

}
