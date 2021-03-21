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

public class QueryForm {
	private String  FormUrl;
    private String  FormPkid;
    private String  FormTitle;
    private String  FormCmd;
    private String  FormName;
    private String  FormMode;

    public String getFormName() {
		return FormName;
	}
	public void setFormName(String formName) {
		FormName = formName;
	}
	public String  getFormUrl(){return this.FormUrl;}
    public void setFormUrl(String value){this.FormUrl=value;}
    
    public String  getFormPkid(){return this.FormPkid;}
    public void setFormPkid(String value){this.FormPkid=value;}
    
    public String  getFormTitle(){return this.FormTitle;}
    public void setFormTitle(String value){this.FormTitle=value;}
    
    public String  getFormCmd(){return this.FormCmd;}
    public void setFormCmd(String value){this.FormCmd=value;}
	public String getFormMode() {
		return FormMode;
	}
	public void setFormMode(String formMode) {
		FormMode = formMode;
	}
    
    
}
