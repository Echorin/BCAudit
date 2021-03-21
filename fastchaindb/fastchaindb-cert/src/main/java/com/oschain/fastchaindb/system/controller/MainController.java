package com.oschain.fastchaindb.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oschain.fastchaindb.blockchain.dto.BlockChain;
import com.oschain.fastchaindb.blockchain.dto.Transaction;
import com.oschain.fastchaindb.blockchain.dto.TransactionQueryDTO;
import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.client.FastChainDBClient;
import com.oschain.fastchaindb.client.dto.ResultDTO;
import com.oschain.fastchaindb.common.BaseController;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.common.utils.SHA256Util;
import com.oschain.fastchaindb.common.utils.StringUtil;
import com.oschain.fastchaindb.common.utils.UserAgentGetter;
import com.oschain.fastchaindb.system.dao.HomeMapper;
import com.oschain.fastchaindb.system.model.*;
import com.oschain.fastchaindb.system.service.*;
import com.wf.captcha.utils.CaptchaUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MainController
 */
@Controller
public class MainController extends BaseController implements ErrorController {
    @Autowired
    private AuthoritiesService authoritiesService;
    @Autowired
    private LoginRecordService loginRecordService;
    @Autowired
    private HomeMapper homeMapper;
    @Autowired
    private DownloadRecordService downloadRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RoleService roleService;

    @Autowired
    private FastChainDBClient fastChainDBClient;

    @Autowired
    private HomeService homeService;

    @Value("${fastchaindb.host}")
    private String fastchaindbHost;

    /**
     * 主页
     */
    @GetMapping("/home")
    public String home(Model model) throws Exception {


        Integer loginTotal = loginRecordService.getLoginTotal();
        Integer sumUserNum = homeService.sumUserNum();
        Integer sumFileNum = homeService.sumFileNum();
        System.out.println(sumFileNum);


        String url = fastchaindbHost + "/api/v1/getBlockHeight";
        JSONObject getBlockHeight = HomeController.sendGet(url);
        String blockHeight = getBlockHeight.getString("data");


        model.addAttribute("sumUserNum", sumUserNum);
        model.addAttribute("loginTotal", loginTotal);
        model.addAttribute("blockHeight", blockHeight);
        model.addAttribute("sumFileNum", sumFileNum);


        return "home.html";
    }

    /**
     * 主页
     */
    @RequestMapping({"/", "/index"})
    public String index(Model model) {

        //List<Authorities> authorities = authoritiesService.listByUserId(getLoginUserId());

        List<Authorities> authorities = (List<Authorities>)redisTemplate.opsForValue().get("authorities:"+getLoginUserId());

        User user = getLoginUser();
        List<Role> roles = roleService.getByUserId(user.getUserId());
        user.setRoles(roles);
        List<Map<String, Object>> menuTree = getMenuTree(authorities, -1);
        model.addAttribute("menus", menuTree);
        model.addAttribute("login_user", user);
        model.addAttribute("user_id", user.getUserId());
        model.addAttribute("login_user_param", JSON.toJSONString(user));
        return "index.html";
    }


    /**
     * 登录页
     */
    @GetMapping("/login")
    public String login() {
//        if (getLoginUser() != null) {
//            return "redirect:index";
//        }
        return "login.html";
    }

    /**
     * 游客查验页
     */
    @GetMapping("/certcheck")
    public String certcheck() {
//        if (getLoginUser() != null) {
//            return "redirect:index";
//        }
        return "certcheck.html";
    }

    /**
     * 查验源文件
     **/
    @RequestMapping(value="/certcheck", method = RequestMethod.POST)
    @ResponseBody
    public CertCheck checkbyfile(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request){
        byte[] bytes = new byte[0];
        CertCheck certInfo =new CertCheck();
        try {
            CertFile certFile =new CertFile();
            bytes = multipartFile.getBytes();
            String fileHash_local = SHA256Util.getSHA256Str(bytes);
            System.out.println("fileHash_local:" + fileHash_local);
            certInfo = homeMapper.certInfo(fileHash_local);
            if(certInfo == null){
                certInfo.setFlag(false);
                return certInfo;
            }
            else{
                TransactionQueryDTO transactionQueryDTO=new TransactionQueryDTO();
                transactionQueryDTO.setTransactionId(certInfo.getTransactionId());

                ResultDTO<BlockChain> resultDto = fastChainDBClient.getChainByTransactionId(transactionQueryDTO);
                if(resultDto!=null){
                    List<Transaction> transactionList=resultDto.getData().getTransactionList();
                    if(transactionList!=null && transactionList.size()>0){
                        Transaction transaction=transactionList.get(0);
                        //Mrsu debug
                        String fileHash_block = transaction.getTransactionData();
                        System.out.println("fileHash_block:" + fileHash_block);
//                String fileId = transaction.getTransactionId();
//                CertFileBlockDTO certFileBlockDTO= GsonUtil.gsonToBean(json,CertFileBlockDTO.class);
                        if(fileHash_block.equals(fileHash_local)){
                            //更新最后验证时间
                            certInfo.setFlag(true);
                            return certInfo;
                        }
                    }
                }
                certInfo.setFlag(false);
                return certInfo;
            }


        } catch (IOException e) {
            e.printStackTrace();
            certInfo.setFlag(false);
            return certInfo;
        }

    }
    /**
     * 查验源文件
     **/
    @ResponseBody
    @RequestMapping(value="/checkbyid")
    public CertCheck checkbyid(@RequestParam("certid") String certid){
        CertCheck certInfo = new CertCheck();
        certInfo = homeMapper.certInfobyId(certid);
        if(certInfo == null){
            certInfo.setFlag(false);
            return certInfo;
        }else{
            certInfo.setFlag(true);
            return certInfo;
        }
    }

