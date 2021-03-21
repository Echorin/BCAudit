/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.oschain.fastchaindb.common.config;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oschain.fastchaindb.common.datapage.QueryAttr;
import com.oschain.fastchaindb.common.datapage.QueryConfig;
import com.oschain.fastchaindb.common.datapage.QueryCore.QueryType;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 * @author kevin
 * @version 2013-12-05
 */

@Service
@Transactional(readOnly = true)
public class InitService implements InitializingBean {
	
	private static  Map<String, QueryAttr> listAttr=null;
	private static  Map<String, QueryAttr> combAttr=null;
	private static  Map<String, QueryAttr> selectAttr=null;
	
	public static QueryAttr getQueryAttr(String url,QueryType queryType ){
		
//		 Iterator<Map.Entry<String, QueryAttr>> it = listAttr.entrySet().iterator();
//		  while (it.hasNext()) {
//			  Map.Entry<String, QueryAttr> entry = it.next();
//			  QueryAttr queryAttr = entry.getValue();
//			  System.out.println("key= " + entry.getKey() );
//		  }

		if(queryType.equals(QueryType.listconfig))
		{
			return listAttr.get(url);
		}
		else if(queryType.equals(QueryType.comboconfig))
		{
			return combAttr.get(url);
		}
		else {
			return selectAttr.get(url);
		}
	}


	///////////////// Synchronized to the Activiti //////////////////
	
	// 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java

	/**
	 * 是需要同步Activiti数据，如果从未同步过，则同步数据。
	 */
	public void afterPropertiesSet() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n======================================================================\r\n");
		sb.append("\r\n    欢迎使用FastChainDB \r\n");
		sb.append("\r\n======================================================================\r\n");
		System.out.println(sb.toString());
		
		//初始化DATASET数据源
		if(listAttr==null){
			listAttr = QueryConfig.getQueryPageAttr("dataconfig");
		}
		if(combAttr==null){
			//combAttr = QueryConfig.getComboPageAttr("comboconfig");
		}
		if(selectAttr==null){
			//selectAttr = QueryConfig.getSelectPageAttr("selectconfig");
		}
	}
	
	
	///////////////// Synchronized to the Activiti end //////////////////
	
}
