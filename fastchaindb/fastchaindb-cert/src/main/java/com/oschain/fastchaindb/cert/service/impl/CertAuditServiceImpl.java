package com.oschain.fastchaindb.cert.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.cert.dao.CertAuditFileMapper;
import com.oschain.fastchaindb.cert.model.CertAuditFile;
import com.oschain.fastchaindb.cert.service.CertAuditService;
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



@Service("certAuditFileService")
public class CertAuditServiceImpl extends ServiceImpl<CertAuditFileMapper, CertAuditFile> implements CertAuditService {
	@Autowired
	private CertAuditFileMapper certAuditFileMapper;

	// 计算审计报告文件存储总数
    @Override
    public BigInteger sumReportSize() {
        return certAuditFileMapper.sumReportSize();
    }


    /**
     * 新增文件表
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (CertAuditFile sysFile){
        sysFile.setCreateTime(new Date());
        sysFile.setDeleteFlag(GlobalConsts.YesOrNo.YES);
        return certAuditFileMapper.insert(sysFile);
	}

    @Override
    public PageResult<CertAuditFile> list(int pageNum, int pageSize, Map<String, Object> condition) {
//         page = new Page<>(pageNum, pageSize);
//        List<LoginRecord> records = loginRecordMapper.listFull(page, startDate, endDate, account);

        Wrapper<CertAuditFile> wrapper = new EntityWrapper<>();

//        if (StringUtil.isNotBlank(column)) {
//            wrapper.like(column, value);
//        }
//        if (!showDelete) {  // 不显示锁定的用户
//            wrapper.eq("state", 0);
//        }

        Page<CertAuditFile> certFilePage = new Page<>(pageNum, pageSize);
        List<CertAuditFile> certFileList = certAuditFileMapper.selectPage(certFilePage, wrapper.orderBy("create_time", true));

        return new PageResult<>(certFilePage.getTotal(), certFileList);
    }

    public List<Console> reportSizeAdditionalRecord(){
        return certAuditFileMapper.reportSizeAdditionalRecord();
    }
}
