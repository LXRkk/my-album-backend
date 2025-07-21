package com.lxrkk.myalbumbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lxrkk.myalbumbackend.common.BaseResponse;
import com.lxrkk.myalbumbackend.model.dto.user.UserAddRequest;
import com.lxrkk.myalbumbackend.model.dto.user.UserQueryRequest;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.vo.LoginUserVO;
import com.lxrkk.myalbumbackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Chris
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-07-20 15:44:57
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 注册用户id
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 退出登录
     *
     * @param request 请求
     * @return 退出登录结果
     */
    boolean logout(HttpServletRequest request);

    /**
     * 用户信息脱敏 （个人视角）
     *
     * @param user 未脱敏的用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 用户信息脱敏（他人视角）
     *
     * @param user 未脱敏的用户信息
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 获取脱敏后的用户列表
     *
     * @param userList 未脱敏的用户列表
     * @return 脱敏后的用户列表
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 构造用于分页查询的 QueryWrapper
     *
     * @param userQueryRequest 查询参数
     * @return QueryWrapper
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取加密密码
     *
     * @param password 密码
     * @return 加密密码
     */
    String getEncryptPassword(String password);


}
