package com.oschain.fastchaindb.common.utils;

import ch.ethz.ssh2.*;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.io.*;

public class ScpUtil {

    private static Log logger = LogFactory.getLog(ScpUtil.class);

    /**
     * 远程文件传输，如果local参数是文件，则本地传输到远程；如果是目录，则远程传输到本地
     * @param remoteIp   远程主机IP或hostname
     * @param port       远程主机端口号
     * @param user       远程主机用户名
     * @param password   远程主机对应密码
     * @param local      本地主机文件名（本地->远程）；本地主机目录名（远程->本地）
     * @param remote     远程主机目录名（本地->远程）；远程主机文件名（远程->本地）
     * @return           返回0：成功。1：失败
     */
    public static int scpFile(String remoteIp, int port, String user, String password, String local,
                              String remote) {
        Connection con = new Connection(remoteIp, port);
        try {
            con.connect();
            boolean isAuthed = con.authenticateWithPassword(user, password);

            if (!isAuthed) {
                logger.error("远程主机" + remoteIp + "用户名或密码验证失败！");
                return -1;//SystemGlobal.FAILED;
            }

            SCPClient scpClient = con.createSCPClient();
            File localFile = new File(local);

            if (localFile.isFile()) {
                if (!localFile.exists()) {
                    logger.error("本地文件" + local + "不存在，无法传输！");
                    return -2;//SystemGlobal.FAILED;
                } else {
                    try {
                        SFTPv3Client sftpClient = new SFTPv3Client(con);
                        sftpClient.mkdir(remote, 0777); //远程新建目录
                    } catch (SFTPException e1) {
                        logger.debug("目录" + remote + "已存在，无需再创建。");
                    }

                    try {
                        SCPOutputStream scpOutputStream = scpClient.put(local, localFile.length(),remote, "0777");
//                        String content = IOUtils.toString(new FileInputStream(local), StandardCharsets.UTF_8);
//                        scpOutputStream.write(content.getBytes());
//                        scpOutputStream.flush();
//                        scpOutputStream.close();

                    } catch (IOException e2) {
                        logger.error("路径" + remote + "不是一个文件夹。");
                        return -3;//SystemGlobal.FAILED;
                    }

                    String filename = local.substring(local.lastIndexOf('/') + 1);
                    Session session = con.openSession();
                    session.execCommand("ls -l " + remote + "/" + filename);
                    InputStream is = new StreamGobbler(session.getStdout());
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = br.readLine();
                    br.close();
                    is.close();
                    session.close();
                    logger.debug("上传到远程主机上的文件为：" + line);
//                    if (!TaskUtil.isStrEmpty(line)) {
//                        return SystemGlobal.SUCCESS;
//                    }
                }
            } else {
                if (!localFile.exists()) {
                    localFile.mkdirs();
                }

                scpClient.get(remote);

                String filename = remote.substring(remote.lastIndexOf('/') + 1);
                if (new File(local + "/" + filename).exists()) {
                    return 0;//SystemGlobal.SUCCESS;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        con.close();
        return -1;//SystemGlobal.FAILED;
    }

//    public static void main(String args[]) throws Exception {
//
//        ScpUtil.scpFile("192.168.124.12",22,"fabric", "123456","f:\\aa.pdf","/usr/local/test");
//
//        //RemoteShellExecutor executor = new RemoteShellExecutor("192.168.124.12", "fabric", "123456");
//        // 执行myTest.sh 参数为java Know dummy
//        //System.out.println(executor.exec2("python /data/bluemoon/kettle/runScript/ods/fact_org_employee.py"));
//
//        //System.out.println(executor.exec2("mkdir file1"));
//
//        //executor.transferFile("f:\\人工智能.pdf","/home/fabric/file1");
//
//        //SSHAgent sshAgent = new SSHAgent();
//        //sshAgent.initSession("192.168.243.21", "user", "password#");
//        //sshAgent.transferFile("C:\\data\\applogs\\bd-job\\jobhandler\\2020-03-07\\483577870267060231.log","/data/applogs/bd-job/jobhandler/2018-09-13");
//        //sshAgent.close();
//
//    }

}
