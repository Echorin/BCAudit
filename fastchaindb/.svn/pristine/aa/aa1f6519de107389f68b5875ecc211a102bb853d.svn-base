package com.oschain.fastchaindb.controls.service.impl;

import com.oschain.fastchaindb.common.datapage.QueryDelete;
import com.oschain.fastchaindb.common.datapage.QueryPage;
import com.oschain.fastchaindb.controls.dao.QueryDao;
import com.oschain.fastchaindb.controls.service.QueryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class QueryServiceImpl implements QueryService {
    @Resource
    private QueryDao queryDao;

    public QueryPage queryPage(QueryPage queryPage) {
        return queryDao.getQueryPage(queryPage);
    }

    public String getCheckValue(QueryPage queryPage) {

        List<Map<String,Object>> list=queryDao.queryList(queryPage);
        if(list==null)return "0";
        if(list.size()>0){
            return "1";
        }
        else {
            return "0";
        }

    }

    @Override
    public String delRecord(QueryDelete queryDelete) {
        return queryDao.queryDelete(queryDelete);
    }

}
