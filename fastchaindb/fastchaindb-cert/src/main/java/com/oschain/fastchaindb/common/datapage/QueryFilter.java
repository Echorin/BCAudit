package com.oschain.fastchaindb.common.datapage;

public class QueryFilter {
	private String Filter;// 查询条件
	private java.util.Map<String, Object> FilterParam;// 字段名称

	public String getFilter() {
		return Filter;
	}

	public void setFilter(String filter) {
		Filter = filter;
	}

	public java.util.Map<String, Object> getFilterParam() {
		return FilterParam;
	}

	public void setFilterParam(java.util.Map<String, Object> filterParam) {
		FilterParam = filterParam;
	}

	 
}
