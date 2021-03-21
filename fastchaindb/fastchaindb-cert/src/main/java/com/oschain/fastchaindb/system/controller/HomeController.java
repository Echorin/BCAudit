package com.oschain.fastchaindb.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.oschain.fastchaindb.cert.service.CertFileService;
import com.oschain.fastchaindb.system.model.Role;
import com.oschain.fastchaindb.system.service.DownloadRecordService;
import com.oschain.fastchaindb.system.service.LoginRecordService;
import com.oschain.fastchaindb.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by kevin on 2018-07-13 上午 11:21.
 */
@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private DownloadRecordService downloadRecordService;

    @Autowired
    private LoginRecordService loginRecordService;

    @Autowired
    private CertFileService certFileService;

    @Autowired
    private RoleService roleService;

    @Value("${fastchaindb.host}")
    private String fastchaindbHost;


    /**
     * 控制台
     */
    @RequestMapping("/console")
    public String console(Model model) throws Exception {

        Integer downloadTotal = downloadRecordService.getDownloadTotal();

        Integer loginTotal = loginRecordService.getLoginTotal();

        BigInteger sumFileSize = certFileService.sumFileSize();
        String sumFilesize = getPrintSize(sumFileSize);

        String url = fastchaindbHost + "/api/v1/getBlockHeight";
        JSONObject getBlockHeight = sendGet(url);
        String blockHeight = getBlockHeight.getString("data");

        model.addAttribute("downloadTotal", downloadTotal);
        model.addAttribute("loginTotal", loginTotal);
        model.addAttribute("blockHeight", blockHeight);
        model.addAttribute("sumFilesize", sumFilesize);


        List loginRecordList = loginRecordService.sumPastLoginRecord();
        List downloadRecordList = downloadRecordService.sumPastDownloadRecord();
        List fileSizeAdditionalRecord = certFileService.fileSizeAdditionalRecord();
        model.addAttribute("downloadRecordList", JSON.toJSONString(downloadRecordList));
        model.addAttribute("loginRecordList", JSON.toJSONString(loginRecordList));
        model.addAttribute("fileSizeAdditionalRecord", JSON.toJSONString(fileSizeAdditionalRecord));


        return "home/console.html";
    }

    /**
     * 游客上传文件/验证
     */
    @RequestMapping("/editForm")
    public String editForm(Model model) {
        //List<Role> roles = roleService.list(false);
        //model.addAttribute("roles", roles);
        return "cert/certinfo_form.html";
    }

    /**
     * 消息弹窗
     */
    @RequestMapping("/message")
    public String message() {
        return "tpl/message.html";
    }

    /**
     * 修改密码弹窗
     */
    @RequestMapping("/password")
    public String password() {
        return "tpl/password.html";
    }

    /**
     * 主题设置弹窗
     */
    @RequestMapping("/theme")
    public String theme() {
        return "tpl/theme.html";
    }

    /**
     * 设置主题
     */
    @RequestMapping("/setTheme")
    public String setTheme(String themeName, HttpServletRequest request) {
        if (null == themeName) {
            request.getSession().removeAttribute("theme");
        } else {
            request.getSession().setAttribute("theme", themeName);
        }
        return "redirect:/";
    }

    /**
     * 控制台
     */
    @RequestMapping("/user")
    public String user(Model model) {
        List<Role> roles = roleService.list(false);
        model.addAttribute("roles", roles);
        return "tpl/user.html";
    }

    private void summary(Model model){

        Integer downloadTotal = downloadRecordService.getDownloadTotal();
        Integer loginTotal = loginRecordService.getLoginTotal();
        model.addAttribute("downloadTotal", downloadTotal);
        model.addAttribute("loginTotal", loginTotal);
        System.out.println(loginTotal);
    }

    public static JSONObject sendGet(String url) throws Exception {
        String USER_AGENT = "Mozilla/5.0";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
        String result = response.toString();
        System.out.println(result);

        //将结果转成JSON格式
        JSONObject resultJSON = JSONObject.parseObject(result);

        return resultJSON;

    }


    // 字节换算
    public static String getPrintSize(BigInteger size) {
        BigInteger base = new BigInteger("1024");
        BigInteger base2 = new BigInteger("100");
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size.compareTo(base) == -1) {
            return String.valueOf(size) + "B";
        } else {
            size = size.divide(base);
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size.compareTo(base) == -1) {
            return String.valueOf(size) + "KB";
        } else {
            size = size.divide(base);
        }
        if (size.compareTo(base) == -1) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size.multiply(base2);
            return String.valueOf(size.divide(base2)) + "."
                    + String.valueOf((size.remainder(base2))) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size.multiply(base2).divide(base);
            return String.valueOf(size.divide(base2)) + "."
                    + String.valueOf(size.remainder(base2)) + "GB";
        }
    }

//
//    /// </summary>
//    /// <param name="List">泛型集合</param>
//    /// <returns></returns>
//    public DataSet  ConvertToDataSet<T>(IList<T> List)
//    {
//        if (list == null || list.Count <= 0)
//        {
//            return null;
//        }
//        DataSet ds = new DataSet();
//        DataTable dt = new DataTable(typeof(T).Name);
//        DataColumn column;
//        DataRow row;
//        System.Reflection.PropertyInfo[] myPropertyInfo = typeof(T).GetProperties(System.Reflection.BindingFlags.Public | System.Reflection.BindingFlags.Instance);
//        foreach (T t in list)
//        {
//            if (t == null)
//            {
//                continue;
//            }
//            row = dt.NewRow();
//            for (int i = 0, j = myPropertyInfo.Length; i < j; i++)
//            {
//                System.Reflection.PropertyInfo pi = myPropertyInfo[i];
//                string name = pi.Name;
//                if (dt.Columns[name] == null)
//                {
//                    column = new DataColumn(name, pi.PropertyType);
//                    dt.Columns.Add(column);
//                }
//                row[name] = pi.GetValue(t, null);
//            }
//            dt.Rows.Add(row);
//        }
//        ds.Tables.Add(dt);
//        return ds;
//    }


//    /**
//     * 添加系统日志
//     */
//    private void addsystemRecord(Date date) {
//        LoginRecord loginRecord = new LoginRecord();
//        loginRecord.setUserId(userId);
//        loginRecord.setOsName(agentGetter.getOS());
//        loginRecord.setDevice(agentGetter.getDevice());
//        loginRecord.setBrowserType(agentGetter.getBrowser());
//        loginRecord.setIpAddress(agentGetter.getIpAddr());
//        loginRecordService.add(loginRecord);
//    }

}
