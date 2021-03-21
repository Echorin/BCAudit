package com.oschain.fastchaindb.fabric.task;

import com.oschain.fastchaindb.blockchain.dto.FCDBTransaction;
import com.oschain.fastchaindb.common.utils.RedisUtil;
import com.oschain.fastchaindb.fabric.Handler.FabricHandler;
import com.oschain.fastchaindb.fabric.service.FabricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;


@Service
public class FabricTaskRun implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FabricTaskService.class);
    @Autowired
    FabricService fabricService;
    @Autowired
    private RedisUtil redisUtil;

//    private int taskNum;
//    public FabricTaskRun(int taskNum) {
//        this.taskNum = taskNum;
//    }


    @Override
    public void run() {
        while (true) {

            try {

//                List<String> list = EHCacheUtil.getList(FabricConfig.chaincodeName);

                //存入Redis队列
                //Object obj =  redisUtil.out(FabricConfig.chaincodeName+fabricService.serverTag);
                FCDBTransaction transaction = FabricHandler.concurrentLinkedQueue.poll();
                if(transaction==null){
                    //logger.debug("线程");
                    Thread.sleep(1000);
                    continue;
                }

                //Transaction transaction=(Transaction)obj;
                fabricService.sendTransaction(transaction);
                //logger.debug("线程");

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw, true);
                e.printStackTrace(pw);
                pw.flush();
                sw.flush();
                logger.error(sw.toString());
            }
        }
    }
}
