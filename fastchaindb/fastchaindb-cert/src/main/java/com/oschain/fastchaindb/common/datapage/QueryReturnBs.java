package com.oschain.fastchaindb.common.datapage;


public class QueryReturnBs {

	 /** 返回码 result   1表示成功 0表示无数据 -1或其它表示error*/
    private int total=0;
    /** 数据集    rows    **/
    private Object rows;
    private String result="1";
    private String msg = "";
    
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public Object getRows() {
		return rows;
	}
	public void setRows(Object rows) {
		this.rows = rows;
	}

}
