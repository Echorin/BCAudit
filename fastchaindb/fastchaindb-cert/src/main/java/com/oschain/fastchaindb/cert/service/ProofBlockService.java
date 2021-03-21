package com.oschain.fastchaindb.cert.service;


import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.cert.model.CertBlock;
import com.oschain.fastchaindb.cert.model.ProofBlock;

/**
 * 
 * @author kevin
 * @date 2019-06-18 15:26:50
 */
public interface ProofBlockService extends IService<ProofBlock> {

    /**
     * 新增
     * @return
     */
    int save(ProofBlock proofBlock);

    /**
     * 修改
     * @return
     */
    int update(ProofBlock proofBlock);


}
