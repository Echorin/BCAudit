package com.oschain.fastchaindb.cert.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.ProofFile;
import com.oschain.fastchaindb.system.model.Console;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
public interface ProofFileMapper extends BaseMapper<ProofFile> {

    Integer updateByFileIds(Map<String, Object> condition); // 通过文件ID更新

    List<ProofFile> selectByFileIds(Map<String, Object> condition);  // 通过文件ID选择

    List<Console> fileSizeAdditionalRecord();

    Integer updateByIds(Map<String, Object> condition); // 通过文件ID更新

    Integer updateLastCheckTimeById(ProofFile proofFile); // 通过ID更新最后确认时间

    BigInteger sumFileSize();   // 文件总大小

}
