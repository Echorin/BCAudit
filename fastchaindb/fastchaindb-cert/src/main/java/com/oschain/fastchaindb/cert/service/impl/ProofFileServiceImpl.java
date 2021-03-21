package com.oschain.fastchaindb.cert.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.cert.dao.ProofFileMapper;
import com.oschain.fastchaindb.cert.model.ProofFile;
import com.oschain.fastchaindb.cert.service.ProofFileService;
import com.oschain.fastchaindb.common.GlobalConsts;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.cert.dao.CertFileMapper;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.system.model.Console;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Date;

import com.oschain.fastchaindb.cert.service.CertFileService;



@Service("proofFileService")
public class ProofFileServiceImpl extends ServiceImpl<ProofFileMapper, ProofFile> implements ProofFileService {
	@Autowired
	private ProofFileMapper proofFileMapper;

	// 计算文件存储总数
    @Override
    public BigInteger sumFileSize() {
        return proofFileMapper.sumFileSize();
    }


    /**
     * 新增文件表
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (ProofFile sysFile){
        sysFile.setCreateTime(new Date());
        sysFile.setDeleteFlag(GlobalConsts.YesOrNo.YES);
        System.out.println("4444444444444444");
        System.out.println(sysFile.getId());
        System.out.println("---------------");
        System.out.println(sysFile.getFileName());
        System.out.println("---------------");
        System.out.println(sysFile.getFilePath());
        System.out.println("---------------");
        System.out.println(sysFile.getFileType());
        System.out.println("---------------");
        System.out.println(sysFile.getFileHash());
        System.out.println("---------------");
        System.out.println(sysFile.getCreateTime());
        System.out.println("---------------");
        System.out.println(sysFile.getCreateUser());
        System.out.println("---------------");
        System.out.println(sysFile.getCertSignId());
        System.out.println("---------------");
        System.out.println(sysFile.getDeleteFlag());
        System.out.println("---------------");
        System.out.println(sysFile.getFileId());
        System.out.println("---------------");
        System.out.println(sysFile.getTransactionId());
        System.out.println("---------------");
        System.out.println(sysFile.getFileSize());
        System.out.println("---------------");
        System.out.println(sysFile.getLastCheckTime());
        System.out.println("---------------");
        sysFile.setCertId(13);
        System.out.println(sysFile.getCertId());
        System.out.println("---------------");
        return proofFileMapper.insert(sysFile);
	}

    @Override
    public PageResult<ProofFile> list(int pageNum, int pageSize, Map<String, Object> condition) {
//         page = new Page<>(pageNum, pageSize);
//        List<LoginRecord> records = loginRecordMapper.listFull(page, startDate, endDate, account);

        Wrapper<ProofFile> wrapper = new EntityWrapper<>();

//        if (StringUtil.isNotBlank(column)) {
//            wrapper.like(column, value);
//        }
//        if (!showDelete) {  // 不显示锁定的用户
//            wrapper.eq("state", 0);
//        }

        Page<ProofFile> proofFilePage = new Page<>(pageNum, pageSize);
        List<ProofFile> proofFileList = proofFileMapper.selectPage(proofFilePage, wrapper.orderBy("create_time", true));

        return new PageResult<>(proofFilePage.getTotal(), proofFileList);
    }

    public List<Console> fileSizeAdditionalRecord(){
        return proofFileMapper.fileSizeAdditionalRecord();
    }
}
