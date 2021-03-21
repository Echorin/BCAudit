package com.oschain.fastchaindb.fabric.model;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.oschain.fastchaindb.common.persistence.DataEntity;


/**
 * 
 * @author kevin
 * @date 2019-10-15 17:52:53
 */
@TableName("fabric_monitor_order")
public class FabricMonitorOrder extends DataEntity<FabricMonitorOrder> {

	private static final long serialVersionUID = 1L;
	
	@TableId
    private Integer id;
				
    //通道名称ID
    private Integer fabricMonitorId;
						
    //排序节点
    private String orderName;
						
    //节点IP
    private String orderIp;
															



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
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getOrderName() {
		
		return orderName;
	}
	public void setOrderIp(String orderIp) {
		this.orderIp = orderIp;
	}
	public String getOrderIp() {
		
		return orderIp;
	}
}
