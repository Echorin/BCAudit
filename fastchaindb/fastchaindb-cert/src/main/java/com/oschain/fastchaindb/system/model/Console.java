package com.oschain.fastchaindb.system.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Date;


/**
 * 控制台统计
 */
//@TableName("sys_console")
public class Console {
    private Integer count; //数量

    private String month; //月份

    public Integer getCount() {
        return count;
    }

    public String getMonth() {
        return month;
    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//
//
//    public Integer getNum_visit() { return num_visit; }
//
//    public void setNum_visit(Integer num_visit) {
//        this.num_visit = num_visit;
//    }
//
//
//
//    public Integer getNum_download() { return num_download; }
//
//    public void setNum_download(Integer num_download) {
//        this.num_download = num_download;
//    }
//
//
//
//    public Integer blockchain_height() { return blockchain_height; }
//
//    public void blockchain_height(Integer blockchain_height) {
//        this.blockchain_height = blockchain_height;
//    }
//
//
//
//    public Integer capacity_used() { return capacity_used; }
//
//    public void capacity_used(Integer capacity_used) { this.capacity_used = capacity_used; }


}