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
@TableName("fabric_monitor_peer")
public class FabricMonitorPeer extends DataEntity<FabricMonitorPeer> {

	private static final long serialVersionUID = 1L;
	
	@TableId
    private Integer id;
				
    //通道名称ID
    private Integer fabricMonitorId;
						
    //智能合约名称
    private String chainCodeName;
						
    //节点名称
    private String peerName;
						
    //节点IP
    private String peerIp;
															



	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		
		return id;
	}
	public void setFabricMonitorId(Integer fabricMonitorId) {
		this.fabricMonitorId = fabricMonitorId;
	}
	public Integer getFabricMonitorId() {
		
		return fabricMonitorId;
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
	public void setPeerIp(String peerIp) {
		this.peerIp = peerIp;
	}
	public String getPeerIp() {
		
		return peerIp;
	}
}
