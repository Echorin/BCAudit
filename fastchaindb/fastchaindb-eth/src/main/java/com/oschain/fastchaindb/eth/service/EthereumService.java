package com.oschain.fastchaindb.eth.service;

import com.oschain.fastchaindb.blockchain.dto.FCDBBlock;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.BooleanResponse;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.geth.Geth;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Service
public class EthereumService {

    private static final String URL = "http://localhost:8545/";

    /**
     * 初始化web3j普通api调用
     *
     * @return web3j
     */
    public static Web3j initWeb3j() {
        return Web3j.build(getService());
    }

    /**
     * 初始化personal级别的操作对象
     * @return Geth
     */
    public static Geth initGeth(){
        return Geth.build(getService());
    }

    /**
     * 初始化admin级别操作的对象
     * @return Admin
     */
    public static Admin initAdmin(){
        return Admin.build(getService());
    }

    /**
     * 初始化admin级别操作的对象
     * @return Admin
     */
    public static Parity initParity(){
        return Parity.build(getService());
    }

    /**
     * 通过http连接到geth节点
     * @return
     */
    private static HttpService getService(){
        return new HttpService(URL);
    }

    /**
     * 输入密码创建地址
     *
     * @param password 密码（建议同一个平台的地址使用一个相同的，且复杂度较高的密码）
     * @return 地址hash
     * @throws IOException
     */
    public static String newAccount(String password) throws IOException {
        Admin admin = initAdmin();
        Request<?, NewAccountIdentifier> request = admin.personalNewAccount(password);
        NewAccountIdentifier result = request.send();
        return result.getAccountId();
    }

    /**
     * 获得当前区块高度
     *
     * @return 当前区块高度
     * @throws IOException
     */
    public static BigInteger getCurrentBlockNumber() throws IOException {
        Web3j web3j = initWeb3j();
        Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
        return request.send().getBlockNumber();
    }

    /**
     * 解锁账户，发送交易前需要对账户进行解锁
     *
     * @param address  地址
     * @param password 密码
     * @param duration 解锁有效时间，单位秒
     * @return
     * @throws IOException
     */
    public static Boolean unlockAccount(String address, String password, BigInteger duration) throws IOException {
        Admin admin = initAdmin();
        Request<?, PersonalUnlockAccount> request = admin.personalUnlockAccount(address, password, duration);
        PersonalUnlockAccount account = request.send();
        return account.accountUnlocked();

    }

    /**
     * 账户解锁，使用完成之后需要锁定
     *
     * @param address
     * @return
     * @throws IOException
     */
    public static Boolean lockAccount(String address) throws IOException {
        Geth geth = initGeth();
        Request<?, BooleanResponse> request = geth.personalLockAccount(address);
        BooleanResponse response = request.send();
        return response.success();
    }

    /**
     * 根据hash值获取交易
     *
     * @param hash
     * @return
     * @throws IOException
     */
    public static FCDBBlock getTransactionByHash(String hash) throws IOException {
        System.out.println("getTransactionByHash进入成功");
        Web3j web3j = initWeb3j();
        EthTransaction ethTransaction = web3j.ethGetTransactionByHash(hash).send();
        BigInteger bigInteger = ethTransaction.getTransaction().get().getBlockNumber();
        Integer integer = Integer.valueOf(bigInteger.toString());
        FCDBBlock xChainBlock = new FCDBBlock();
        xChainBlock.setBlockHash(ethTransaction.getTransaction().get().getBlockHash());
        xChainBlock.setBlockHeight(integer.longValue());

        EthBlock ethBlock = getBlockEthBlock(integer);
        xChainBlock.setBlockTime(new Date(ethBlock.getBlock().getTimestamp().longValue()));
        xChainBlock.setPrevBlockHash(ethBlock.getBlock().getParentHash());
        xChainBlock.setTransactionCount(ethBlock.getBlock().getTransactions().size());

        List<EthBlock.TransactionResult> listTransactionResult = ethBlock.getBlock().getTransactions();
//        List<com.hiat.xchain.chain.dto.Transaction> listTransaction = null;
//        for(int i = 0; i < listTransactionResult.size(); i++){
//            com.hiat.xchain.chain.dto.Transaction transaction = new com.hiat.xchain.chain.dto.Transaction();
//            transaction.setTransactionData(listTransactionResult.get(i).toString());
//            transaction.setTransactionId(null);
//            transaction.setTransactionTime(null);
//            transaction.setSendNum(1);
//            listTransaction.add(transaction);
//        }
        xChainBlock.setTransactionList(null);
        return xChainBlock;
    }

