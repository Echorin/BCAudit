package com.oschain.fastchaindb.cert.controller;

import com.oschain.fastchaindb.cert.model.CertFile;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.cert.service.CertFileService;
import com.oschain.fastchaindb.cert.service.CertInfoService;
import com.oschain.fastchaindb.common.BaseController;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.common.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 文件表
 *
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
@Controller
@RequestMapping("/cert/file")
public class CertFileController extends BaseController {
    @Autowired
    private CertFileService certFileService;
    @Autowired
    private CertInfoService certInfoService;


    //@RequiresPermissions("user:view")
    @RequestMapping
    public String file(Model model) {
        return "cert/certfile.html";
    }

    //	@RequiresPermissions("user:view")
    @ResponseBody
    @RequestMapping("/list")
    public  PageResult<CertFile> list(Integer page, Integer limit) {
        Map<String, Object> condition=null;
       return certFileService.list(page,limit,condition);
    }

    @RequestMapping("/editForm")
    public String editForm(Model model) {
        //List<Role> roles = roleService.list(false);
        //model.addAttribute("roles", roles);
        return "cert/certfile_form.html";
    }

    /**
     * 保存文件表
     */
    @RequestMapping("/save")
    @ResponseBody
    public JsonResult save(CertInfo certInfo) {
        certInfo.setCreateUser(this.getLoginUserName());
        int row = certInfoService.save(certInfo);
        return JsonResult.ok("添加成功");
    }

    /**
     * 保存文件表
     */
    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(CertFile certFile) {
        certFileService.deleteById(certFile.getId());
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
        String fileAlias = multipartFile.getOriginalFilename();

        //multipartFile.transferTo();

        byte[] bytes = new byte[0];
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String s = new String(bytes);
        String fileid=UUIDUtil.randomUUID32();

        System.out.println("Spring MVC获得的文件名：" + fileAlias+"-->"+s);

        Map<String,String> fileMap=new HashMap<>();
        fileMap.put("fileid",fileid);

        return JsonResult.ok().put("key","1");
    }

}
