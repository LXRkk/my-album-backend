package com.lxrkk.myalbumbackend.model.dto.user;

import com.lxrkk.myalbumbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询用户请求
 *
 * @author : LXRkk
 * @date : 2025/7/21 10:40
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 个人简介
     */
    private String userProfile;
}
