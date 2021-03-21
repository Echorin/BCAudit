package com.oschain.fastchaindb.cert.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.dto.ProofInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.cert.model.ProofInfo;

import java.util.List;
import java.util.Map;

/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
public interface ProofInfoMapper extends BaseMapper<ProofInfo> {

    List<ProofInfoFileDTO> listProofInfoFile(Page<ProofInfoFileDTO> page, Map<String, Object> condition);

    ProofInfoFileDTO getProofInfoFile(Map<String, Object> condition);

}
