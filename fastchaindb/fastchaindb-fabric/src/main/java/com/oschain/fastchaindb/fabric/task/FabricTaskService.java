package com.oschain.fastchaindb.fabric.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

@DependsOn("transactionAdviceConfig")
public class FabricTaskService implements InitializingBean,Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FabricTaskService.class);

    @Autowired
    FabricTaskRun fabricTaskRun;

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this).start();
    }

    @Override
    public void run() {
        System.out.println("启动Fabric监控服务");
        for (int i=0;i<10;i++){
            new Thread(fabricTaskRun).start();
        }
    }
}
