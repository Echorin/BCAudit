package com.oschain.fastchaindb.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.oschain.fastchaindb.system.model.Console;
import com.oschain.fastchaindb.system.model.DownloadRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DownloadRecordMapper extends BaseMapper<DownloadRecord> {

    Integer getDownloadTotal();

    List<DownloadRecord> listFull(Page<DownloadRecord> page, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("account") String account);

    List<Console> sumPastDownloadRecord();
}
