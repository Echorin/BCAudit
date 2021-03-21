package com.oschain.fastchaindb.common.utils;

import com.jcraft.jsch.*;
import com.oschain.fastchaindb.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class FtpsUtil {

    private static Session sshSession=null;
    private static ChannelExec sshExec=null;
    private static ChannelSftp sftp = null;
    private static final Logger log = LoggerFactory.getLogger(FtpsUtil.class);


//    public static void main(String[] args) {
//
//        //        File imagefile = new File("f:/aa.pdf");
////        String imagefileFileName = "aa_1";
////        //创建ftp客户端
////        FTPClient ftpClient = new FTPClient();
////        ftpClient.setControlEncoding("GBK");
////        String hostname = "192.168.124.12";
////        int port = 22;
////        String username = "fabric";
////        String password = "123456";
//        //listFileNames("192.168.124.12", 22, "fabric", "123456", "/webapp/myrds1/lib");
////        FtpsFileList.getConnect().upload("f:/cbs");
//
//        FtpsUtil fpsUtil=new FtpsUtil();
//
//        //此处必须是ROOT帐号登录，否则没有权限
//        //home/fabric
//        //fpsUtil.connect("192.168.124.12",22,"root","123456");
//        fpsUtil.connect("192.168.124.12",22,"fabric","123456");
//
//        //fpsUtil.executeCommand("cd /home/fabric;wget -o go.zip http://192.168.124.18:8844/go.zip;");
//
//        fpsUtil.executeCommand("cd /home/fabric;sh go.sh");
//
//
////        String filePath="F:\\Release\\fabric";
////        File file = new File(filePath);
////
////        File[] fileList = file.listFiles();
////        for (int i = 0; i < fileList.length; i++) {
////            if (fileList[i].isFile()) {
////                String fileName = fileList[i].getName();
////                System.out.println("文件：" + fileName);
////
////                fpsUtil.upload(fileList[i],fileName,"/home/fabric");
////
////            }
////            if (fileList[i].isDirectory()) {
////                String fileName = fileList[i].getName();
////                System.out.println("目录：" + fileName);
////            }
////        }
//
////        File file = new File("f:/cbs");
////        fpsUtil.upload(file,"1234","/usr/local/test2");
////
//        //fpsUtil.executeCommand("cd /usr/local/test;ls;");
//
//        //fpsUtil.executeCommand("/usr/local/test2","ls");
//
//        fpsUtil.disconnect();
//
//
//    }


    /**
     * sftp连接
     */
    public void connect(String host,int port,String username,  String password) {
        try {
            if (sftp != null) {
                log.info("sftp is not null");
            }
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            log.info("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();

            sshConfig.put("StrictHostKeyChecking", "no");


            sshSession.setConfig(sshConfig);
            sshSession.connect();
            log.info("Session connected.");
            log.info("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            log.info("Connected to " + host + ".");
        } catch (Exception e) {
            throw new BusinessException("连接:["+host+"]ftp服务器异常");
        }
    }

    /**
     * sftp断开连接
     */
    public void disconnect() {
        if (this.sftp != null) {
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e) {
                e.printStackTrace();
            }
            if (this.sftp.isConnected()) {
                this.sftp.disconnect();
            } else if (this.sftp.isClosed()) {
                //System.out.println("sftp is closed already");
            }
            sftp.quit();
        }
    }


    /**
     * 执行服务器端命令
     */
    public void executeCommand(String cdPath,String command)  {
        try {
            sftp.cd(cdPath);
            executeCommand(command);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }
    /**
     * 执行服务器端命令
     */
    public void executeCommand(String command)  {

        try {
            sshExec = (ChannelExec)sshSession.openChannel("exec");
        } catch (JSchException e) {
            e.printStackTrace();
        }

        if (sshExec == null) {
            System.out.println("openChannel is closed");
            return;
        }
        try {
            sshExec.setInputStream(null);
            sshExec.setErrStream(System.err);
            sshExec.setCommand(command);

            InputStream in = sshExec.getInputStream();
            sshExec.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * 上传文件 流 本地文件路径 remotePath 服务器路径
     */
    public void upload(File file, String remotName, String remotePath) {
        try {
            // File file = new File(localPath);
            if (file.isFile()) {
                // System.out.println("localFile : " + file.getAbsolutePath());
                String rpath = remotePath; // 服务器需要创建的路径
                try {
                    createDir(rpath, sftp);
                } catch (Exception e) {
                    throw new BusinessException("创建路径失败：" + rpath+","+e.getMessage());
                }
                // this.sftp.rm(file.getName());
                sftp.cd(remotePath);
                this.sftp.put(new FileInputStream(file), remotName);
            }
        } catch (FileNotFoundException e) {
            throw new BusinessException("上传文件没有找到,"+e.getMessage());
        } catch (SftpException e) {
            throw new BusinessException("上传ftp服务器错误,"+e.getMessage());
        }
    }

    /**
     * 创建一个文件目录
     */
    public void createDir(String createpath, ChannelSftp sftp) {
        try {
            if (isDirExist(createpath)) {
                this.sftp.cd(createpath);
                return;
            }

            sftp.cd("/usr/local");
            sftp.mkdir("test");


            String pathArry[] = createpath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                    //sftp.chmod(Integer.parseInt("777", 8), filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());

                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }
            this.sftp.cd(createpath);
        } catch (SftpException e) {
            //throw new BusinessException("创建路径错误：" + createpath);
            //System.out.println(e);
            e.printStackTrace();
        }
    }

    /**
     * 判断目录是否存在
     */
    public boolean isDirExist(String directory) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

}
