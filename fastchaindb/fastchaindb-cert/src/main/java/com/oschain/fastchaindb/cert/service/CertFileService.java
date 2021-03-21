package com.oschain.fastchaindb.cert.service;

import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.common.PageResult;
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
public interface CertFileService extends IService<CertFile> {

    /**
     * 新增文件表
     * @return
     */

    int save(CertFile certFile);

    BigInteger sumFileSize();

    PageResult<CertFile> list(int pageNum, int pageSize, Map<String, Object> condition);

    List<Console> fileSizeAdditionalRecord();
}
