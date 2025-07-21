package com.lxrkk.myalbumbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员修改用户信息
 *
 * @author : LXRkk
 * @date : 2025/7/21 10:38
 */
@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户 id
     */
    private Long id;

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
