package com.oschain.fastchaindb.cert.controller;

import com.oschain.fastchaindb.blockchain.dto.BlockChain;
import com.oschain.fastchaindb.blockchain.dto.TransactionQueryDTO;
import com.oschain.fastchaindb.cert.dto.CertInfoFileDTO;
import com.oschain.fastchaindb.cert.model.CertAuditFile;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.cert.service.CertAuditService;
import com.oschain.fastchaindb.cert.service.CertFileService;
import com.oschain.fastchaindb.cert.service.CertInfoService;
import com.oschain.fastchaindb.client.FastChainDBClient;
import com.oschain.fastchaindb.client.dto.ResultDTO;
import com.oschain.fastchaindb.common.BaseController;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.common.exception.BusinessException;
import com.oschain.fastchaindb.common.utils.*;
import com.oschain.fastchaindb.system.dao.HomeMapper;
import com.oschain.fastchaindb.system.dao.UserMapper;
import com.oschain.fastchaindb.system.model.DownloadRecord;
import com.oschain.fastchaindb.system.model.Role;
import com.oschain.fastchaindb.system.model.User;
import com.oschain.fastchaindb.system.service.DownloadRecordService;
import com.oschain.fastchaindb.system.service.RoleService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 文件表
 *
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
@Controller
@RequestMapping("/cert/info")
public class CertInfoController extends BaseController {
    @Autowired
    private CertFileService certFileService;
    @Autowired
    private CertInfoService certInfoService;
    @Autowired
    private CertAuditService certAuditService;
    @Autowired
    private FastChainDBClient fastChainDBClient;
    @Autowired
    private DownloadRecordService downloadRecordService;
    @Autowired
    private HomeMapper homeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService roleService;


    // 定义在application.properties
    @Value("${file.path}")
    private String filePath;

    // 定义在application.properties
    @Value("${file.path.down}")
    private String filePathDown;

    // 定义在application.properties
    @Value("${file.server}")
    private String fileServer;

    //@RequiresPermissions("user:view")
    @RequestMapping
    public String info(Model model) {
        return "cert/certinfo.html";
    }

