package com.oschain.fastchaindb.common.shiro;

import com.oschain.fastchaindb.common.utils.StringUtil;
import com.oschain.fastchaindb.system.model.Authorities;
import com.oschain.fastchaindb.system.model.Role;
import com.oschain.fastchaindb.system.model.User;
import com.oschain.fastchaindb.system.service.AuthoritiesService;
import com.oschain.fastchaindb.system.service.RoleService;
import com.oschain.fastchaindb.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Shiro认证和授权
 * Created by kevin on 2018-02-22 上午 11:29
 */
public class ShiroRedisUserRealm extends AuthorizingRealm {
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    @Lazy
    private RoleService roleService;
    @Autowired
    @Lazy
    private AuthoritiesService authoritiesService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 验证用户身份
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();


        // 角色
//        List<Role> userRoles = roleService.getByUserId(user.getUserId());
//        Set<String> roles = new HashSet<>();
//        for (int i = 0; i < userRoles.size(); i++) {
//            roles.add(String.valueOf(userRoles.get(i).getRoleId()));
//        }

        Set<String> roles = (Set<String>)redisTemplate.opsForValue().get("role:"+user.getUserId());
        authorizationInfo.setRoles(roles);



        List<Authorities> authorities = (List<Authorities>)redisTemplate.opsForValue().get("authorities:"+user.getUserId());
        // 权限
//        List<Authorities> authorities = authoritiesService.listByUserId(user.getUserId());
        Set<String> permissions = new HashSet<>();
        for (int i = 0; i < authorities.size(); i++) {
            String authority = authorities.get(i).getAuthority();
            if (StringUtil.isNotBlank(authority)) {
                permissions.add(authority);
            }
        }

//        Set<String> permissions = (Set<String>)redisTemplate.opsForValue().get("permissions:"+user.getUserId());

        authorizationInfo.setStringPermissions(permissions);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String) authenticationToken.getPrincipal();
        User user = userService.getByUsername(username);
        if (user == null) {
            throw new UnknownAccountException(); // 账号不存在
        }
        if (user.getState() != 0) {
            throw new LockedAccountException();  // 账号被锁定
        }

        //写入redis
        List<Role> userRoles = roleService.getByUserId(user.getUserId());
        Set<String> roles = new HashSet<>();
        for (int i = 0; i < userRoles.size(); i++) {
            roles.add(String.valueOf(userRoles.get(i).getRoleId()));
        }
        redisTemplate.opsForValue().set("role:"+user.getUserId(),roles);

        // 权限
        List<Authorities> authorities = authoritiesService.listByUserId(user.getUserId());
        redisTemplate.opsForValue().set("authorities:"+user.getUserId(),authorities);


        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, user.getPassword(), ByteSource.Util.bytes(user.getUsername()), getName());
        return authenticationInfo;
    }
}
