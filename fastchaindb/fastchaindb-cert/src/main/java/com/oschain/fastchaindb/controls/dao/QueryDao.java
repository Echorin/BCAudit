package com.oschain.fastchaindb.controls.dao;

import com.oschain.fastchaindb.common.datapage.QueryPage;
import com.oschain.fastchaindb.common.driver.JdbcHelper;
import org.springframework.stereotype.Repository;

@Repository
public class QueryDao extends JdbcHelper {

    public QueryPage getQueryPage(QueryPage queryPage) {
        return this.queryPage(queryPage);
    }

}
