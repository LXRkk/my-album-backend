package com.lxrkk.myalbumbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员添加用户请求
 *
 * @author : LXRkk
 * @date : 2025/7/21 10:32
 */
@Data
public class UserAddRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 个人简介
     */
    private String userProfile;

}
