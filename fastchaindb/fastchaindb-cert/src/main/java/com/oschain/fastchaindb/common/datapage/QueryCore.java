package com.oschain.fastchaindb.common.datapage;

import com.oschain.fastchaindb.common.config.InitService;
import com.oschain.fastchaindb.common.utils.ObjectUtil;
import com.oschain.fastchaindb.common.utils.SpringContextHolder;
import com.oschain.fastchaindb.common.utils.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;


/************************************************
 * Copyright (c)  by goldensoft
 * All right reserved.
 * Create Date: 2015-4-10
 * Create Author: lww
 * Last version:  1.0
 * Function:查询配置
 * Last Update Date:
 * Change Log:
 **************************************************/

public class QueryCore {

	public enum QueryType {listconfig,comboconfig,selectconfig}
	private static InitService initService = SpringContextHolder.getBean(InitService.class);
	
	private com.oschain.fastchaindb.common.datapage.QueryPage queryPage=new com.oschain.fastchaindb.common.datapage.QueryPage();
	public com.oschain.fastchaindb.common.datapage.QueryPage getQueryPage() {
		return queryPage;
	}

	private com.oschain.fastchaindb.common.datapage.QueryData queryData=new com.oschain.fastchaindb.common.datapage.QueryData();
	public com.oschain.fastchaindb.common.datapage.QueryData getQueryData() {
		return queryData;
	}

	public QueryCore(){
	}
	
	public QueryCore(String tableUrl,String filters,QueryType queryType)
	{
		queryPage.setQueryType(queryType);
		queryPage.setQueryAttr(InitService.getQueryAttr(tableUrl, queryType));
		setFilter(filters,queryType);
	}
	
	public QueryCore(String tableUrl,String filters,int currentPageIndex,QueryType queryType)
	{
		queryPage.setQueryType(queryType);
		QueryAttr queryAttr=initService.getQueryAttr(tableUrl, queryType);
		queryPage.setQueryAttr(queryAttr);
		setFilter(filters,queryType);
		queryPage.setCurrentPageIndex(currentPageIndex);
	}
	
	public QueryCore(String tableUrl,String filters,int currentPageIndex,int pageSize,QueryType queryType)
	{		
		queryPage.setQueryType(queryType);
		queryPage.setQueryAttr(InitService.getQueryAttr(tableUrl, queryType));
		setFilter(filters,queryType);
		queryPage.setCurrentPageIndex(currentPageIndex);
		queryPage.setPageSize(pageSize);
	}
 
 
	/** 
	 * 生成查询参数,create by lww at 2013.10.18
	 * SQL条件：username=?
	 * SQL参数值：list.add(1);
	 * */
	public void setFilter(String json,QueryType queryType) {

		if (ObjectUtil.isNullOrEmptyString(json))
			return;
		
		if(queryType.equals(QueryType.comboconfig))
			return;

		StringBuffer sbfilter = new StringBuffer();//查询SQL
		Map<String,Object> mapFilter = new LinkedHashMap<String,Object>();//SQL参数值
		//List<String> fieldName = queryPage.getQueryAttr().getFieldName();//显示字段，安全过滤
		
		JSONObject objjson = (JSONObject) JSONObject.fromObject(json);
		JSONArray arr = JSONArray.fromObject(objjson.get("filter"));
		for (int i = 0; i < arr.size(); i++) {
			JSONObject obj = (JSONObject) arr.get(i);

			String val = "", dbfield = "", compare = "", logical = "";
			if (obj.containsKey("dbfield")) {
				dbfield = StringUtil.escapeSql((String) obj.get("dbfield"));
			}
			
//			if(!fieldName.contains(dbfield.toLowerCase()))
//			{
//				continue;
//			}

			if (obj.containsKey("compare"))
			{
				compare = obj.get("compare").toString().toLowerCase();
				if(StringUtil.isBlank(compare))
					compare="like";
			}
			else
				compare = "like";

			if (obj.containsKey("logical"))
			{
				logical = (String) obj.get("logical");
				if(StringUtil.isBlank(logical))
					logical="and";
			}
			else
				logical = "and";

			//值不为空
			if (obj.containsKey("value")) {
				if (ObjectUtil.isNotEmpty((String) obj.get("value"))) {
					if (compare.equals("like"))
						val = "%" + (String) obj.get("value") + "%";
					else if (compare.equals("leftlike"))
						val = "%" + (String) obj.get("value");
					else if (compare.equals("rightlike"))
						val = (String) obj.get("value") + "%";
					else
						val = (String) obj.get("value");
				}
			}

			if (ObjectUtil.isNotEmpty(val) && ObjectUtil.isNotEmpty(dbfield)) {
				sbfilter.append(GetFilterString(dbfield, compare, logical,mapFilter,val));
				//mapFilter.put(dbfield,val);
			}
		}
		
		if (sbfilter.length() > 0) {
			queryPage.setFilter(" 1=1 " + sbfilter.toString());
			queryPage.setFilterParam(mapFilter);
		}
	}
	
