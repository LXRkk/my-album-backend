package com.lxrkk.myalbumbackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxrkk.myalbumbackend.annotation.AuthCheck;
import com.lxrkk.myalbumbackend.common.BaseResponse;
import com.lxrkk.myalbumbackend.common.DeleteRequest;
import com.lxrkk.myalbumbackend.common.ResultUtils;
import com.lxrkk.myalbumbackend.constant.UserConstant;
import com.lxrkk.myalbumbackend.exception.BusinessException;
import com.lxrkk.myalbumbackend.exception.ErrorCode;
import com.lxrkk.myalbumbackend.exception.ThrowUtils;
import com.lxrkk.myalbumbackend.model.dto.space.*;
import com.lxrkk.myalbumbackend.model.entity.Space;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.enums.SpaceLevelEnum;
import com.lxrkk.myalbumbackend.model.vo.SpaceVO;
import com.lxrkk.myalbumbackend.service.SpaceService;
import com.lxrkk.myalbumbackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 空间管理
 *
 * @author : LXRkk
 * @date : 2025/7/22 13:07
 */
@Slf4j
@Api(tags = "空间管理接口")
@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @ApiOperation("创建空间")
    @PostMapping("/add")
    public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest addRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long newId = spaceService.addSpace(addRequest, loginUser);
        return ResultUtils.success(newId);
    }

    /**
     * 删除空间(仅本人或管理员)
     */
    @ApiOperation("删除空间(仅本人或管理员)")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        // 仅本人或管理员可删除
        spaceService.checkSpaceAuth(loginUser, space);
        // 删除
        boolean result = spaceService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");
        return ResultUtils.success(true);
    }

    /**
     * 更新空间(管理员）
     */
    @ApiOperation("更新空间(管理员)")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        // dto 转 entity
        Space space = new Space();
        BeanUtil.copyProperties(spaceUpdateRequest, space);
        // 空间数据填充
        spaceService.fillSpaceBySpaceLevel(space);
        // 数据校验
        spaceService.validSpace(space, false);
        // 判断是否存在
        Long spaceId = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(spaceId);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 更新
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取空间(管理员)
     */
    @ApiOperation("根据 id 获取空间(管理员)")
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(space);
    }

    /**
     * 根据 id 获取空间(普通用户)
     */
    @ApiOperation("根据 id 获取空间(普通用户)")
    @GetMapping("/get/vo")
    public BaseResponse<SpaceVO> getSpaceVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Space space = spaceService.getById(id);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(spaceService.getSpaceVO(space, request));
    }

    /**
     * 分页获取空间(管理员)
     */
    @ApiOperation("分页获取空间(管理员)")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = spaceQueryRequest.getCurrent();
        int pageSize = spaceQueryRequest.getPageSize();
        Page<Space> page = spaceService.page(new Page<>(current, pageSize),
                spaceService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(page);
    }

    /**
     * 分页获取空间列表(普通用户)
     */
    @ApiOperation("分页获取空间列表(普通用户)")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = spaceQueryRequest.getCurrent();
        int pageSize = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        Page<Space> page = spaceService.page(new Page<>(current, pageSize),
                spaceService.getQueryWrapper(spaceQueryRequest));
        Page<SpaceVO> spaceVOPage = spaceService.getSpaceVOPage(page, request);
        return ResultUtils.success(spaceVOPage);
    }


    /**
     * 编辑空间(普通用户)
     */
    @ApiOperation("编辑空间(普通用户)")
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(spaceEditRequest == null || spaceEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        // dto => entiry
        Space space = new Space();
        BeanUtil.copyProperties(spaceEditRequest, space);
        // 空间数据填充
        spaceService.fillSpaceBySpaceLevel(space);
        // 设置编辑时间
        space.setEditTime(new Date());
        // 数据校验
        spaceService.validSpace(space, false);
        // 判断是否存在
        long spaceId = spaceEditRequest.getId();
        Space oldSpace = spaceService.getById(spaceId);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可修改
        User loginUser = userService.getLoginUser(request);
        spaceService.checkSpaceAuth(loginUser, oldSpace);
        // 数据库更新
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败!");
        return ResultUtils.success(true);
    }

    /**
     * 获取所有空间等级，便于前端显示
     * @return
     */
    @ApiOperation("查询所有空间等级")
    @GetMapping("/list/level")
    public BaseResponse<List<SpaceLevel>> listSpaceLevel() {
        List<SpaceLevel> spaceLevelList = Arrays.stream(SpaceLevelEnum.values())
                .map(spaceLevelEnum -> new SpaceLevel(
                        spaceLevelEnum.getValue(),
                        spaceLevelEnum.getText(),
                        spaceLevelEnum.getMaxCount(),
                        spaceLevelEnum.getMaxSize()))
                .collect(Collectors.toList());
        return ResultUtils.success(spaceLevelList);
    }
}
