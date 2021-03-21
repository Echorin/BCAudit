package com.oschain.fastchaindb.fabric.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.oschain.fastchaindb.common.persistence.DataEntity;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * @author kevin
 * @date 2019-10-15 17:52:53
 */
@TableName("fabric_monitor")
public class FabricMonitor extends DataEntity<FabricMonitor> {

	private static final long serialVersionUID = 1L;
	
	@TableId
    private Integer id;
				
    //通道名称
    private String channelName;
						
    //组织名称
    private String orgName;
						
    //智能合约名称
    private String chainCodeName;
						
    //节点名称
    private String peerName;
						
    //排序节点
    private String orderName;
															



	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		
		return id;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getChannelName() {
		
		return channelName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getOrgName() {
		
		return orgName;
	}
	public void setChainCodeName(String chainCodeName) {
		this.chainCodeName = chainCodeName;
	}
	public String getChainCodeName() {
		
		return chainCodeName;
	}
	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}
	public String getPeerName() {
		
		return peerName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getOrderName() {
		
		return orderName;
	}
}
