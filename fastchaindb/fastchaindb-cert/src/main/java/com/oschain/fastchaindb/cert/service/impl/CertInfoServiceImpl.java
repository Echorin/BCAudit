package com.oschain.fastchaindb.cert.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.blockchain.dto.BlockChain;
import com.oschain.fastchaindb.blockchain.dto.Transaction;
import com.oschain.fastchaindb.blockchain.dto.TransactionQueryDTO;
import com.oschain.fastchaindb.blockchain.dto.TransactionSendDTO;
import com.oschain.fastchaindb.cert.dao.CertFileMapper;
import com.oschain.fastchaindb.cert.dao.CertInfoMapper;
import com.oschain.fastchaindb.cert.dto.CertFileBlockDTO;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.cert.service.CertInfoService;
import com.oschain.fastchaindb.client.FastChainDBClient;
import com.oschain.fastchaindb.client.dto.ResultDTO;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.common.exception.BusinessException;
import com.oschain.fastchaindb.common.utils.GsonUtil;
import com.oschain.fastchaindb.common.utils.SHA256Util;
import com.oschain.fastchaindb.common.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.util.*;


@Service("certInfoService")
public class CertInfoServiceImpl extends ServiceImpl<CertInfoMapper, CertInfo> implements CertInfoService {
	@Autowired
	private CertInfoMapper certInfoMapper;
    @Autowired
    private CertFileMapper certFileMapper;
    @Autowired
    private FastChainDBClient fastChainDBClient;

    // 定义在application.properties
    @Value("${file.path}")
    private String filePath;

    // 定义在application.properties
    @Value("${file.path.down}")
    private String filePathDown;

    // 定义在application.properties
    @Value("${file.server}")
    private String fileServer;

