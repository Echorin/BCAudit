package com.oschain.fastchaindb.system.controller;

import com.oschain.fastchaindb.blockchain.dto.AssetKey;
import com.oschain.fastchaindb.blockchain.dto.BlockChain;
import com.oschain.fastchaindb.blockchain.dto.TransactionQueryDTO;
import com.oschain.fastchaindb.cert.service.CertFileService;
import com.oschain.fastchaindb.client.FastChainDBClient;
import com.oschain.fastchaindb.client.common.utils.HttpCallback;
import com.oschain.fastchaindb.client.constants.TransactionType;
import com.oschain.fastchaindb.client.dto.ResultDTO;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.common.utils.GsonUtil;
import com.oschain.fastchaindb.common.utils.RedisUtil;
import com.oschain.fastchaindb.common.utils.RedissonUtil;
import com.oschain.fastchaindb.cert.model.CertFile;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    private CertFileService sysFileService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private FastChainDBClient xChainClient;


    @RequestMapping(value = "/getTran", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult getTran() {

        AssetKey assetKey=new AssetKey();
        assetKey.setBlockKey1("key1");

        TransactionQueryDTO transactionQueryDTO=new TransactionQueryDTO();
        transactionQueryDTO.setTransactionData(GsonUtil.gsonString(assetKey));
        transactionQueryDTO.setTransactionType(TransactionType.ARCHIVES.toString());

        ResultDTO<List<BlockChain>> listResultDTO = xChainClient.getChainByTransactionData(transactionQueryDTO);

        List<BlockChain> list = listResultDTO.getData();

        return JsonResult.ok("????????????").put("data",list);

    }


    @RequestMapping(value = "/xchain", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult xchain() {
//        return JsonResult.ok("????????????");
        return JsonResult.ok("????????????").put("data", xChainClient.createPairKey("{'chaintype':'fabric'}"));
    }


    @RequestMapping(value = "/xchainsend1", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult xchainsend1() {

        xChainClient.sendTransaction("{\"assetData\":1,\"metaData\":2}", new HttpCallback() {
            @Override
            public void success(String response) {

                System.out.print("success:" + response);
            }

            @Override
            public void error(String response) {
                System.out.print("error:" + response);
            }
        });

        return JsonResult.ok("????????????");

    }

    @RequestMapping(value = "/xchainsend2", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult xchainsend2() {
        return JsonResult.ok("????????????");//.put("data", xChainClient.sendTransaction("{\"accessId\":1,\"accessKey\":2,\"data\":\"123\"}"));
    }

    @RequestMapping(value = "/xchainsend3", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult xchainsend3(String transactionId) {
        return JsonResult.ok("????????????");//.put("data", xChainClient.queryXChainBlockByTranID("{\"accessId\":1,\"accessKey\":2,\"transactionId\":\""+transactionId+"\"}").getData());
    }


    /**
     * ???????????????
     */
    @ApiOperation(value = "????????????????????????", notes = "??????url???id???????????????????????????")
    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public JsonResult save() {
        CertFile sysFile = new CertFile();
        sysFile.setFileId("1");
        int row = sysFileService.save(sysFile);
        return JsonResult.ok("????????????");
    }

    @RequestMapping(value = "/redis", method = RequestMethod.GET)
    @ResponseBody
    public String redis() {

        String str2 = "22";
        redisUtil.set("key2", "aa");
        str2 = redisUtil.get("key2").toString();
        System.out.println(str2);

        //????????????
        String redisKey = "token";
        RLock lock = RedissonUtil.getRLock(redissonClient, redisKey);
        boolean isLock = RedissonUtil.getRLockExpireTime(lock, 10L);
        if (isLock) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // ???????????????????????????????????????
            RedissonUtil.unRLock(lock);
        } else {
            str2 = "??????????????????????????????";
        }

        System.out.println(str2);

        return str2;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {

        String str2 = "22";
        redisUtil.set("key2", "aa");
        str2 = redisUtil.get("key2").toString();
        System.out.println(str2);

        String redisKey = "token";
        RLock lock = RedissonUtil.getRLock(redissonClient, redisKey);
        boolean isLock = RedissonUtil.getRLockExpireTime(lock, 10L);
        if (isLock) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // ???????????????????????????????????????
            RedissonUtil.unRLock(lock);
        } else {
            str2 = "??????????????????????????????";
        }

        System.out.println(str2);
        return str2;
    }

}
