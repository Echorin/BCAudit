package com.oschain.fastchaindb.system.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.oschain.fastchaindb.system.model.User;

public interface UserMapper extends BaseMapper<User> {

    User getByUsername(String username);

}
