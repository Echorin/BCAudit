package com.oschain.fastchaindb.system.service;

import com.baomidou.mybatisplus.service.IService;
import com.oschain.fastchaindb.cert.model.CertInfo;
import com.oschain.fastchaindb.system.model.Role;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<Role> getByUserId(Integer userId);

    List<Role> list(boolean showDelete);

    Role getById(Integer roleId);

    boolean add(Role role);

    boolean update(Role role);

    boolean updateState(Integer roleId, int isDelete);  // 逻辑删除

    boolean delete(Integer roleId);  // 物理删除

}
