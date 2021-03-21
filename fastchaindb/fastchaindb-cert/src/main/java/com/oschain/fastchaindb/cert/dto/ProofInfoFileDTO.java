package com.oschain.fastchaindb.cert.dto;

import java.io.Serializable;
import java.util.Date;

public class ProofInfoFileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    //文件名称
    private String fileName;

    //文件路径
    private String filePath;

    //文件ID
    private String fileId;

    //文件类型
    private String fileType;

    //文件标识
    private String fileTag;

    //区块链ID
    private String fileHash;

    //创建时间
    private Date createTime;

    //创建用户
    private String createUser;

    //文件ID
    private String certSignId;

    private String transactionId;

    private String blockHash;

    private Long fileSize;

    private Integer blockStatus;

    private Date lastCheckTime;

    private Long certFileId;

    public Long getCertFileId() {
        return certFileId;
    }

    public void setCertFileId(Long certFileId) {
        this.certFileId = certFileId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(Integer blockStatus) {
        this.blockStatus = blockStatus;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileTag() {
        return fileTag;
    }

    public void setFileTag(String fileTag) {
        this.fileTag = fileTag;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCertSignId() {
        return certSignId;
    }

    public void setCertSignId(String certSignId) {
        this.certSignId = certSignId;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }
}
