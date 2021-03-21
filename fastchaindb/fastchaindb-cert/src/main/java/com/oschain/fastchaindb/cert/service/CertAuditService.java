package com.oschain.fastchaindb.cert.service;

import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.cert.dto.CertAuditFileDTO;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertAuditFile;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.system.model.Console;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
public interface CertAuditService extends IService<CertAuditFile> {

    /**
     * 新增文件表
     * @return
     */
    int save(CertAuditFile certAuditFile);

    BigInteger sumReportSize();

    PageResult<CertAuditFile> list(int pageNum, int pageSize, Map<String, Object> condition);

    List<Console> reportSizeAdditionalRecord();

}

