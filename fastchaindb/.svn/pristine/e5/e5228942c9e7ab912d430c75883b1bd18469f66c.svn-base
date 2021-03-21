package com.oschain.fastchaindb.fabric.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.fabric.dao.FabricMonitorOrderMapper;
import com.oschain.fastchaindb.fabric.model.FabricMonitorOrder;
import com.oschain.fastchaindb.fabric.service.FabricMonitorOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service("fabricMontitorOrderService")
public class FabricMonitorOrderServiceImpl extends ServiceImpl<FabricMonitorOrderMapper, FabricMonitorOrder> implements FabricMonitorOrderService {

	@Autowired
	private FabricMonitorOrderMapper fabricMontitorOrderMapper;

    /**
     * 获取
     * @return
     */
    @Override
    public FabricMonitorOrder get(String id) {
        return fabricMontitorOrderMapper.selectById(id);
    }


    /**
     * 新增
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer add (FabricMonitorOrder fabricMontitorOrder){
        fabricMontitorOrder.preInsert();
        return fabricMontitorOrderMapper.insert(fabricMontitorOrder);
	}


    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer update(FabricMonitorOrder fabricMontitorOrder){
        fabricMontitorOrder.preUpdate();
        return fabricMontitorOrderMapper.updateById(fabricMontitorOrder);
	}

}
