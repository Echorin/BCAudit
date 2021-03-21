package com.oschain.fastchaindb.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelSftp.LsEntry;


public class FtpsFileList {

    private static final Logger LOG = LoggerFactory.getLogger(FtpsFileList.class);

//    public static void main(String[] args) {
//        //        File imagefile = new File("f:/aa.pdf");
////        String imagefileFileName = "aa_1";
////        //创建ftp客户端
////        FTPClient ftpClient = new FTPClient();
////        ftpClient.setControlEncoding("GBK");
////        String hostname = "192.168.124.12";
////        int port = 22;
////        String username = "fabric";
////        String password = "123456";
//
//        //listFileNames("192.168.124.12", 22, "fabric", "123456", "/webapp/myrds1/lib");
//
//        FtpsFileList.getConnect().upload("f:/cbs");
//    }

    private static ChannelSftp sftp = null;

    //账号
    private static String user = "fabric";
    //主机ip
    private static String host =  "192.168.124.12";
    //密码
    private static String password = "123456";
    //端口
    private static int port = 22;
    //上传地址
    private static String directory = "/usr/local/test2";
    //下载目录
    private static String saveFile = "D:\\";

    public static FtpsFileList getConnect(){
        FtpsFileList ftp = new FtpsFileList();
        try {
            JSch jsch = new JSch();

            //获取sshSession  账号-ip-端口
            Session sshSession =jsch.getSession(user, host,port);
            //添加密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            //严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            //开启sshSession链接
            sshSession.connect();
            //获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            //开启
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftp;
    }

    /**
     *
     * @param uploadFile 上传文件的路径
     * @return 服务器上文件名
     */
    public String upload(String uploadFile) {
        File file = null;
        String fileName = null;
        try {
            sftp.cd(directory);
            file = new File(uploadFile);
            //获取随机文件名
            //fileName  = UUID.randomUUID().toString() + file.getName().substring(file.getName().length()-5);

            fileName  = UUID.randomUUID().toString();

            //文件名是 随机数加文件名的后5位
            sftp.put(new FileInputStream(file), fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file == null ? null : fileName;
    }

    /**
     * 下载文件
     *
     * @param directory
     *            下载目录
     * @param downloadFile
     *            下载的文件名
     * @param saveFile
     *            存在本地的路径
     * @param sftp
     */
    public void download(String downloadFileName) {
        try {
            sftp.cd(directory);

            File file = new File(saveFile);

            sftp.get(downloadFileName, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     *
     * @param deleteFile
     *            要删除的文件名字
     * @param sftp
     */
    public void delete(String deleteFile) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 列出目录下的文件
     *
     * @param directory
     *            要列出的目录
     * @param sftp
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory)
            throws SftpException {
        return sftp.ls(directory);
    }



}