    /**
     * 获得ethblock
     *
     * @param blockNumber 根据区块编号
     * @return
     * @throws IOException
     */
    public static EthBlock getBlockEthBlock(Integer blockNumber) throws IOException {
        System.out.println("getBlockEthBlock进入成功");
        Web3j web3j = initWeb3j();

        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        Request<?, EthBlock> request = web3j.ethGetBlockByNumber(defaultBlockParameter, true);
        EthBlock ethBlock = request.send();

        return ethBlock;
    }

    public static FCDBBlock getXChainByBlockHeight(Integer blockNumber) throws IOException {
        System.out.println("getXChainByBlockHeight进入成功");
        Web3j web3j = initWeb3j();

        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNumber);
        Request<?, EthBlock> request = web3j.ethGetBlockByNumber(defaultBlockParameter, true);
        EthBlock ethBlock = request.send();
        FCDBBlock xChainBlock = new FCDBBlock();
        xChainBlock.setBlockHeight(blockNumber.longValue());
        xChainBlock.setBlockHash(ethBlock.getBlock().getHash());
        xChainBlock.setTransactionCount(ethBlock.getBlock().getTransactions().size());
        xChainBlock.setTransactionList(null);
        xChainBlock.setPrevBlockHash(ethBlock.getBlock().getParentHash());
        xChainBlock.setBlockTime(new Date(ethBlock.getBlock().getTimestamp().longValue()));
        return xChainBlock;
    }

    /**
     * 获得ethblock
     *
     * @param blockNumber 根据区块Hash
     * @return
     * @throws IOException
     */
    public static FCDBBlock getBlockEthBlockByblockHash(String blockHash) throws IOException {
        System.out.println("getBlockEthBlockByblockHash进入成功");
        Web3j web3j = initWeb3j();
        Request<?, EthBlock> request = web3j.ethGetBlockByHash(blockHash, true);
        EthBlock ethBlock = request.send();


        FCDBBlock xChainBlock = new FCDBBlock();
        xChainBlock.setBlockHeight(ethBlock.getBlock().getNumber().longValue());
        xChainBlock.setBlockHash(ethBlock.getBlock().getHash());
        xChainBlock.setTransactionList(null);
        xChainBlock.setTransactionCount(ethBlock.getBlock().getTransactions().size());
        xChainBlock.setPrevBlockHash(ethBlock.getBlock().getParentHash());
        xChainBlock.setBlockTime(new Date(ethBlock.getBlock().getTimestamp().longValue()));
        return xChainBlock;
    }

    /**
     * 封装交易
     *
     * @param transaction
     * @param password
     * @return
     * @throws IOException
     */
    public static Transaction packageTransaction(String accountId,String passsword,String toAccountId, BigDecimal amount,String data) throws IOException {

        Transaction transaction = new Transaction(accountId,null,null,null,toAccountId,amount.toBigInteger(),data);

        return transaction;

    }

    /**
     * 发送交易并获得交易hash值
     *
     * @param transaction
     * @param password
     * @return
     * @throws IOException
     */
    public static String sendTransaction(String hash) throws IOException {
        System.out.println("sendTransaction进入成功");
        Web3j web3j = initWeb3j();
        EthAccounts ethAccounts = web3j.ethAccounts().send();
        Transaction transaction = new Transaction(ethAccounts.getAccounts().get(0),
                null,
                null,
                null,
                ethAccounts.getAccounts().get(0),
                BigInteger.valueOf(5),
                hash);
        String password = "123456";
        Parity parity = initParity();
        EthSendTransaction ethSendTransaction =parity.personalSendTransaction(transaction,password).send();
        //String tradeHash = ethSendTransaction.getTransactionHash();
        return ethSendTransaction.getTransactionHash();

    }

    /**
     * 指定地址发送交易所需nonce获取
     *
     * @param address 待发送交易地址
     * @return
     * @throws IOException
     */
    public static BigInteger getNonce(String address) throws IOException {
        System.out.println("getNonce进入成功");
        Web3j web3j = initWeb3j();
        Request<?, EthGetTransactionCount> request = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST);
        return request.send().getTransactionCount();
    }
}
