package com.oschain.fastchaindb.chainsql.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @author kevin
 * @date 2019-06-18 15:26:50
 */
@TableName("cert_block")
public class CertBlock implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId
	private Integer id;

	//
	private String blockBody;

	//
	private String blockHash;

	//创建时间
	private Date createTime;

	//创建用户
	private String createUser;

	//区块链ID
	private String transactionId;

	//状态0生成交易完成，1上链完成
	private Integer blockStatus;

	//
	private Date lastCheckTime;

	private Date blockTime;

	private Integer writeMode;

	private String blockKey1;
	private String blockKey2;
	private String blockKey3;
	private String blockKey4;
	private String blockKey5;


	public String getBlockKey1() {
		return blockKey1;
	}

	public void setBlockKey1(String blockKey1) {
		this.blockKey1 = blockKey1;
	}

	public String getBlockKey2() {
		return blockKey2;
	}

	public void setBlockKey2(String blockKey2) {
		this.blockKey2 = blockKey2;
	}

	public String getBlockKey3() {
		return blockKey3;
	}

	public void setBlockKey3(String blockKey3) {
		this.blockKey3 = blockKey3;
	}

	public String getBlockKey4() {
		return blockKey4;
	}

	public void setBlockKey4(String blockKey4) {
		this.blockKey4 = blockKey4;
	}

	public String getBlockKey5() {
		return blockKey5;
	}

	public void setBlockKey5(String blockKey5) {
		this.blockKey5 = blockKey5;
	}

	public Integer getWriteMode() {
		return writeMode;
	}

	public void setWriteMode(Integer writeMode) {
		this.writeMode = writeMode;
	}

	public Date getBlockTime() {
		return blockTime;
	}

	public void setBlockTime(Date blockTime) {
		this.blockTime = blockTime;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setBlockBody(String blockBody) {
		this.blockBody = blockBody;
	}
	public String getBlockBody() {
		return blockBody;
	}
	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}
	public String getBlockHash() {
		return blockHash;
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
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setBlockStatus(Integer blockStatus) {
		this.blockStatus = blockStatus;
	}
	public Integer getBlockStatus() {
		return blockStatus;
	}
	public void setLastCheckTime(Date lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}
	public Date getLastCheckTime() {
		return lastCheckTime;
	}
}
