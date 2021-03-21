package com.hyperledger.fabric.sdk.entity.dto.api;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by Kevin on 2018-09-03 19:03
 *
 * peer节点DTO
 */
public class PeerNodeDTO extends NodeDTO implements Serializable {

    private String evenHubUrl;
    private String ip;

    public PeerNodeDTO(String nodeName, String grpcUrl) {
        super(nodeName, grpcUrl);
    }

    public PeerNodeDTO(String nodeName, String grpcUrl, String evenHubUrl) {
        super(nodeName, grpcUrl);
        this.evenHubUrl = evenHubUrl;
    }

    public PeerNodeDTO(String nodeName, String grpcUrl, String evenHubUrl, String ip) {
        super(nodeName, grpcUrl);
        this.evenHubUrl = evenHubUrl;
        this.ip=ip;
    }

    public String getEvenHubUrl() {
        return evenHubUrl;
    }

    public void setEvenHubUrl(String evenHubUrl) {
        this.evenHubUrl = evenHubUrl;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}