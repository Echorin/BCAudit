package com.oschain.fastchaindb.cert.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;


/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
@TableName("cert_info")
public class CertInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId
	private Integer id;

	//文件标识
	private String fileTag;

	//区块链ID
//	private String fileHash;

	//创建时间
	private Date createTime;

	//创建用户
	private String createUser;

	//文件标识
	private String signId;

	//文件标识
	@TableField(exist = false)
	private String fileId;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getSignId() {
		return signId;
	}

	public void setSignId(String signId) {
		this.signId = signId;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setFileTag(String fileTag) {
		this.fileTag = fileTag;
	}
	public String getFileTag() {
		return fileTag;
	}
//	public void setFileHash(String fileHash) {
//		this.fileHash = fileHash;
//	}
//	public String getFileHash() {
//		return fileHash;
//	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateUser() {
		return createUser;
	}

}
