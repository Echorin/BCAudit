package com.oschain.fastchaindb.system.service.impl;

import com.oschain.fastchaindb.system.dao.HomeMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.oschain.fastchaindb.system.service.HomeService;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService{
    @Autowired
    private HomeMapper homeMapper;

    @Override
    public Integer sumFileNum(){
        return homeMapper.sumFileNum();
    }

    @Override
    public Integer sumUserNum(){
        return homeMapper.sumUserNum();
    }
}
