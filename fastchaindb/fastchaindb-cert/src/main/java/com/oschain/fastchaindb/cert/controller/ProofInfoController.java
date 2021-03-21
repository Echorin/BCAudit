package com.oschain.fastchaindb.cert.controller;

import com.oschain.fastchaindb.blockchain.dto.BlockChain;
import com.oschain.fastchaindb.blockchain.dto.TransactionQueryDTO;
import com.oschain.fastchaindb.cert.dto.ProofInfoFileDTO;
import com.oschain.fastchaindb.cert.model.*;
import com.oschain.fastchaindb.cert.service.*;
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
@RequestMapping("/proof/info")
public class ProofInfoController extends BaseController {
    @Autowired
    private ProofFileService proofFileService;
    @Autowired
    private ProofInfoService proofInfoService;
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
        return "cert/proofinfo.html";
    }

    //	@RequiresPermissions("user:view")
    @ResponseBody
    @RequestMapping("/list")
    public  PageResult<ProofInfoFileDTO> list(Integer page, Integer limit, String keyword) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        String username = user.getUsername();
        List<Role> roles = roleService.getByUserId(user.getUserId());
        Boolean admin = false;
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
        return proofInfoService.list(page,limit,condition);
    }

    // +证明
    @RequestMapping("/editForm")
    public String editForm(Model model) {
        //List<Role> roles = roleService.list(false);
        //model.addAttribute("roles", roles);
        return "cert/proofinfo_form.html";
    }

    //@RequiresPermissions("user:audit")
    // +审计
    @RequestMapping("/audit")
    public String audit(String fileid,Model model) {

//        CertAuditFile certAuditFile = certAuditService.selectById(fileid);
//        String transactionid = certAuditFile.getTransactionId();
//        model.addAttribute("fileid", fileid);// 将fileid路由给/audit
//        model.addAttribute("transactionid", transactionid);
        return "cert/certaudit_form.html";
    }

    /**
     * 保存文件表修改
     */
    @RequestMapping("/save")
    @ResponseBody
    public JsonResult save(ProofInfo proofInfo) {
        if(StringUtil.isBlank(proofInfo.getFileId())){
            return JsonResult.error("存证文件为空");
        }
        proofInfo.setCreateUser(this.getLoginUserName());
        int row = proofInfoService.save(proofInfo);
        return JsonResult.ok("存证成功");
    }

    /**
     * 保存文件表
     */
    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(ProofInfo proofInfo) {
        proofInfoService.delete(proofInfo);
        return JsonResult.ok("删除成功");
    }

    @RequestMapping(value="/uploadsave", method = RequestMethod.POST)
    @ResponseBody
    public JsonResult uploadsave(@RequestParam("uploadImg") MultipartFile multipartFile,
                                 HttpServletRequest request) {

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

            ProofFile proofFile=new ProofFile();
            proofFile.setFileId(fileid);
            proofFile.setCreateUser(this.getLoginUserName());
            proofFile.setFileHash(fileHash);
            proofFile.setFileName(originalFilename);
            proofFile.setFilePath(filePath2.toString());
            proofFile.setFileType(fileType);
            proofFile.setFileSize(multipartFile.getSize());
            proofFileService.save(proofFile);
            System.out.println("5555555555555555");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JsonResult.ok(fileid);//"586fce27794541b9913131878b165f64,f848d457f55d443eaab16908648a9d6d"
    }


    //@RequiresPermissions("user:view")
    @RequestMapping("/view")
    public String view(String fileid,Model model) {

        ProofFile proofFile = proofFileService.selectById(fileid);
        String transactionid = proofFile.getTransactionId();
        model.addAttribute("fileid", fileid);// 将fileid路由给/view
        model.addAttribute("transactionid", transactionid);
        return "cert/proofinfo_view.html";
    }

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

        ProofInfoFileDTO proofInfoFileDto= proofInfoService.getProofInfoFile(fileid);

        return JsonResult.ok().put("file",proofInfoFileDto).put("user",user);
    }

    //@RequiresPermissions("user:view")
    @RequestMapping("/block")
    public String block(String transactionid,Model model) {
        model.addAttribute("transactionid", transactionid);
        return "cert/proofinfo_block.html";
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
            ProofFile proofFile = proofFileService.selectById(fileid);
            proofFile.setLastCheckTime(new Date());

            //验证区块链上面是否一致
            if(!proofInfoService.checkProofInfoFile(proofFile)){
                throw new BusinessException("存证文件已被修改");
            }
            String dirpath = System.getProperty("user.dir");

            String pathUrl=dirpath+"/public/"+proofFile.getFilePath();
            //String str = "http://localhost:8089/uploadfile/fabric-sdk-java-hello-word.pdf";
            File file = new File(pathUrl);
            byte[] bytes = Files.readAllBytes(file.toPath());

            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(proofFile.getFileName(), "UTF-8"));

            //写出
            ServletOutputStream out = response.getOutputStream();
            out.write(bytes);

            //释放资源
            out.flush();
            out.close();

            String fileName = proofFile.getFileName();
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
            ProofFile proofFile = proofFileService.selectById(fileid);
            proofFile.setLastCheckTime(new Date());

            //验证区块链上面是否一致
            if(!proofInfoService.checkProofInfoFile(proofFile)){
                throw new BusinessException("存证文件已被修改");
            }

            return JsonResult.ok(DateUtil.formatDate(proofFile.getLastCheckTime()));

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
