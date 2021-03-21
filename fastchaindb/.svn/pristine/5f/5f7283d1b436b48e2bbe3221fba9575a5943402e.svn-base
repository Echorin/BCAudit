package com.oschain.fastchaindb.common.datapage;

import java.util.List;
import java.util.Map;

import com.oschain.fastchaindb.common.datapage.QueryCore.QueryType;


public class QueryData {
    /**　总记录数   */
    private int RowsCount=0;
    /**　分页索引   */
    private int CurrentPageIndex=0;
    /**　分页记录数    */
    private int PageSize=5;
    /**　返回数据集    */
    private List<Map<String, Object>> list;
    /**　数据源信息   */
    private QueryAttr  queryAttr=new QueryAttr();
    /** 是否合计 */
    private Boolean IsTotal;
    /** 查询字段 */
    private String  Fields;
    /** 模型类型 (listconfig,comboconfig,selectconfig)*/
    private QueryType queryType;
    /**　查询SQL语句   */
    private String  Filter;
    /**　查询字段值   */
    private Map<String, Object> FilterParam;
    /**　排序   */
    private String  Sort;
    /**　合计字段[sl1,100]    */
    private Map<String, Object> FieldTotal;


    public String getSort() {
        return Sort;
    }

    public void setSort(String sort) {
        Sort = sort;
    }

    public Map<String, Object> getFieldTotal() {
        return FieldTotal;
    }

    public void setFieldTotal(Map<String, Object> fieldTotal) {
        FieldTotal = fieldTotal;
    }

    public Map<String, Object> getFilterParam() {
        return FilterParam;
    }

    public void setFilterParam(Map<String, Object> filterParam) {
        FilterParam = filterParam;
    }

    public String getFilter() {
        return Filter;
    }

    public void setFilter(String filter) {
        Filter = filter;
    }

    public int getRowsCount() {
        return RowsCount;
    }

    public void setRowsCount(int rowsCount) {
        RowsCount = rowsCount;
    }

    public int getCurrentPageIndex() {
        return CurrentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        CurrentPageIndex = currentPageIndex;
    }

    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int pageSize) {
        PageSize = pageSize;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public QueryAttr getQueryAttr() {
        return queryAttr;
    }

    public void setQueryAttr(QueryAttr queryAttr) {
        this.queryAttr = queryAttr;
    }

    public Boolean getIsTotal() {
        return IsTotal;
    }

    public void setIsTotal(Boolean total) {
        IsTotal = total;
    }

    public String getFields() {
        return Fields;
    }

    public void setFields(String fields) {
        Fields = fields;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }
}
