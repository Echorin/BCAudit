package com.oschain.fastchaindb.system.controller;

import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.common.utils.StringUtil;
import com.oschain.fastchaindb.system.model.DownloadRecord;
import com.oschain.fastchaindb.system.service.DownloadRecordService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 下载日志管理
 **/
@Controller
@RequestMapping("/system/downloadRecord")
public class DownloadRecordController{
    @Autowired
    private DownloadRecordService downloadRecordService;

    @RequiresPermissions("downloadRecord:view")
    @RequestMapping()
    public String downloadRecord() {
        return "system/download_record.html";
    }

    /**
     * 查询所有下载日志
     **/
    @RequiresPermissions("downloadRecord:view")
    @ResponseBody
    @RequestMapping("/list")
    public PageResult<DownloadRecord> list(Integer page, Integer limit, String startDate, String endDate, String account) {
        if (StringUtil.isBlank(startDate)) {
            startDate = null;
        } else {
            startDate += " 00:00:00";
        }
        if (StringUtil.isBlank(endDate)) {
            endDate = null;
        } else {
            endDate += " 23:59:59";
        }
        if (StringUtil.isBlank(account)) {
            account = null;
        }
        return downloadRecordService.list(page, limit, startDate, endDate, account);
    }
}
