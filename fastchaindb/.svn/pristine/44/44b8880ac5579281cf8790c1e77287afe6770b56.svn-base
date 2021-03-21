package com.oschain.fastchaindb.cert.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertInfo;

import java.util.List;
import java.util.Map;

/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
public interface CertInfoMapper extends BaseMapper<CertInfo> {

    List<CertInfoFileDTO> listCertInfoFile(Page<CertInfoFileDTO> page, Map<String, Object> condition);

    CertInfoFileDTO getCertInfoFile(Map<String, Object> condition);

}
