package com.oschain.fastchaindb.cert.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.oschain.fastchaindb.cert.model.CertBlock;
import com.oschain.fastchaindb.cert.model.ProofBlock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author kevin
 * @date 2019-06-18 15:32:58
 */
@Mapper
public interface ProofBlockMapper extends BaseMapper<ProofBlock> {
}
