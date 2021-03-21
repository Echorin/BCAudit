package com.oschain.fastchaindb.cert.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.oschain.fastchaindb.cert.model.CertAuditFile;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.system.model.Console;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
public interface CertAuditFileMapper extends BaseMapper<CertAuditFile> {

    Integer updateByFileIds(Map<String, Object> condition); // 通过

    List<CertAuditFile> selectByAuditIds(Map<String, Object> condition);  // 通过文件ID选择

    List<Console> reportSizeAdditionalRecord();

    Integer updateByIds(Map<String, Object> condition); // 通过ID更新

    Integer updateLastCheckTimeById(CertFile certFile); // 通过ID更新最新检查时间

    BigInteger sumReportSize();   // 审计文件大小

}
