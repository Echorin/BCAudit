package com.oschain.fastchaindb.cert.service;

import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.common.PageResult;

import java.util.Map;

/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
public interface CertInfoService extends IService<CertInfo> {

    /**
     * 新增文件表
     * @return
     */
    int save(CertInfo certInfo);

    int delete(CertInfo certInfo);

    PageResult<CertInfoFileDTO> list(int pageNum, int pageSize, Map<String, Object> condition);

    CertInfoFileDTO getCertInfoFile(String certFileId);

    boolean checkCertInfoFile(CertFile certFile);

}
