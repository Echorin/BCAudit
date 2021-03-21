package com.oschain.fastchaindb.fabric.service.impl;

import java.util.Map;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.oschain.fastchaindb.fabric.dao.FabricConfigMapper;
import com.oschain.fastchaindb.fabric.model.FabricConfig;
import com.oschain.fastchaindb.fabric.service.FabricConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;





@Service("fabricConfigService")
public class FabricConfigServiceImpl extends ServiceImpl<FabricConfigMapper, FabricConfig> implements FabricConfigService {

	@Autowired
	private FabricConfigMapper fabricConfigMapper;

    /**
     * 获取
     * @return
     */
    @Override
    public FabricConfig get(String id) {
        return fabricConfigMapper.selectById(id);
    }


    /**
     * 新增
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer add (FabricConfig fabricConfig){
        fabricConfig.preInsert();
        return fabricConfigMapper.insert(fabricConfig);
	}


    /**
     * 修改
     * @return
     */
    //增加事务
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer update(FabricConfig fabricConfig){
        fabricConfig.preUpdate();
        return fabricConfigMapper.updateById(fabricConfig);
	}

}
