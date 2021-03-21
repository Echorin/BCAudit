package com.oschain.fastchaindb.cert.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @author kevin
 * @date 2019-06-18 15:26:50
 */
@TableName("proof_block")
public class ProofBlock implements Serializable {

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
