package com.lxrkk.myalbumbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求参数包装类
 *
 * @author : LXRkk
 * @date : 2025/7/20 16:02
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;
}
