package com.oschain.fastchaindb.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.oschain.fastchaindb.system.model.Console;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsoleMapper extends BaseMapper<Console> {

    List<ConsoleMapper> selectByUserIds(@Param("userIds") List<Integer> userIds);

}