	/** 
	 * 生成查询参数,create by lww at 2013.10.18
	 * SQL条件：username=? and pwd like ?
	 * SQL参数值：list.add("a"),list.add("%a%");
	 * */
	/*
	public static QueryFilter getFilter(String json, Class arg1) {
		if (ObjectUtils.isNullOrEmptyString(json))
			return null;

		QueryFilter q=new QueryFilter();
		StringBuffer sbfilter = new StringBuffer();//查询SQL
		Map<String,Object> mapFilter = new LinkedHashMap<String,Object>();//SQL参数值
		
		List<String> fieldName = new ArrayList<String>();// 显示字段，安全过滤
		Field[] f = arg1.getDeclaredFields();
		for (int i = 0; i < f.length; i++) {
			fieldName.add(f[i].getName().toUpperCase());
		}
		f = arg1.getSuperclass().getDeclaredFields();
		for (int i = 0; i < f.length; i++) {
			fieldName.add(f[i].getName().toUpperCase());
		}

		JSONObject objjson = (JSONObject) JSONObject.fromObject(json);
		JSONArray arr = JSONArray.fromObject(objjson.get("filter"));
		for (int i = 0; i < arr.size(); i++) {
			JSONObject obj = (JSONObject) arr.get(i);

			String val = "", dbfield = "", compare = "", logical = "";
			if (obj.containsKey("dbfield")) {
				dbfield = (String) obj.get("dbfield");
			}
			
			if(!fieldName.contains(dbfield.toUpperCase()))
			{
				continue;
			}

			if (obj.containsKey("compare"))
			{
				compare = obj.get("compare").toString().toLowerCase();
				if(StringUtil.isBlank(compare))
					compare="equal";
			}
			else
				compare = "equal";

			if (obj.containsKey("logical"))
			{
				logical = (String) obj.get("logical");
				if(StringUtil.isBlank(logical))
					logical="and";
			}
			else
				logical = "and";

			//值不为空，防止SQL注入
			//如下写法（test%' or 1=1 and name like '%），PreparedStatement会将'转义成\',屏蔽SQL注入
			if (obj.containsKey("value")) {
				if (ObjectUtils.isNotEmpty((String) obj.get("value"))) {
					if (compare.equals("like"))
						val = "%" + (String) obj.get("value") + "%";
					else if (compare.equals("leftlike"))
						val = "%" + (String) obj.get("value");
					else if (compare.equals("rightlike"))
						val = (String) obj.get("value") + "%";
					else
						val = (String) obj.get("value");
				}
			}

			if (ObjectUtils.isNotEmpty(val) && ObjectUtils.isNotEmpty(dbfield)) {
				sbfilter.append(GetFilterString(dbfield, compare, logical,mapFilter,val));
				//mapFilter.put(dbfield, val);
			}
		}
		
		if (sbfilter.length() > 0) {
			q.setFilter(" 1=1 " + sbfilter.toString());
			q.setFilterParam(mapFilter);
		}
		
		return q;
	}
	*/

