package com.oschain.fastchaindb.fabric.service;


import com.oschain.fastchaindb.fabric.model.FabricMonitor;

/**
 * 
 * @author kevin
 * @date 2019-10-15 17:52:53
 */
public interface FabricMonitorService {


    /**
     * 获取
     * @return
     */
    FabricMonitor get(String id);

    /**
     * 新增
     * @return
     */
    Integer add(FabricMonitor fabricMonitor);

    /**
     * 修改
     * @return
     */
    Integer update(FabricMonitor fabricMonitor);

}
