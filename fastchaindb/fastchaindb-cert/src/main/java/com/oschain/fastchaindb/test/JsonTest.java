package com.oschain.fastchaindb.test;

public class JsonTest {

//    public static void main(String[] args) {
//String json= "{\n" +
//        "    \"code\": 0,\n" +
//        "    \"msg\": \"ok\",\n" +
//        "    \"data\": {\n" +
//        "        \"blockHeight\": 12,\n" +
//        "        \"blockHash\": \"83792f14c437941e8370eecac4c3ae0751ae0470be6dcb1cffc4ae44249bf4de\",\n" +
//        "        \"prevBlockHash\": \"c2cd6c9518b32c1c0f9ed96b889ff17c767376f4d02886965505b34a158d248b\",\n" +
//        "        \"blockTime\": null,\n" +
//        "        \"transactionCount\": 1,\n" +
//        "        \"transactionList\": [\n" +
//        "            {\n" +
//        "                \"transactionId\": \"a4537871df8457ff0cd4bc8e7d458e57b9782b3086513cc1084d0519cbc97b40\",\n" +
//        "                \"transactionData\": \"{\\\"fileHash\\\":\\\"98cd5bf1548c4ab97b467e74f6c73472217c64a9d31b03fb49ed0e7f813ec1c1\\\"}\",\n" +
//        "                \"transactionTime\": \"2019/06/20 17:17:58\"\n" +
//        "            }\n" +
//        "        ]\n" +
//        "    }\n" +
//        "}";
//
//
//        JsonParser jsonParser = new JsonParser();
//        JsonObject  jsonObject = jsonParser.parse(json).getAsJsonObject();
//        String code = jsonObject.get("code").toString();
//        String data = jsonObject.get("data").toString();
//
//
//        BlockChain xChainBlock = GsonUtil.gsonToBean(data,BlockChain.class);
//        List<Transaction> transactionList=xChainBlock.getTransactionList();
//
//
//       // XChainBlock lists = gson.fromJson(json,new TypeToken<List<Transaction>>(){}.getType());
//
//
//       // ResultDTO<XChainBlock> resultDTO=GsonUtil.gsonToBean(json,ResultDTO.class);
////        if(resultDTO!=null){
////            List<Transaction> transactionList=resultDTO.getData().getTransactionList();
////            if(transactionList!=null && transactionList.size()>0){
////                Transaction transaction=transactionList.get(0);
////                transaction.getTransactionData();
////            }
////
////        }
//    }
}