	/**
	 * 拼接SQL语句，如pm like ? and cz =?
	 * @param dbfield
	 * @param compare
	 * @param logical
	 * @return
	 */
	private static String GetFilterString(String dbfield, String compare, String logical,Map<String,Object> mapFilter,String val) {
		StringBuffer sbfilter = new StringBuffer();
		if (compare.equals("equal"))
			sbfilter.append(logical + "  " + dbfield + " = ? ");
		else if (compare.equals("notequal"))
			sbfilter.append(logical + "  " + dbfield + " != ? ");
		else if (compare.equals("less"))
			sbfilter.append(logical + "  " + dbfield + " < ? ");
		else if (compare.equals("lessorequal")){
			//日期特殊处理
			if(dbfield.contains("_date")){
				mapFilter.put("e_"+dbfield, val+" 23:59:59");//添加参数
				sbfilter.append(logical + "  " + dbfield + " <= ? ");
				return sbfilter.toString();
			}
			sbfilter.append(logical + "  " + dbfield + " <= ? ");
		}
		else if (compare.equals("more"))
			sbfilter.append(logical + "  " + dbfield + " > ? ");
		else if (compare.equals("moreorequal")){
			//日期特殊处理
			if(dbfield.contains("_date")){
				mapFilter.put("s_"+dbfield, val+" 00:00:00");//添加参数
				sbfilter.append(logical + "  " + dbfield + " >= ? ");
				return sbfilter.toString();
			}
			sbfilter.append(logical + "  " + dbfield + " >= ? ");
		}
		else if (compare.equals("like") || compare.equals("leftlike") || compare.equals("rightlike"))
			sbfilter.append(logical + "  " + dbfield + " like ? ");
		else if (compare.equals("notlike"))
			sbfilter.append(logical + "  " + dbfield + " no like ? ");
		else if (compare.equals("in"))
			sbfilter.append(logical + "  " + dbfield + " in (?) ");

		mapFilter.put(dbfield, val);//添加参数
		return sbfilter.toString();
	}


//	public static void main(String args[]) throws DocumentException {
//
//		//		PageBasic pc = new PageBasic();
//		//		String json="{\"action\": \"search\",\"cmdtxt\": \"\",\"filter\":[{\"value\":\"1\",\"dbfield\":\"user_code\",\"dbtype\":\"var\",\"compare\":\"Like\",\"logical\":\"And\"},{\"value\":\"2\",\"dbfield\":\"user_name\",\"dbtype\":\"num\",\"compare\":\"Equal\",\"logical\":\"Or\"},{\"value\":\"2013-09-26\",\"dbfield\":\"user_date\",\"dbtype\":\"date\",\"compare\":\"MoreOrEqual\",\"logical\":\"And\"},]}";
//		//
//		//		//String path=FileUtils.getRelativeDir("config/listconfig.xml");
//		//
//		//		//String path = "D:/java/listconfig.xml";
//		//		PageQuery p=pc.GetPageAttr("kc_wmzx");
//		//		p.setMapFilter(pc.GetFilter(json));
//		//
//		//		PageDao dao = new PageDao();
//		//
//		//		List<Map<String, String>> li = dao.PageGetQueryByMap(p,true);
//		//
//		//		System.out.println(pc.CreateFixTable(p, li,"fixtable","kc_wmzx", true));
//		//
//		//		Map<String,Object> map = pc.GetFilter(json);
//		//		String filter = map.get("$filter$").toString();
//		//
//		//		System.out.println(filter);
//		//		PagerConfig pc = new PagerConfig();
//		//		PagerQuery pager=pc.GetPageAttr("kc_wmzx", "D:/listconfig1.xml");
//		//
//		//		Map<String, Object> FieldAttr =pager.getFieldAttr();
//		//		PagerField field = (PagerField)FieldAttr.get("pm_");
//		//
//		//		System.out.println(pager.getFilter()+"->>"+field.getName()+"-->>"+pager.getFields());
//	}

}
