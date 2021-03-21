package com.oschain.fastchaindb.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/down")
public class DownController {

    @RequestMapping("testUrl")
    public void testUrl(HttpServletResponse response){

        try {
            String str = "http://localhost:8089/uploadfile/fabric-sdk-java-hello-word.pdf";

            byte[] bytes = FileDownConnManager.fileDown(str);
            //以下载方式打开
            response.setHeader("Content-Disposition", "attachment;filename=java.pdf");

            //写出
            ServletOutputStream out = response.getOutputStream();
            out.write(bytes);

            //释放资源
            out.flush();
            out.close();


//
//
//
//            URL url = new URL(str);
//            //得到connection对象。
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//            //设置请求方式
//            connection.setRequestMethod("GET");
//            //连接
//            connection.connect();
//
//            byte[] bytes=new byte[0];
//            InputStream inputStream=null;
//
//            //得到响应码
//            int responseCode = connection.getResponseCode();
//            if(responseCode == HttpURLConnection.HTTP_OK){
//                //得到响应流
//                inputStream = connection.getInputStream();
//                bytes = input2byte(inputStream);
//
////                //文件保存位置
////                File saveDir = new File("d:/");
////                if(!saveDir.exists()){
////                    saveDir.mkdir();
////                }
////
////                File file = new File(saveDir+File.separator+"725.pdf");
////                FileOutputStream fos = new FileOutputStream(file);
////                fos.write(bytes);
////                if(fos!=null){
////                    fos.close();
////                }
//
//                //该干的都干完了,记得把连接断了
//                connection.disconnect();
//
//                //以下载方式打开
//                response.setHeader("Content-Disposition", "attachment;filename=java.pdf");
//
//                //写出
//                ServletOutputStream out = response.getOutputStream();
//                out.write(bytes);
//
//                if(inputStream!=null){
//                    inputStream.close();
//                }
//
//                //释放资源
//                out.flush();
//                out.close();
//            }
//            else
//            {
//                throw new BusinessException("文件不存在");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


}
