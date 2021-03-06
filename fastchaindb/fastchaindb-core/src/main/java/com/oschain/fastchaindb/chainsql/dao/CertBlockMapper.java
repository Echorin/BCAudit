package com.oschain.fastchaindb.chainsql.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.oschain.fastchaindb.blockchain.dto.FCDBTransaction;
import com.oschain.fastchaindb.chainsql.model.CertBlock;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * @author kevin
 * @date 2019-06-18 15:32:58
 */
public interface CertBlockMapper extends BaseMapper<CertBlock> {

    List<CertBlock> listCertBlock(Page<CertBlock> page,CertBlock certBlock);

    Integer updateByTranId(CertBlock certBlock);

    Integer updateLastTimeByTranId(CertBlock certBlock);

    Integer updateLastTimeByTranIds(List<FCDBTransaction> list);

}
