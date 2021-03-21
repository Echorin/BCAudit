package com.oschain.fastchaindb.system.service;

import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.system.model.LoginRecord;
import com.oschain.fastchaindb.system.model.Console;

import java.util.List;

public interface LoginRecordService {

    Integer getLoginTotal();

    boolean add(LoginRecord loginRecord);

    PageResult<LoginRecord> list(int pageNum, int pageSize, String startDate, String endDate, String account);

    List<Console> sumPastLoginRecord();
}
