package com.oschain.fastchaindb.controls.service;

import com.oschain.fastchaindb.common.datapage.QueryDelete;
import com.oschain.fastchaindb.common.datapage.QueryPage;

public interface QueryService {
    QueryPage queryPage(QueryPage queryPage);
    String getCheckValue(QueryPage queryPage);
    String delRecord(QueryDelete queryDelete);
}