    /**
     * 新增文件表
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (CertInfo certInfo) {
        String signId = UUIDUtil.randomUUID32();
        certInfo.setCreateTime(new Date());
        certInfo.setSignId(signId);
        Integer insertRows = certInfoMapper.insert(certInfo);

        CertFile updateCertFile;
        ResultDTO<String> resultDto;
        List<CertFile> updateListCertFile = new ArrayList<>();


        //如果多个附件，需要循环上链
        List<String> fileIds = Arrays.asList(certInfo.getFileId().split(","));

        Map<String, Object> condition = new HashMap<>();
        condition.put("fileIds", fileIds);
        List<CertFile> listCertFile = certFileMapper.selectByFileIds(condition);
        for (CertFile certFile : listCertFile) {
            System.out.println("------------");
            CertFileBlockDTO certFileBlockDTO=new CertFileBlockDTO();
            certFileBlockDTO.setFileId(certFile.getFileId());
            certFileBlockDTO.setFileHash(certFile.getFileHash());
            System.out.println("============");
            System.out.println(certFileBlockDTO);
            TransactionSendDTO transactionSendDTO=new TransactionSendDTO();
            transactionSendDTO.setData(GsonUtil.gsonString(certFileBlockDTO));


            //区块上链(这地方后期优化性能，可以考虑批量提交)
            //resultDto = xChainClient.sendTransaction("{\"accessId\":1,\"accessKey\":2,\"data\":\"" + GsonUtil.gsonString(certFileBlockDTO) + "\"}");
//            System.out.println(transactionSendDTO.toString());
//            System.out.println(certFileBlockDTO);
            //System.out.println(GsonUtil.gsonString(certFileBlockDTO));
            resultDto = fastChainDBClient.sendTransaction(transactionSendDTO);
//            System.out.println(resultDto);
//            System.out.println(resultDto.getData());
            updateCertFile = new CertFile();
            updateCertFile.setTransactionId(resultDto.getData().toString());
            updateCertFile.setId(certFile.getId());
            updateListCertFile.add(updateCertFile);
        }

        //更新区块信息，判断是否返回区块信息
        condition =new HashMap<>();
        if (updateListCertFile != null && updateListCertFile.size() > 0) {
            condition.put("certSignId",certInfo.getSignId());
            condition.put("listCertFile",updateListCertFile);

            Integer updateRows = certFileMapper.updateByIds(condition);
        }
        else {

             throw new BusinessException("区块存入失败");
//            condition.put("certSignId",certInfo.getSignId());
//            condition.put("fileIds",fileIds);
//            //condition.put("transactionId",null);
//            Integer updateRows = certFileMapper.updateByFileIds(condition);
        }

        return insertRows.intValue();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete (CertInfo certInfo){
        certInfo=certInfoMapper.selectById(certInfo.getId());
        Integer deleteRows =certInfoMapper.deleteById(certInfo.getId());

        Wrapper<CertFile> wrapper = new EntityWrapper<>();
        wrapper.eq("cert_sign_id", certInfo.getSignId());
        certFileMapper.delete(wrapper);

        return deleteRows.intValue();
    }

    @Override
    public PageResult<CertInfoFileDTO> list(int pageNum, int pageSize, Map<String, Object> condition) {
//         page = new Page<>(pageNum, pageSize);
//        List<LoginRecord> records = loginRecordMapper.listFull(page, startDate, endDate, account);

//        Wrapper<CertInfo> wrapper = new EntityWrapper<>();
//        if (StringUtil.isNotBlank(column)) {
//            wrapper.like(column, value);
//        }
//        if (!showDelete) {  // 不显示锁定的用户
//            wrapper.eq("state", 0);
//        }

        Page<CertInfoFileDTO> certInfoFilePage = new Page<>(pageNum, pageSize);
        List<CertInfoFileDTO> certInfoFileList = certInfoMapper.listCertInfoFile(certInfoFilePage, condition);

        return new PageResult<>(certInfoFilePage.getTotal(), certInfoFileList);
    }

    @Override
    public CertInfoFileDTO getCertInfoFile(String certFileId) {
        Map<String, Object> condition =new HashMap<>();
        condition.put("certFileId",certFileId);
       return certInfoMapper.getCertInfoFile(condition);
    }

    @Override
    public boolean checkCertInfoFile(CertFile certFile) {

        TransactionQueryDTO transactionQueryDTO=new TransactionQueryDTO();
        transactionQueryDTO.setTransactionId(certFile.getTransactionId());

        // ----- 与存储的文件做校验
        String dirpath = System.getProperty("user.dir");
        System.out.println("dirpath:" +dirpath);

        String pathUrl=dirpath+"/public/"+certFile.getFilePath();
        System.out.println("pathUrl:" + pathUrl);
        byte[] bytes = new byte[0];
        try{
              File file = new File(pathUrl);
              bytes = Files.readAllBytes(file.toPath());
//            bytes = FileDownConnManager.fileDown(pathUrl);
        }catch (Exception ex){
            System.out.println("input local file is error");
        }
        String fileHash_local = SHA256Util.getSHA256Str(bytes);

        System.out.println("fileHash_local:" + fileHash_local);
        // ----------

        ResultDTO<BlockChain> resultDto = fastChainDBClient.getChainByTransactionId(transactionQueryDTO);
        if(resultDto!=null){
            List<Transaction> transactionList=resultDto.getData().getTransactionList();
            if(transactionList!=null && transactionList.size()>0){
                Transaction transaction=transactionList.get(0);
                //Mrsu debug
                String fileHash_block = transaction.getTransactionData();
                System.out.println("fileHash_block:" + fileHash_block);
//                String fileId = transaction.getTransactionId();
//                CertFileBlockDTO certFileBlockDTO= GsonUtil.gsonToBean(json,CertFileBlockDTO.class);
                if(fileHash_block.equals(fileHash_local)){
                    //更新最后验证时间
                    certFileMapper.updateLastCheckTimeById(certFile);
                    return true;
                }
            }
        }

        return false;
    }




}
