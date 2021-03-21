package com.oschain.fastchaindb.cert.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.cert.dao.CertBlockMapper;
import com.oschain.fastchaindb.cert.dao.ProofBlockMapper;
import com.oschain.fastchaindb.cert.model.CertBlock;
import com.oschain.fastchaindb.cert.model.ProofBlock;
import com.oschain.fastchaindb.cert.service.CertBlockService;
import com.oschain.fastchaindb.cert.service.ProofBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.Date;





@Service("proofBlockService")
public class ProofBlockServiceImpl extends ServiceImpl<ProofBlockMapper, ProofBlock> implements ProofBlockService {
	@Autowired
	private ProofBlockMapper proofBlockMapper;


    /**
     * 新增
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (ProofBlock proofBlock){
        proofBlock.setCreateTime(new Date());
        return proofBlockMapper.insert(proofBlock);
	}


    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int update(ProofBlock proofBlock){

        return 0;//proofBlockMapper.update(proofBlock);
	}

}
