package com.oschain.fastchaindb.controls.controller;

import com.oschain.fastchaindb.common.BaseController;
import com.oschain.fastchaindb.common.JsonResult;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.common.datapage.*;
import com.oschain.fastchaindb.controls.service.QueryService;
import jodd.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2018-07-13 上午 11:21.
 */
@Controller()
@RequestMapping("/query")
public class QueryController extends BaseController {

    @Resource
    private QueryService queryService;

    /**
     * 控制台
     */
    @ResponseBody
    @RequestMapping("/list/{tableurl}")
    public PageResult<Map<String, Object>> list(@PathVariable String tableurl, String filter, int page, int limit, String sort ) {

//        int pageindex = 1;
//        int pagesize = 10;
//        String sort = "";

        QueryCore queryCore = new QueryCore(tableurl, filter, page, limit, QueryCore.QueryType.listconfig);
        QueryPage queryPage = queryCore.getQueryPage();//可以做扩展操作

        //对条件进行重写,queryPage.getQueryAttr().setFilter()，这样不能赋值是静态变量，会影响后面的结果
        String defaultFilter = queryPage.getQueryAttr().getFilter();//默认条件
        String queryFilter = queryPage.getFilter();//查询传入条件
        queryPage.setSort(sort);
        queryPage = queryService.queryPage(queryPage);
        return new PageResult<>(queryPage.getRowsCount(), queryPage.getList());

    }




    /**
     * 下拉查询控件验证
     * @param combourl
     * @param defquery
     * @param query
     * @param request
     * @param response
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/comboxcheck")
    public QueryReturn comboxcheck(String combourl, String defquery, String query) {

//		QueryCore queryCore=new QueryCore();
//		QueryPage queryPage=queryCore.getComboPageAttr(combourl);
        QueryCore queryCore =new QueryCore(combourl,"",QueryCore.QueryType.comboconfig);
        QueryPage queryPage=queryCore.getQueryPage();//可以做扩展操作

        StringBuilder sbfilter = new StringBuilder();
        Map<String, Object> liFilter=new HashMap<String, Object>();//SQL参数值

        if (StringUtil.isNotEmpty(queryPage.getFilter())) {
            sbfilter.append(" and " + queryPage.getFilter());
        }
        if (StringUtil.isNotEmpty(query)) {
            sbfilter.append(" and " + queryPage.getQueryAttr().getCheckKey()+ " = ? ");
            liFilter.put(queryPage.getQueryAttr().getCheckKey(),query);
        }

        if (StringUtil.isNotEmpty(queryPage.getQueryAttr().getKey())) {
            queryPage.setFields(queryPage.getFields() + "," + queryPage.getQueryAttr().getKey());
        }

        if (sbfilter.length() > 0) {
            queryPage.setFilter(" 1=1 " + sbfilter.toString());
            queryPage.setFilterParam(liFilter);
        }

        QueryReturn qrv = new QueryReturn();
        qrv.setResult(queryService.getCheckValue(queryPage));

        return qrv;
    }

    @ResponseBody
    @RequestMapping("/delete/{tableurl}")
    public JsonResult delete(@PathVariable String tableurl, String id, HttpServletRequest request) {

        QueryCore queryCore =new QueryCore(tableurl,"",QueryCore.QueryType.listconfig);
        QueryPage queryPage=queryCore.getQueryPage();//可以做扩展操作
        QueryDelete queryDelete=new QueryDelete();

        Map<String, QueryForm> form= queryPage.getQueryAttr().getFormAttr();
        QueryForm queryForm = (QueryForm)form.get("delete");
        String[] formVal = queryForm.getFormUrl().split(":");
        if(formVal.length!=2)
        {
            return JsonResult.error("删除失败，参数有误");
        }

        queryDelete.setIp(request.getRemoteAddr());
        queryDelete.setUserId(this.getLoginUserId().toString());
        queryDelete.setProcName(formVal[0]);
        queryDelete.setTableName(formVal[1]);
        //queryDelete.setSqlSessionFactory(queryPage.getQueryAttr().getSqlSessionFactory());
        queryDelete.setDes(queryPage.getQueryAttr().getTitle());
        queryDelete.setId(id);

        queryService.delRecord(queryDelete);

        return JsonResult.ok();
    }

        @RequestMapping("/index")
    public String index() {
        return "system/test.html";
    }


}
