package com.oschain.fastchaindb.cert.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;


/**
 * 文件表
 * @author kevin
 * @date 2019-05-14 14:53:13
 */
@TableName("cert_file")
public class CertFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId
	private Integer id;

	//文件名称
	private String fileName;

	//文件路径
	private String filePath;

	//文件ID
	private String fileId;

	//文件类型
	private String fileType;

	//区块链ID
	private String fileHash;

	//创建时间
	private Date createTime;

	//创建用户
	private String createUser;

	//文件ID
	private String certSignId;

	//文件ID
	private Integer deleteFlag;

	//区块ID
	private String transactionId;

	//文件大小
	private Long fileSize;

	//最后审核时间
	private Date lastCheckTime;

	public Date getLastCheckTime() {
		return lastCheckTime;
	}

	public void setLastCheckTime(Date lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public String getCertSignId() {
		return certSignId;
	}
	public void setCertSignId(String certSignId) {
		this.certSignId = certSignId;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
	public String getFileHash() {
		return fileHash;
	}
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
