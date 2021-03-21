package com.oschain.fastchaindb.cert.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.cert.dao.CertBlockMapper;
import com.oschain.fastchaindb.cert.model.CertBlock;
import com.oschain.fastchaindb.cert.service.CertBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.Date;





@Service("certBlockService")
public class CertBlockServiceImpl extends ServiceImpl<CertBlockMapper, CertBlock> implements CertBlockService {
	@Autowired
	private CertBlockMapper certBlockMapper;


    /**
     * 新增
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (CertBlock certBlock){
        certBlock.setCreateTime(new Date());
        return certBlockMapper.insert(certBlock);
	}


    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(CertBlock certBlock){

        return 0;//certBlockMapper.update(certBlock);
	}

}
