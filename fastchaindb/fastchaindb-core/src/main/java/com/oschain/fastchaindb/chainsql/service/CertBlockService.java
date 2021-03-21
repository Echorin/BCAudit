package com.oschain.fastchaindb.chainsql.service;


import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.blockchain.dto.FCDBTransaction;
import com.oschain.fastchaindb.chainsql.model.CertBlock;
import com.oschain.fastchaindb.common.PageResult;

import java.util.List;

/**
 * 
 * @author kevin
 * @date 2019-06-18 15:26:50
 */
public interface CertBlockService extends IService<CertBlock> {


    /**
     * 查询
     * @param certBlock
     * @return
     */
    PageResult<CertBlock> listCertBlock(int pageNum, int pageSize,CertBlock certBlock);

    /**
     * 新增
     * @return
     */
    int save(CertBlock certBlock);

    /**
     * 修改
     * @return
     */
    int update(CertBlock certBlock);


    int updateLastTime(CertBlock certBlock);

    int updateLastTime(List<FCDBTransaction> list);
}
