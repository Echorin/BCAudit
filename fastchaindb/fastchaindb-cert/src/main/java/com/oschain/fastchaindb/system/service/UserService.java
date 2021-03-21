package com.oschain.fastchaindb.system.service;

import com.oschain.fastchaindb.common.PageResult;
import com.oschain.fastchaindb.common.exception.BusinessException;
import com.oschain.fastchaindb.common.exception.ParameterException;
import com.oschain.fastchaindb.system.model.User;

public interface UserService {

    User getByUsername(String username);

    PageResult<User> list(int pageNum, int pageSize, boolean showDelete, String searchKey, String searchValue);

    User getById(Integer userId);

    boolean add(User user) throws BusinessException;

    boolean update(User user);

    boolean updateState(Integer userId, int state) throws ParameterException;

    boolean updatePsw(Integer userId, String username, String newPsw);

    boolean delete(Integer userId);


}
