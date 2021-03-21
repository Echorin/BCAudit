package com.oschain.fastchaindb.common.persistence;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oschain.fastchaindb.system.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 * 数据Entity类
 * @author ThinkGem
 * @version 2014-05-16
 */
public abstract class DataEntity<T> extends BaseEntity<T> {

	private static final long serialVersionUID = 1L;
	
	public Date updateTime;	//修改日期
	public Date createTime;	//创建日期
	public Integer createUser;	// 创建者
	public Integer updateUser;	// 更新者
	

	@Override
	public void preInsert() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Object object = subject.getPrincipal();
			if (object != null) {
				User user = (User) object;
				createUser=user.getUserId();
				updateUser=createUser;
			}
		}

		this.updateTime = new Date();
		this.createTime = this.updateTime;
	}

	@Override
	public void preUpdate() {
		Subject subject = SecurityUtils.getSubject();
		if (subject != null) {
			Object object = subject.getPrincipal();
			if (object != null) {
				User user = (User) object;
				updateUser=user.getUserId();
			}
		}
		this.updateTime = new Date();
	}
	
}
