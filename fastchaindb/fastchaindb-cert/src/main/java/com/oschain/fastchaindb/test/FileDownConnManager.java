package com.oschain.fastchaindb.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.concurrent.*;


public class FileDownConnManager {
    private static final Logger logger = LoggerFactory.getLogger(FileDownConnManager.class);

    private static final FileDownConnManager connManager = new FileDownConnManager();

    private static ExecutorService executorService = Executors.newFixedThreadPool(10); //10个线程跑

    public static FileDownConnManager getDefaultManager() {
        return connManager;
    }

    public static byte[] fileDown(final String netURL) throws ExecutionException, InterruptedException {
        Future<byte[]> future = executorService.submit(new Callable<byte[]>() {
            @Override
            public byte[] call() throws Exception {
                Date date = new Date();
                URL url;
                byte[] getData = new byte[0];
                InputStream is = null;
                try {
                    url = new URL(netURL);
                    URLConnection connection = url.openConnection();
                    is = connection.getInputStream();
                    getData = readInputStream(is);

                } catch (IOException e) {
                    logger.error("从URL获得字节流数组失败");
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        logger.error("从URL获得字节流数组流释放失败 ");
                    }
                }
                return getData;
            }
        });
        return future.get();
    }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

}
