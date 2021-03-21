package com.oschain.fastchaindb.fabric.service.impl;

import java.util.Map;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.fabric.dao.FabricMonitorMapper;
import com.oschain.fastchaindb.fabric.model.FabricMonitor;
import com.oschain.fastchaindb.fabric.service.FabricMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service("fabricMonitorService")
public class FabricMonitorServiceImpl extends ServiceImpl<FabricMonitorMapper, FabricMonitor> implements FabricMonitorService {

	@Autowired
	private FabricMonitorMapper fabricMonitorMapper;

    /**
     * 获取
     * @return
     */
    @Override
    public FabricMonitor get(String id) {
        return fabricMonitorMapper.selectById(id);
    }


    /**
     * 新增
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer add (FabricMonitor fabricMonitor){
        fabricMonitor.preInsert();
        return fabricMonitorMapper.insert(fabricMonitor);
	}


    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer update(FabricMonitor fabricMonitor){
        fabricMonitor.preUpdate();
        return fabricMonitorMapper.updateById(fabricMonitor);
	}

}
