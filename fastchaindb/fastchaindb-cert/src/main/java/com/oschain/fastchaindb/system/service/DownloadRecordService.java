package com.oschain.fastchaindb.system.service;

import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.system.model.Console;
import com.oschain.fastchaindb.system.model.DownloadRecord;

import java.util.List;

public interface DownloadRecordService extends IService<DownloadRecord> {

    List<Console> sumPastDownloadRecord();

    Integer getDownloadTotal();

    boolean add(DownloadRecord downloadRecord);

    PageResult<DownloadRecord> list(int pageNum, int pageSize, String startDate, String endDate, String account);

}
