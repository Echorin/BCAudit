package com.oschain.fastchaindb.system.dto;

import com.baomidou.mybatisplus.annotations.TableId;

import java.util.Date;

public class UserInfo {
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer userId;  // 主键id

    private String username;  // 账号

    private String password;  // 密码

    private String nickName;  // 昵称

    private String avatar;  // 头像

    private String sex;  // 性别

    private String phone;  // 手机号

    private String email;  // 邮箱

    private Integer emailVerified;  // 邮箱是否验证

    private Integer personId;  // 人员id，关联person表，如果是教学系统，则关联学生表和教师表

    private Integer personType;  // 人员类型，比如：0学生，1教师

    private Integer state;  // 用户状态，0正常，1锁定

    private Date createTime;  // 注册时间

    private Date updateTime;  // 修改时间

}
