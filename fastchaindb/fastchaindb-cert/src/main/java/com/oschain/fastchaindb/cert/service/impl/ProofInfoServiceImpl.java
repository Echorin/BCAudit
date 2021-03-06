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
import com.oschain.fastchaindb.cert.dao.ProofFileMapper;
import com.oschain.fastchaindb.cert.dao.ProofInfoMapper;
import com.oschain.fastchaindb.cert.dto.CertFileBlockDTO;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.dto.ProofFileBlockDTO;
import com.oschain.fastchaindb.cert.dto.ProofInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.cert.model.ProofFile;
import com.oschain.fastchaindb.cert.model.ProofInfo;
import com.oschain.fastchaindb.cert.service.CertInfoService;
import com.oschain.fastchaindb.cert.service.ProofInfoService;
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


@Service("proofInfoService")
public class ProofInfoServiceImpl extends ServiceImpl<ProofInfoMapper, ProofInfo> implements ProofInfoService {
	@Autowired
	private ProofInfoMapper proofInfoMapper;
    @Autowired
    private ProofFileMapper proofFileMapper;
    @Autowired
    private FastChainDBClient fastChainDBClient;

    // ?????????application.properties
    @Value("${file.path}")
    private String filePath;

    // ?????????application.properties
    @Value("${file.path.down}")
    private String filePathDown;

    // ?????????application.properties
    @Value("${file.server}")
    private String fileServer;

    /**
     * ???????????????
     * @return
     */
    //????????????
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (ProofInfo proofInfo) {
        String signId = UUIDUtil.randomUUID32();    // ????????????signID
        proofInfo.setCreateTime(new Date());        // ???????????????????????????
        proofInfo.setSignId(signId);                // ??????signID
        Integer insertRows = proofInfoMapper.insert(proofInfo); // ??????proofInfo
        System.out.println("1111111111111");
        ProofFile updateProofFile;
        ResultDTO<String> resultDto;
        List<ProofFile> updateListProofFile = new ArrayList<>();
        System.out.println("2222222222222");
        //???????????????????????????????????????
        List<String> fileIds = Arrays.asList(proofInfo.getFileId().split(","));

        Map<String, Object> condition = new HashMap<>();
        condition.put("fileIds", fileIds);
        List<ProofFile> listProofFile = proofFileMapper.selectByFileIds(condition);
        System.out.println("3333333333333");
        for (ProofFile proofFile : listProofFile) {
            System.out.println("------------");
            ProofFileBlockDTO proofFileBlockDTO=new ProofFileBlockDTO();
            proofFileBlockDTO.setFileId(proofFile.getFileId());
            proofFileBlockDTO.setFileHash(proofFile.getFileHash());
            System.out.println("============");
            System.out.println(proofFileBlockDTO);
            TransactionSendDTO transactionSendDTO=new TransactionSendDTO();
            transactionSendDTO.setData(GsonUtil.gsonString(proofFileBlockDTO));

            resultDto = fastChainDBClient.sendTransaction(transactionSendDTO);

            updateProofFile = new ProofFile();
            updateProofFile.setTransactionId(resultDto.getData().toString());
            updateProofFile.setId(proofFile.getId());
            updateListProofFile.add(updateProofFile);
        }

        //???????????????????????????????????????????????????
        condition =new HashMap<>();
        if (updateListProofFile != null && updateListProofFile.size() > 0) {
            condition.put("proofSignId",proofInfo.getSignId());
            condition.put("listProofFile",updateListProofFile);

            Integer updateRows = proofFileMapper.updateByIds(condition);
        }
        else {

             throw new BusinessException("??????????????????");
//            condition.put("certSignId",certInfo.getSignId());
//            condition.put("fileIds",fileIds);
//            //condition.put("transactionId",null);
//            Integer updateRows = certFileMapper.updateByFileIds(condition);
        }

        return insertRows.intValue();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delete (ProofInfo proofInfo){
        proofInfo=proofInfoMapper.selectById(proofInfo.getId());
        Integer deleteRows =proofInfoMapper.deleteById(proofInfo.getId());

        Wrapper<ProofFile> wrapper = new EntityWrapper<>();
        wrapper.eq("proof_sign_id", proofInfo.getSignId());
        proofFileMapper.delete(wrapper);

        return deleteRows.intValue();
    }

    @Override
    public PageResult<ProofInfoFileDTO> list(int pageNum, int pageSize, Map<String, Object> condition) {
//         page = new Page<>(pageNum, pageSize);
//        List<LoginRecord> records = loginRecordMapper.listFull(page, startDate, endDate, account);

//        Wrapper<CertInfo> wrapper = new EntityWrapper<>();
//        if (StringUtil.isNotBlank(column)) {
//            wrapper.like(column, value);
//        }
//        if (!showDelete) {  // ????????????????????????
//            wrapper.eq("state", 0);
//        }

        Page<ProofInfoFileDTO> proofInfoFilePage = new Page<>(pageNum, pageSize);
        List<ProofInfoFileDTO> proofInfoFileList = proofInfoMapper.listProofInfoFile(proofInfoFilePage, condition);

        return new PageResult<>(proofInfoFilePage.getTotal(), proofInfoFileList);
    }

    @Override
    public ProofInfoFileDTO getProofInfoFile(String proofFileId) {
        Map<String, Object> condition =new HashMap<>();
        condition.put("proofFileId",proofFileId);
       return proofInfoMapper.getProofInfoFile(condition);
    }

    @Override
    public boolean checkProofInfoFile(ProofFile proofFile) {

        TransactionQueryDTO transactionQueryDTO=new TransactionQueryDTO();
        transactionQueryDTO.setTransactionId(proofFile.getTransactionId());

        // ----- ???????????????????????????
        String dirpath = System.getProperty("user.dir");
        System.out.println("dirpath:" +dirpath);

        String pathUrl=dirpath+"/public/"+proofFile.getFilePath();
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
                    //????????????????????????
                    proofFileMapper.updateLastCheckTimeById(proofFile);
                    return true;
                }
            }
        }

        return false;
    }




}
