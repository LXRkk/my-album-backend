package com.lxrkk.myalbumbackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求参数包装类
 *
 * @author : LXRkk
 * @date : 2025/7/20 16:02
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
