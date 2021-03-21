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
@TableName("cert_audit")
public class CertAuditFile implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId
	private Integer id;
	//审计报告文件名称
	private String reportName;
	//审计报告文件路径
	private String reportPath;
	//审计报告文件ID
	private String reportId;
	//报告文件类型
	private String fileType;
	//区块链ID
	private String fileHash;
	//创建时间
	private Date createTime;
	//创建用户
	private String createUser;
	//审计报告ID
	private String certSignId;
	//删除标识
	private Integer deleteFlag;
	//区块ID
	private String transactionId;
	//审计报告大小
	private Long reportSize;
	//最后审核时间
	private Date lastCheckTime;
	public Date getLastCheckTime() {
		return lastCheckTime;
	}
	public void setLastCheckTime(Date lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}
	public Long getreportSize() {
		return reportSize;
	}
	public void setreportSize(Long reportSize) {
		this.reportSize = reportSize;
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
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getFileName() {
		return reportName;
	}
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	public String getReportPath() {
		return reportPath;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getReportId() {
		return reportId;
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
