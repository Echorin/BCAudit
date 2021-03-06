package com.oschain.fastchaindb.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oschain.fastchaindb.blockchain.dto.Transaction;
import com.oschain.fastchaindb.blockchain.dto.TransactionQueryDTO;
import com.oschain.fastchaindb.blockchain.dto.TransactionSendDTO;
import com.oschain.fastchaindb.blockchain.dto.BlockChain;
import com.oschain.fastchaindb.client.common.utils.*;
import com.oschain.fastchaindb.client.dto.PairKey;
import com.oschain.fastchaindb.client.dto.ResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FastChainDBClient implements IFastChainDB {
    private static final Logger logger = LoggerFactory.getLogger(FastChainDBClient.class);

    public FastChainDBClient(){}
    private String chainPath;
    private String accessId;
    private String accessKey;
    private String chainType="fabric";
    private String version="v1";

    public FastChainDBClient(String xchainPath, String accessId, String accessKey){
        this.chainPath=xchainPath;
        this.accessId=accessId;
        this.accessKey=accessKey;
    }
    public FastChainDBClient(String xchainPath, String accessId, String accessKey, String chainType){
        this.chainPath=xchainPath;
        this.accessId=accessId;
        this.accessKey=accessKey;
        this.chainType=chainType;
    }

    public PairKey createPairKey(String args) {

        try {
           String str = HttpUtils.doGet(getSendPath("/createPairKey"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public ResultDTO<String> sendTransaction(TransactionSendDTO transactionSendDTO) {

        String response = null;
        try {
            transactionSendDTO.setAccessId(accessId);
            transactionSendDTO.setAccessKey(accessKey);
            response = OkHttpUtil.doPost(getSendPath("/sendTransaction"), transactionSendDTO.toString()).body().string();
//            System.out.println(response);
            logger.debug(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResultDTO<String> dto = GsonUtil.gsonToBean(response,ResultDTO.class);

        //ResultDTO<String> resultDTO=(ResultDTO<String>)
        return dto;
    }

    public ResultDTO getBlockHeight() {
        String response = null;
        try {
            response = OkHttpUtil.doGet(getSendPath("/getBlockHeight")).body().string();
            logger.debug(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResultDTO<Long> dto = GsonUtil.gsonToBean(response,ResultDTO.class);

        return dto;
    }

    public ResultDTO<List<BlockChain>> getChainByTransactionData(TransactionQueryDTO transactionQueryDTO) {

        String response = null;
        try {
            transactionQueryDTO.setAccessId(accessId);
            transactionQueryDTO.setAccessKey(accessKey);
            response = OkHttpUtil.doPost(getSendPath("/getChainByTransactionData"), transactionQueryDTO.toString()).body().string();
            logger.debug(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getResultListDTO(response);
    }

    public ResultDTO<BlockChain> getChainByTransactionId(TransactionQueryDTO transactionQueryDTO) {

        String response = null;
        try {
            transactionQueryDTO.setAccessId(accessId);
            transactionQueryDTO.setAccessKey(accessKey);
            response = OkHttpUtil.doPost(getSendPath("/getChainByTransactionId"), transactionQueryDTO.toString()).body().string();
            logger.debug(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getResultDTO(response);
    }

    public void sendTransaction(String args,final HttpCallback callback) {
        OkHttpUtil.doPost(getSendPath("/sendTransaction"),args,callback);
    }



    private String getSendPath(String apiName){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(chainPath).append("/api/").append(version).append(apiName).append("?"+chainType);
        return stringBuilder.toString();
    }



    /**??????**/
    public String getChainPath() {
        return chainPath;
    }
    public void setChainPath(String chainPath) {
        this.chainPath = chainPath;
    }
    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    private ResultDTO<BlockChain> getResultDTO(String json){

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

        System.out.println("result:"+json);
        int code=Integer.parseInt(jsonObject.get("code").toString());
        String msg=jsonObject.get("msg").toString();
        String data = jsonObject.get("data").toString();
        BlockChain xChainBlock = GsonUtil.gsonToBean(data,BlockChain.class);

        ResultDTO<BlockChain> resultDTO=new ResultDTO<BlockChain>();
        resultDTO.setCode(code);
        resultDTO.setMsg(msg);
        resultDTO.setData(xChainBlock);

        return resultDTO;
    }

    private ResultDTO<List<BlockChain>> getResultListDTO(String json){

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

        int code=Integer.parseInt(jsonObject.get("code").toString());
        String msg=jsonObject.get("msg").toString();
        String data = jsonObject.get("data").toString();



        BlockChain blockChainMod;
        List<BlockChain> listMod = new ArrayList<BlockChain>();
        JsonArray jsonArray = jsonParser.parse(data).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray) {
            blockChainMod = GsonUtil.gsonToBean(jsonElement.toString(),BlockChain.class);
            listMod.add(blockChainMod);
        }


        //?????????????????????
//        List<BlockChain> blockChainList = GsonUtil.gsonToList(data,BlockChain.class);
        //????????????
//        for(BlockChain blockChain :listMod ) {
//            List<Transaction> list = blockChain.getTransactionList();
//            for(Transaction tran : list){
//               String tranId =  tran.getTransactionId();
//            }
//        }

        ResultDTO<List<BlockChain>> resultDTO=new ResultDTO<List<BlockChain>>();
        resultDTO.setCode(code);
        resultDTO.setMsg(msg);
        resultDTO.setData(listMod);

        return resultDTO;
    }
}
