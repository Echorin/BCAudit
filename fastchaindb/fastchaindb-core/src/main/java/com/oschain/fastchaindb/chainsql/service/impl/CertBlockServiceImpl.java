package com.oschain.fastchaindb.chainsql.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.blockchain.dto.FCDBTransaction;
import com.oschain.fastchaindb.chainsql.dao.CertBlockMapper;
import com.oschain.fastchaindb.chainsql.model.CertBlock;
import com.oschain.fastchaindb.chainsql.service.CertBlockService;
import com.oschain.fastchaindb.common.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service("certBlockService")
public class CertBlockServiceImpl extends ServiceImpl<CertBlockMapper, CertBlock> implements CertBlockService {
	@Autowired
	private CertBlockMapper certBlockMapper;


    @Override
    public PageResult<CertBlock> listCertBlock(int pageNum,int pageSize,CertBlock certBlock) {
        Page<CertBlock> page = new Page<>(pageNum, pageSize);
        List<CertBlock> records = certBlockMapper.listCertBlock(page, certBlock);
        return new PageResult<>(page.getTotal(), records);
    }

    /**
     * 新增
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int save (CertBlock certBlock){
        //certBlock.setCreateTime(new Date());
        certBlock.setLastCheckTime(new Date());
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
        certBlock.setLastCheckTime(new Date());
        return certBlockMapper.updateByTranId(certBlock);
	}

    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int updateLastTime(CertBlock certBlock){
        return certBlockMapper.updateLastTimeByTranId(certBlock);
    }

    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int updateLastTime(List<FCDBTransaction> list){
        return certBlockMapper.updateLastTimeByTranIds(list);
    }

}
