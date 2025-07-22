package com.lxrkk.myalbumbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lxrkk.myalbumbackend.constant.UserConstant;
import com.lxrkk.myalbumbackend.exception.BusinessException;
import com.lxrkk.myalbumbackend.exception.ErrorCode;
import com.lxrkk.myalbumbackend.exception.ThrowUtils;
import com.lxrkk.myalbumbackend.mapper.UserMapper;
import com.lxrkk.myalbumbackend.model.dto.user.UserQueryRequest;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.enums.UserRoleEnum;
import com.lxrkk.myalbumbackend.model.vo.LoginUserVO;
import com.lxrkk.myalbumbackend.model.vo.UserVO;
import com.lxrkk.myalbumbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chris
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-07-20 15:44:57
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    // 默认用户名
    final String DEFAULT_USERNAME = "新用户";

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 注册用户id
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验参数
        if (StrUtil.hasBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 2.账号是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 3.密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 4.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(DEFAULT_USERNAME);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "注册失败，系统错误");
        }

        return user.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验参数
        ThrowUtils.throwIf(CharSequenceUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号过短");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码过短");
        // 2.密码加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3.查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 检查用户是否存在
        if (user == null) {
            log.warn("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 4.记录用户登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
        // 5.返回脱敏后的用户信息
        return getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 当前登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询完整的用户信息，确保数据一致性
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return currentUser;
    }

    /**
     * 退出登录
     *
     * @param request 请求
     * @return 退出登录结果
     */
    @Override
    public boolean logout(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }

    /**
     * 用户信息脱敏（个人视角）
     *
     * @param user 未脱敏的用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 用户信息脱敏（他人视角）
     *
     * @param user 未脱敏的用户信息
     * @return 脱敏后的用户信息
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏后的用户列表
     *
     * @param userList 未脱敏的用户列表
     * @return 脱敏后的用户列表
     */
    @Override
    public List<UserVO> getUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream()
                .map(this::getUserVO)
                .collect(Collectors.toList());
    }

    /**
     * 构造用于分页查询的 QueryWrapper
     *
     * @param userQueryRequest 查询参数
     * @return QueryWrapper
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userRole = userQueryRequest.getUserRole();
        String userProfile = userQueryRequest.getUserProfile();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id)
                .eq(StrUtil.isNotBlank(userRole), "userRole", userRole)
                .like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount)
                .like(StrUtil.isNotBlank(userName), "userName", userName)
                .like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile)
                .orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 判断是否为管理员
     *
     * @param user 用户
     * @return 是否为管理员
     */
    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取加密密码
     *
     * @param password 密码
     * @return 加密密码
     */
    @Override
    public String getEncryptPassword(String password) {
        // 盐值，混淆密码
        final String SALT = "lxrkk";
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }

}