    /**
     * 注册页
     */
    @GetMapping("/register")
    public String register() {
//        if (getLoginUser() != null) {
//            return "redirect:index";
//        }
        return "register.html";
    }


    /**
     * 添加用户
     **/
    @ResponseBody
    @RequestMapping("/register/add")
    public JsonResult register(User user) {

        //注册帐号默认为普通角色
        List<Role> roles = new ArrayList<>();
        roles.add(new Role(3));
        user.setRoles(roles);

        if (userService.add(user)) {
            return JsonResult.ok("注册成功");
        } else {
            return JsonResult.error("注册失败");
        }
    }

    /**
     * 登录
     */
    @ResponseBody
    @PostMapping("/login")
    public JsonResult doLogin(String username, String password, String code, HttpServletRequest request) {
        if (StringUtil.isBlank(username, password)) {
            return JsonResult.error("账号密码不能为空");
        }
//        if (!CaptchaUtil.ver(code, request)) {
//            CaptchaUtil.clear(request);
//            return JsonResult.error("验证码不正确");
//        }
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
//            SecurityUtils.getSubject().isPermitted("");
            SecurityUtils.getSubject().login(token);
            addLoginRecord(getLoginUserId(), request);
            return JsonResult.ok("登录成功");
        } catch (IncorrectCredentialsException ice) {
            return JsonResult.error("密码错误");
        } catch (UnknownAccountException uae) {
            return JsonResult.error("账号不存在");
        } catch (LockedAccountException e) {
            return JsonResult.error("账号被锁定");
        } catch (ExcessiveAttemptsException eae) {
            return JsonResult.error("操作频繁，请稍后再试");
        }
    }

    /**
     * 图形验证码，用assets开头可以排除shiro拦截
     */
    @RequestMapping("/assets/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) {
        try {
//            CaptchaUtil.outPng(request, response);
            CaptchaUtil.out(request, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * iframe页
     */
    @RequestMapping("/iframe")
    public String error(String url, Model model) {
        model.addAttribute("url", url);
        return "tpl/iframe.html";
    }

    /**
     * 错误页
     */
    @ApiIgnore//使用该注解忽略这个API
    @RequestMapping("/error")
    public String error(String code) {
        if ("403".equals(code)) {
            return "error/403.html";
        }
        return "error/404.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * 递归转化树形菜单
     */
    private List<Map<String, Object>> getMenuTree(List<Authorities> authorities, Integer parentId) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < authorities.size(); i++) {
            Authorities temp = authorities.get(i);
            if (temp.getIsMenu().equals(0) && parentId.equals(temp.getParentId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("menuName", temp.getAuthorityName());
                map.put("menuIcon", temp.getMenuIcon());
                map.put("menuUrl", StringUtil.isBlank(temp.getMenuUrl()) ? "javascript:;" : temp.getMenuUrl());
                map.put("subMenus", getMenuTree(authorities, authorities.get(i).getAuthorityId()));
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 添加登录日志
     */
    private void addLoginRecord(Integer userId, HttpServletRequest request) {
        UserAgentGetter agentGetter = new UserAgentGetter(request);
        // 添加到登录日志
        LoginRecord loginRecord = new LoginRecord();
        loginRecord.setUserId(userId);
        loginRecord.setOsName(agentGetter.getOS());
        loginRecord.setDevice(agentGetter.getDevice());
        loginRecord.setBrowserType(agentGetter.getBrowser());
        loginRecord.setIpAddress(agentGetter.getIpAddr());
        loginRecordService.add(loginRecord);
    }

    /**
     * 获取当前登录的userId
     */
    public Integer getLoginUserId() {
        User loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUserId();
    }

    /**
     * 获取当前登录的username
     */
    public String getLoginUserName() {
        User loginUser = getLoginUser();
        return loginUser == null ? null : loginUser.getUsername();
    }

}
