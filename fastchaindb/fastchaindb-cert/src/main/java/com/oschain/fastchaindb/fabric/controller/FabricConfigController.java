package com.oschain.fastchaindb.fabric.controller;

import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.fabric.model.FabricConfig;
import com.oschain.fastchaindb.fabric.service.FabricConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 
 * @author kevin
 * @date 2019-09-03 13:47:41
 */
@Controller
@RequestMapping("/fabric/fabricconfig")
public class FabricConfigController {
	@Autowired
	private FabricConfigService fabricConfigService;
	
	/**
	 * 查询列表
	 */
	@RequestMapping("/list")
	//@RequiresPermissions("fabric:config:list")
	public String list(){
		return "fabric/fabricconfig_list.html";
	}

    /**
     * 编辑表单
     */
    @RequestMapping("/form")
    //@RequiresPermissions("fabric:config:form")
    public String form() {
        return "fabric/fabricconfig.html";
    }

    /**
     * 获取表单数据
     */
    @ResponseBody
    @RequestMapping("/get")
    //@RequiresPermissions("fabric:config:view")
    public JsonResult get(String id) {
		FabricConfig fabricConfig = fabricConfigService.get(id);
        return JsonResult.ok().put("data",fabricConfig);
    }

    /**
     * 添加
     */
    @ResponseBody
    @RequestMapping("/add")
    //@RequiresPermissions("fabric:config:add")
    public JsonResult add(FabricConfig fabricConfig) {
        if (fabricConfigService.add(fabricConfig)>0) {
            return JsonResult.ok("添加成功");
        }
        return JsonResult.error("添加失败");
    }

    /**
     * 修改
     */
    @ResponseBody
    @RequestMapping("/update")
    //@RequiresPermissions("fabric:config:edit")
    public JsonResult update(FabricConfig fabricConfig) {
        if (fabricConfigService.update(fabricConfig)>0) {
            return JsonResult.ok("修改成功");
        }
        return JsonResult.error("修改失败");
    }

}
