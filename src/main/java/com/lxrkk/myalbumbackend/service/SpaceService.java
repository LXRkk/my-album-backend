package com.lxrkk.myalbumbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lxrkk.myalbumbackend.model.dto.space.SpaceAddRequest;
import com.lxrkk.myalbumbackend.model.dto.space.SpaceQueryRequest;
import com.lxrkk.myalbumbackend.model.entity.Picture;
import com.lxrkk.myalbumbackend.model.entity.Space;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.vo.PictureVO;
import com.lxrkk.myalbumbackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Chris
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-08-06 20:31:37
 */
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     * @param addRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest addRequest, User loginUser);

    /**
     * 将查询请求转为 QueryWrapper 对象
     *
     * @param spaceQueryRequest 查询请求
     * @return QueryWrapper 对象
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取空间信息
     *
     * @param space   空间
     * @param request 请求
     * @return 脱敏空间信息
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 分页获取图片封装
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 空间数据校验
     *
     * @param space 空间
     * @param add   是否创建
     */
    void validSpace(Space space, boolean add);


    /**
     * 根据空间等级填充空间信息
     *
     * @param space 空间
     */
    void fillSpaceBySpaceLevel(Space space);
}
