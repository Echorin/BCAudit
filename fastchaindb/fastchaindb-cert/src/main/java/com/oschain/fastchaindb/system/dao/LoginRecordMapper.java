package com.oschain.fastchaindb.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.oschain.fastchaindb.system.model.Console;
import com.oschain.fastchaindb.system.model.LoginRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoginRecordMapper extends BaseMapper<LoginRecord> {

    Integer getLoginTotal();
    List<LoginRecord> listFull(Page<LoginRecord> page, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("account") String account);
    List<Console> sumPastLoginRecord();
}
