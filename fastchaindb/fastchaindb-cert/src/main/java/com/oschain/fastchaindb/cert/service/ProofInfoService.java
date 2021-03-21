package com.oschain.fastchaindb.cert.service;

import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.dto.ProofInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.cert.model.ProofFile;
import com.oschain.fastchaindb.cert.model.ProofInfo;
import com.oschain.fastchaindb.common.PageResult;

import java.util.Map;

/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
public interface ProofInfoService extends IService<ProofInfo> {

    /**
     * 新增文件表
     * @return
     */
    int save(ProofInfo proofInfo);

    int delete(ProofInfo proofInfo);

    PageResult<ProofInfoFileDTO> list(int pageNum, int pageSize, Map<String, Object> condition);

    ProofInfoFileDTO getProofInfoFile(String proofFileId);

    boolean checkProofInfoFile(ProofFile proofFile);

}
