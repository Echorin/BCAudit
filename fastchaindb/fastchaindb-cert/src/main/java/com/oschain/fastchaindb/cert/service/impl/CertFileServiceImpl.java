package com.oschain.fastchaindb.cert.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
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



@Service("certFileService")
public class CertFileServiceImpl extends ServiceImpl<CertFileMapper, CertFile> implements CertFileService {
	@Autowired
	private CertFileMapper certFileMapper;

	// 计算文件存储总数
    @Override
    public BigInteger sumFileSize() {
        return certFileMapper.sumFileSize();
    }


    /**
     * 新增文件表
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (CertFile sysFile){
        sysFile.setCreateTime(new Date());
        sysFile.setDeleteFlag(GlobalConsts.YesOrNo.YES);
        return certFileMapper.insert(sysFile);
	}

    @Override
    public PageResult<CertFile> list(int pageNum, int pageSize, Map<String, Object> condition) {
//         page = new Page<>(pageNum, pageSize);
//        List<LoginRecord> records = loginRecordMapper.listFull(page, startDate, endDate, account);

        Wrapper<CertFile> wrapper = new EntityWrapper<>();

//        if (StringUtil.isNotBlank(column)) {
//            wrapper.like(column, value);
//        }
//        if (!showDelete) {  // 不显示锁定的用户
//            wrapper.eq("state", 0);
//        }

        Page<CertFile> certFilePage = new Page<>(pageNum, pageSize);
        List<CertFile> certFileList = certFileMapper.selectPage(certFilePage, wrapper.orderBy("create_time", true));

        return new PageResult<>(certFilePage.getTotal(), certFileList);
    }

    public List<Console> fileSizeAdditionalRecord(){
        return certFileMapper.fileSizeAdditionalRecord();
    }
}
