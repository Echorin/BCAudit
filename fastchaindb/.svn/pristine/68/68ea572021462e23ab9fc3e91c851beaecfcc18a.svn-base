package com.oschain.fastchaindb.fabric.service.impl;

import java.util.Map;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.fabric.dao.FabricMonitorPeerMapper;
import com.oschain.fastchaindb.fabric.model.FabricMonitorPeer;
import com.oschain.fastchaindb.fabric.service.FabricMonitorPeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service("fabricMonitorPeerService")
public class FabricMonitorPeerServiceImpl extends ServiceImpl<FabricMonitorPeerMapper, FabricMonitorPeer> implements FabricMonitorPeerService {

	@Autowired
	private FabricMonitorPeerMapper fabricMonitorPeerMapper;

    /**
     * 获取
     * @return
     */
    @Override
    public FabricMonitorPeer get(String id) {
        return fabricMonitorPeerMapper.selectById(id);
    }


    /**
     * 新增
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer add (FabricMonitorPeer fabricMonitorPeer){
        fabricMonitorPeer.preInsert();
        return fabricMonitorPeerMapper.insert(fabricMonitorPeer);
	}


    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer update(FabricMonitorPeer fabricMonitorPeer){
        fabricMonitorPeer.preUpdate();
        return fabricMonitorPeerMapper.updateById(fabricMonitorPeer);
	}

}
