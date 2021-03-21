package com.oschain.fastchaindb.common.datapage;

/************************************************
 * Copyright (c)  by goldensoft
 * All right reserved.
 * Create Date: 2015-4-10
 * Create Author: lww
 * Last version:  1.0
 * Function:查询实列
 * Last Update Date:
 * Change Log:
**************************************************/	

public class QueryField {
	    private String  Field;
	    private String  Name;
	    private String  Sort;
	    private String  Width;
	    private String  Data;
	    private String  Type;
	    private String  Format;
	    private String  Align;
		private Boolean  IsSum=false;
	    
	    public Boolean getIsSum() {
			return IsSum;
		}
		public void setIsSum(Boolean isSum) {
			IsSum = isSum;
		}
		public String  getField(){return this.Field;}
	    public void setField(String value){this.Field=value;}
	    
	    public String  getName(){return this.Name;}
	    public void setName(String value){this.Name=value;}
	    
	    public String  getSort(){return this.Sort;}
	    public void setSort(String value){this.Sort=value;}
	    
	    public String  getWidth(){return this.Width;}
	    public void setWidth(String value){this.Width=value;}
	    
	    public String  getData(){return this.Data;}
	    public void setData(String value){this.Data=value;}
	    
	    public String  getType(){return this.Type;}
	    public void setType(String value){this.Type=value;}
 
	    public String  getFormat(){return this.Format;}
	    public void setFormat(String value){this.Format=value;}
	    
	    public String  getAlign(){return this.Align;}
	    public void setAlign(String value){this.Align=value;}
}