    //	@RequiresPermissions("user:view")
    @ResponseBody
    @RequestMapping("/list")
    public  PageResult<CertInfoFileDTO> list(Integer page, Integer limit, String keyword) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String username = user.getUsername();
        List<Role> roles = roleService.getByUserId(user.getUserId());
        Boolean admin = false;
        System.out.println("-----------");
        System.out.println(roles);
        System.out.println(roles.size());
        for(int i = 0; i < roles.size();i++){
            if(roles.get(i).getRoleId() < 3) {
                admin = true;
                break;
            }
        }
        Map<String, Object> condition= new HashMap<String, Object>();
        if(!admin) condition.put("username", username);
        if(keyword != null) condition.put("keyword", keyword);
        return certInfoService.list(page,limit,condition);
    }

    @RequestMapping("/editForm")
    public String editForm(Model model) {
        //List<Role> roles = roleService.list(false);
        //model.addAttribute("roles", roles);
        return "cert/certinfo_form.html";
    }

    /**
     * 保存文件表修改
     */
    @RequestMapping("/save")
    @ResponseBody
    public JsonResult save(CertInfo certInfo) {
        if(StringUtil.isBlank(certInfo.getFileId())){
            return JsonResult.error("存证文件为空");
        }
        certInfo.setCreateUser(this.getLoginUserName());
        int row = certInfoService.save(certInfo);
        return JsonResult.ok("存证成功");
    }

    /**
     * 保存文件表
     */
    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(CertInfo certInfo) {
        certInfoService.delete(certInfo);
//        certFileService.deleteById(certInfo.getId());
        return JsonResult.ok("删除成功");
    }

    @RequestMapping(value="/uploadsave", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult uploadsave(@RequestParam("uploadImg") MultipartFile multipartFile,
                                 HttpServletRequest request) {

        //如果使用firebug，或者chrome的开发者工具，可以看到，这个文件上传工具发送了两个文件名
        //分别是：name="Filedata"; filename="AVScanner.ini"
        //用这两个文件名获得文件内容都可以，只不过第一个没有后缀，需要自己处理
        //第二个是原始的文件名，但是这个文件名有可能是上传文件时的全路径
        //例如  C:/testssh/a.log，如果是全路径的话，也需要处理

        String fileid=UUIDUtil.randomUUID32();
        byte[] bytes = new byte[0];
        InputStream fileStream = null;


        try {

            String originalFilename = multipartFile.getOriginalFilename();
            String fileType=originalFilename.substring(originalFilename.lastIndexOf("."),originalFilename.length());

            StringBuffer newFilePath=new StringBuffer();
            newFilePath.append(new File(filePath).getAbsolutePath()).append("//").append(fileid).append(fileType);

            StringBuffer filePath2=new StringBuffer();
            filePath2.append(filePathDown).append(fileid).append(fileType);


            File dest = new File(newFilePath.toString());
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

//            fileStream = multipartFile.getInputStream();

//            bytes = FileDownConnManager.readInputStream(fileStream);

            bytes = multipartFile.getBytes();
            String fileHash = SHA256Util.getSHA256Str(bytes);

            multipartFile.transferTo(dest);//存储文件

//            String pathUrl = fileServer + filePath2.toString();
//            try{
//                bytes = FileDownConnManager.fileDown(pathUrl);
//            }catch (Exception ex){
//                System.out.println("input local file is error");
//            }
//            String fileHash = SHA256Util.getSHA256Str(bytes);

            CertFile certFile=new CertFile();
            certFile.setFileId(fileid);
            certFile.setCreateUser(this.getLoginUserName());
            certFile.setFileHash(fileHash);
            certFile.setFileName(originalFilename);
            certFile.setFilePath(filePath2.toString());
            certFile.setFileType(fileType);
            certFile.setFileSize(multipartFile.getSize());

            certFileService.save(certFile);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return JsonResult.ok(fileid);//"586fce27794541b9913131878b165f64,f848d457f55d443eaab16908648a9d6d"
    }


    //@RequiresPermissions("user:view")
    @RequestMapping("/view")
    public String view(String fileid,Model model) {

        CertFile certFile = certFileService.selectById(fileid);
        String transactionid = certFile.getTransactionId();
        model.addAttribute("fileid", fileid);// 将fileid路由给/view
        model.addAttribute("transactionid", transactionid);
        return "cert/certinfo_view.html";
    }

    //@RequiresPermissions("user:audit")
//    @RequestMapping("/audit")
//    public String audit(String fileid,Model model) {
//
//        CertAuditFile certAuditFile = certAuditService.selectById(fileid);
//        String transactionid = certAuditFile.getTransactionId();
//        model.addAttribute("fileid", fileid);// 将fileid路由给/audit
//        model.addAttribute("transactionid", transactionid);
//        return "cert/certaudit.html";
//    }

    //	@RequiresPermissions("user:view")
    @ResponseBody
    @RequestMapping("/dataview")
    public JsonResult dataview(String fileid) {
        String username = homeMapper.username(fileid);
        User user = userMapper.getByUsername(username);
        if(StringUtil.isNotBlank(user.getPhone())) {
            user.setPhone(user.getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        }
        if(StringUtil.isNotBlank(user.getIdcard())) {
            user.setIdcard(user.getIdcard().replaceAll("(\\d{6})\\d{6}(\\d{4})", "$1****$2"));
        }

        CertInfoFileDTO certInfoFileDto= certInfoService.getCertInfoFile(fileid);

        return JsonResult.ok().put("file",certInfoFileDto).put("user",user);
    }

    //@RequiresPermissions("user:view")
    @RequestMapping("/block")
    public String block(String transactionid,Model model) {
        model.addAttribute("transactionid", transactionid);
        return "cert/certinfo_block.html";
    }

    //@RequiresPermissions("user:view")
    @ResponseBody
    @RequestMapping("/blockdata")
    public JsonResult blockdata(String transactionid) {

        //User user = (User) SecurityUtils.getSubject().getPrincipal();
        TransactionQueryDTO transactionQueryDTO=new TransactionQueryDTO();
        transactionQueryDTO.setTransactionId(transactionid);
        ResultDTO<BlockChain> resultDto = fastChainDBClient.getChainByTransactionId(transactionQueryDTO);

        ResultDTO<Long> resultDto2 = fastChainDBClient.getBlockHeight();
        logger.debug("区块链高度："+resultDto2.getData());

        return JsonResult.ok().put("chainblock",resultDto.getData());
    }

    @RequestMapping(value="/downfile")
    @ResponseBody
    public void downfile(String fileid, HttpServletRequest request, HttpServletResponse response) {

        try {

            if(StringUtil.isBlank(fileid)){
                throw new BusinessException("存证文件不存在");
            }
            CertFile certFile = certFileService.selectById(fileid);
            certFile.setLastCheckTime(new Date());

            //验证区块链上面是否一致
            if(!certInfoService.checkCertInfoFile(certFile)){
                throw new BusinessException("存证文件已被修改");
            }
            String dirpath = System.getProperty("user.dir");

            String pathUrl=dirpath+"/public/"+certFile.getFilePath();
            //String str = "http://localhost:8089/uploadfile/fabric-sdk-java-hello-word.pdf";
            File file = new File(pathUrl);
            byte[] bytes = Files.readAllBytes(file.toPath());
            //以下载方式打开
            //response.setHeader("Content-Disposition", "attachment;filename="+certFile.getFileName());

            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(certFile.getFileName(), "UTF-8"));

            //写出
            ServletOutputStream out = response.getOutputStream();
            out.write(bytes);

            //释放资源
            out.flush();
            out.close();

            String fileName = certFile.getFileName();
            addDownloadRecord(fileName, request);

        } catch (Exception e) {
            logger.error("",e);
            throw new BusinessException("文件下载异常");
        }
    }

    /**
     * 添加下载日志
     */
    private void addDownloadRecord(String fileName, HttpServletRequest request) {
        UserAgentGetter agentGetter = new UserAgentGetter(request);
        // 添加到登录日志
        DownloadRecord downloadRecord = new DownloadRecord();
        downloadRecord.setUserId(this.getLoginUserId());
        downloadRecord.setIpAddress(agentGetter.getIpAddr());
        downloadRecord.setFileName(fileName);
        downloadRecord.setUsername(this.getLoginUserName());
        downloadRecordService.add(downloadRecord);
    }



    @RequestMapping(value="/refresh")
    @ResponseBody
    public JsonResult refresh(String fileid,HttpServletResponse response) {

        try {

            if(StringUtil.isBlank(fileid)){
                throw new BusinessException("存证文件不存在");
            }
            CertFile certFile = certFileService.selectById(fileid);
            certFile.setLastCheckTime(new Date());

            //验证区块链上面是否一致
            if(!certInfoService.checkCertInfoFile(certFile)){
                throw new BusinessException("存证文件已被修改");
            }

            return JsonResult.ok(DateUtil.formatDate(certFile.getLastCheckTime()));

        } catch (Exception e) {
            logger.error("",e);
            throw new BusinessException("文件验证异常");
        }
    }

    private static final byte[] input2byte(InputStream inStream)
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
