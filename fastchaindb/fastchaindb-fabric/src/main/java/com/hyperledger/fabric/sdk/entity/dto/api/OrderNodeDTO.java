package com.hyperledger.fabric.sdk.entity.dto.api;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by Kevin on 2018-09-03 19:04
 *
 * order节点DTO
 */
public class OrderNodeDTO extends NodeDTO implements Serializable {
    private String ip;

    public OrderNodeDTO(String nodeName, String grpcUrl) {
        super(nodeName, grpcUrl);
    }

    public OrderNodeDTO(String nodeName, String grpcUrl, String ip) {
        super(nodeName, grpcUrl);
        this.ip=ip;
    }

    public OrderNodeDTO(String nodeName, String grpcUrl, Properties properties) {
        super(nodeName, grpcUrl, properties);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}