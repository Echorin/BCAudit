package com.oschain.fastchaindb.common.driver;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.druid.pool.DruidDataSource;
import com.oschain.fastchaindb.common.datapage.QueryCore;
import com.oschain.fastchaindb.common.datapage.QueryData;
import com.oschain.fastchaindb.common.datapage.QueryDelete;
import com.oschain.fastchaindb.common.datapage.QueryPage;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.sql.DataSource;


public class JdbcHelper {

	@Resource
	DataSource druidDataSource;

	/**
	 * 获取查询列表(直接传入SQL，参数以#name#形式传入)
	 *@param queryPage
	 */
	public List<Map<String, Object>> queryList(String sql, Map<String, Object> mapFilter) {
		
		ResultSet rs = null;
		Connection conn = null;
		SqlSession session=null;
		DruidDataSource dataSource=null;
		PreparedStatement pstmt=null;
		List<Map<String, Object>> list = null;
		
		try{
			WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) webApplicationContext.getBean("sqlSessionFactory");
			session = sqlSessionFactory.openSession();
			conn = session.getConnection();
			
//			dataSource= (ComboPooledDataSource)webApplicationContext.getBean("dataSource");
//			conn=dataSource.getConnection();
			
			pstmt = conn.prepareStatement(sql.toString());
			SetStatement(sql,mapFilter, pstmt);
			rs = pstmt.executeQuery();
			list = ConvertList(rs);//数据集
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null){
					rs.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(conn!=null){
					conn.close();
				}
				if(session!=null){
					session.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

 
	/**
	 * 获取查询列表(checkbox)
	 *@param queryPage
	 */
	public List<Map<String, Object>> queryList(QueryPage queryPage) {

		ResultSet rs = null;
		Connection conn = null;
		SqlSession session=null;
		PreparedStatement pstmt=null;
		List<Map<String, Object>> list = null;
		DruidDataSource dataSource=null;
		
		try {
			
			WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) webApplicationContext.getBean(queryPage.getQueryAttr().getSqlSessionFactory());
			session = sqlSessionFactory.openSession();
			conn = session.getConnection();
			
//			dataSource= (ComboPooledDataSource)webApplicationContext.getBean("dataSource");
//			conn=dataSource.getConnection();
			
			StringBuffer sql = new StringBuffer();
			
			if(StringUtils.isNotBlank(queryPage.getQueryAttr().getHidFields()))
			{
				queryPage.setFields(queryPage.getQueryAttr().getFields()+","+queryPage.getQueryAttr().getHidFields());
				sql.append("select " + queryPage.getFields() + " from " + queryPage.getQueryAttr().getTableName());
			}
			else {
				sql.append("select " + queryPage.getQueryAttr().getFields() + " from " + queryPage.getQueryAttr().getTableName());
			}
			
			if( StringUtils.isNotBlank(queryPage.getFilter()))
			{
				sql.append(" where "+queryPage.getFilter());
			}
			
			if( StringUtils.isNotBlank( queryPage.getQueryAttr().getGroup()))
			{
				sql.append(" group by "+queryPage.getQueryAttr().getGroup());
			}
		 
			if(StringUtils.isNotBlank(queryPage.getQueryAttr().getSort()))
			{
				sql.append(" order by "+queryPage.getQueryAttr().getSort());
			}

			pstmt = conn.prepareStatement(sql.toString());
			SetStatement(queryPage.getFilterParam(), pstmt);
			rs = pstmt.executeQuery();
			list = ConvertList(rs);//数据集
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null){
					rs.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(session!=null){
					session.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 获取查询对象
	 *@param queryPage
	 */
	protected QueryPage queryPage(QueryPage queryPage) {

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt=null;
		List<Map<String, Object>> list = null;
		Map<String,Object> map=null;
		DruidDataSource  dataSource=null;
		try {
			StringBuffer sql = new StringBuffer();
			StringBuffer sqlsum = new StringBuffer();

			//外面字段已经拼接完成了
			if(queryPage.getQueryType()==QueryCore.QueryType.comboconfig)
			{
				sql.append("select " + queryPage.getFields() + " from " + queryPage.getQueryAttr().getTableName());
			}
			else {
				//如果已经有SQL就不需要拼接了（bstable）
				if(StringUtils.isNotBlank(queryPage.getQueryAttr().getSql())){
					sql.append(queryPage.getQueryAttr().getSql());
				}
				else{
					//传统配置
					if(StringUtils.isNotBlank(queryPage.getQueryAttr().getHidFields()))
					{
						queryPage.setFields(queryPage.getQueryAttr().getFields()+","+queryPage.getQueryAttr().getHidFields());
						sql.append("select " + queryPage.getFields() + " from " + queryPage.getQueryAttr().getTableName());
					}
					else {
						sql.append("select " + queryPage.getQueryAttr().getFields() + " from " + queryPage.getQueryAttr().getTableName());
					}
				}
			}
			
			
			//是否包含合计字段
			if (StringUtils.isNotBlank(queryPage.getQueryAttr().getTotalKey()))
			{
				//如果已经有SQL就不需要拼接了（bstable）
				if(StringUtils.isNotBlank(queryPage.getQueryAttr().getSql())){
					sqlsum.append("select count(1) as rscount," + queryPage.getQueryAttr().getTotalKey() + " "+removeSelect(queryPage.getQueryAttr().getSql()));
				}else{
					sqlsum.append("select count(1) as rscount," + queryPage.getQueryAttr().getTotalKey() + " from " + queryPage.getQueryAttr().getTableName());
				}
				queryPage.setIsTotal(true);
			}
			else
			{	//如果已经有SQL就不需要拼接了（bstable）
				if(StringUtils.isNotBlank(queryPage.getQueryAttr().getSql())){
					sqlsum.append("select count(1) as rscount "+removeSelect(queryPage.getQueryAttr().getSql()));
				}else{
					sqlsum.append("select count(1) as rscount from " + queryPage.getQueryAttr().getTableName());
				}
				queryPage.setIsTotal(false);
			}
			
			if( StringUtils.isNotBlank(queryPage.getFilter()))
			{
				sql.append(" where "+queryPage.getFilter());
				sqlsum.append(" where "+queryPage.getFilter());
				
//				if(StringUtils.isNotBlank(queryPage.getQueryAttr().getFilter())){
//					sql.append(" and "+queryPage.getQueryAttr().getFilter());
//					sqlsum.append("  and "+queryPage.getQueryAttr().getFilter());
//				}
			}
//			else{
//				if(StringUtils.isNotBlank(queryPage.getQueryAttr().getFilter())){
//					sql.append(" where "+queryPage.getQueryAttr().getFilter());
//					sqlsum.append(" where "+queryPage.getQueryAttr().getFilter());
//				}
//			}
			
			if( StringUtils.isNotBlank( queryPage.getQueryAttr().getGroup()))
			{
				sql.append(" group by "+queryPage.getQueryAttr().getGroup());
			}
		 
			if(StringUtils.isNotBlank(queryPage.getSort()))
			{
				sql.append(" order by "+queryPage.getSort());
			}
			else {
				if(StringUtils.isNotBlank(queryPage.getQueryAttr().getSort()))
				{
					sql.append(" order by "+queryPage.getQueryAttr().getSort());
				}
			}
			

			//WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
			
//			SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) webApplicationContext.getBean(queryPage.getQueryAttr().getSqlSessionFactory());
//			session = sqlSessionFactory.openSession();
//			conn = session.getConnection();
			
			//dataSource= (DruidDataSource)webApplicationContext.getBean("dataSource");
			dataSource= (DruidDataSource)druidDataSource;//webApplicationContext.getBean(DataSource.class);
			conn=dataSource.getConnection();
			
			//从mybatis中获取数据源
			//queryPage.getQueryAttr().getDataType().equals("mapping")
			
//			if(queryPage.getQueryAttr().getDataType().equals("mapping")){
//				Map<String, Object> mapFilter=queryPage.getFilterParam();
//				if(mapFilter==null){
//					mapFilter=new HashMap<String, Object>();
//				}
//				//可以直接执行Mybatis
//				Page<Object> page=new Page<Object>(queryPage.getCurrentPageIndex(), queryPage.getPageSize());
//				List<String> listField= queryPage.getQueryAttr().getSumField();
//				if(listField!=null){
//					Map<String, Object> mapTotal = new HashMap<String, Object>();  
//				    for (String field : listField) {  
//				    	mapTotal.put(field, 0);  
//				    }  
//					page.setMapTotal(mapTotal);
//				}
//				mapFilter.put("querypage", page);//分页参数，拦截器用到
//				List<Map<String,Object>> listMap=session.selectList(queryPage.getQueryAttr().getTableName(), mapFilter);
//				queryPage.setList(listMap);
//				queryPage.setFieldTotal(page.getMapTotal());
//				queryPage.setRowsCount(page.getCount());
//			}
//			else {
				
				//获取总记录数
				pstmt = conn.prepareStatement(sqlsum.toString());
				SetStatement(queryPage.getFilterParam(), pstmt);
				rs = pstmt.executeQuery();
				map=ConvertMap(rs);
				queryPage.setRowsCount( Integer.valueOf(map.get("rscount").toString()) );
	
				//获取数据
				int MinRowNum = (queryPage.getCurrentPageIndex()-1) * queryPage.getPageSize();
				if(MinRowNum>=queryPage.getRowsCount())
				{
					MinRowNum=countTotalPage(queryPage.getRowsCount(),queryPage.getPageSize());
				}
				
				//MYSQL
				StringBuffer cmdText = new StringBuffer();
				cmdText.append(sql+" limit "+MinRowNum+","+queryPage.getPageSize());
				pstmt = conn.prepareStatement(cmdText.toString());
				SetStatement(queryPage.getFilterParam(), pstmt);
				rs = pstmt.executeQuery();
				list = ConvertList(rs);//数据集
				
				//ORC
				//int MinRowNum = 1;
				//int MaxRowNum = 20;
				//cmdText.append(" select * from ( select t.*, rownum as rsindex from ( ");
				//cmdText.append(sql);
				//cmdText.append(" ) t  ) where rsindex >= " + MinRowNum +
				//" and rsindex <=" + MaxRowNum + " ");
				
				queryPage.setList(list);
				queryPage.setFieldTotal(map);
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs!=null){
					rs.close();
				}
				if(pstmt!=null){
					pstmt.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return queryPage;
	}
	
	
	/**
	 * 删除表数据
	 * @param queryPage
	 * @param id
	 * @return 0成功，1失败
	 */
	public String queryDelete(QueryDelete queryDelete)
	{
		Connection conn = null;
		DruidDataSource  dataSource=null;
		CallableStatement cstmt=null;
		String ref_msg=null;
		try {

			dataSource= (DruidDataSource)druidDataSource;//webApplicationContext.getBean(DataSource.class);
			conn=dataSource.getConnection();
			
			//参数名：i_id,i_tablename,i_ip,i_userid,i_des,o_msg
			cstmt= conn.prepareCall("{call "+queryDelete.getProcName()+"(?,?,?,?,?,?)}");
			cstmt.setString(1, queryDelete.getId());
			cstmt.setString(2, queryDelete.getTableName());
			cstmt.setString(3, queryDelete.getIp());
			cstmt.setString(4, queryDelete.getUserId());
			cstmt.setString(5, queryDelete.getDes());
			cstmt.registerOutParameter(6, Types.VARCHAR);
			
			cstmt.executeUpdate();
			ref_msg = cstmt.getString(6);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}  
		finally  
		{
			try {
				if(cstmt!=null){
					cstmt.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return ref_msg;
	}

	/**
	 * SQL预编译
	 * 
	 * @param sql
	 * @param sqlparm
	 * @param pstmt
	 */
	private void SetStatement(Map<String,Object> sqlparm, PreparedStatement pstmt) {

		if (sqlparm == null)
			return;

		try {

			int parmindex = 1;
			for (Object key : sqlparm.keySet()) {
				Object value = sqlparm.get(key);
				pstmt.setObject(parmindex, value);
				parmindex++;
			}


			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * SQL预编译
	 * 
	 * @param sql
	 * @param sqlparm
	 * @param pstmt
	 */
	private void SetStatement(String sql,Map<String,Object> sqlparm, PreparedStatement pstmt) {

		if (sqlparm == null)
			return;

		try {

			// SQL:username=#username#,根据username顺序写入值
			int parmindex = 1;
			String regex = "#(.*?)#";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(sql);
			while (m.find()) {
				String key = m.group(1).trim().toLowerCase();
				if (sqlparm.containsKey(key)) {
					pstmt.setObject(parmindex, sqlparm.get(key));
					parmindex++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public Map<String,Object> ConvertMap(ResultSet rs)
	{
		Map<String,Object> map = new LinkedHashMap<String,Object>();
		if(rs==null)return map;
		
		try
		{
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					map.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
				}
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

		return map;
	}
	
	public List<Map<String, Object>> ConvertList(ResultSet rs) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (rs == null)
			return list;

		try {
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					// if (Types.CLOB == md.getColumnType(i)) {
					// Clob clob = (Clob) rs.getObject(i);
					// map.put(md.getColumnName(i), ClobToString(clob));
					// } else {
				
					map.put(md.getColumnLabel(i).toLowerCase(), rs.getObject(i));
					//map.put(md.getColumnName(i).toLowerCase(), rs.getObject(i)); orc
					// }
				}
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	private String removeSelect(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql + " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	private String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private String removeGroupBy(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("group\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private  int countTotalPage(int rows, int pagesize) {
		if(rows==0)return 0;
		if (rows % pagesize == 0)
			return (rows / pagesize)-1;
		else
			return Integer.valueOf(rows / pagesize);
	}
	
}
