package com.lxrkk.myalbumbackend.controller;

import com.lxrkk.myalbumbackend.common.BaseResponse;
import com.lxrkk.myalbumbackend.common.ResultUtils;
import com.lxrkk.myalbumbackend.exception.ErrorCode;
import com.lxrkk.myalbumbackend.exception.ThrowUtils;
import com.lxrkk.myalbumbackend.model.dto.space.analyze.*;
import com.lxrkk.myalbumbackend.model.entity.Space;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.vo.space.analyze.*;
import com.lxrkk.myalbumbackend.service.SpaceAnalyzeService;
import com.lxrkk.myalbumbackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 空间分析
 *
 * @author : LXRkk
 * @date : 2025/7/22 13:07
 */
@Slf4j
@Api(tags = "空间分析接口")
@RestController
@RequestMapping("/space/analyze")
public class SpaceAnalyzeController {

    @Resource
    private UserService userService;

    @Resource
    private SpaceAnalyzeService spaceAnalyzeService;

    /**
     * 获取空间使用情况
     * @param spaceUsageAnalyzeRequest
     * @param request
     * @return
     */
    @ApiOperation("获取空间的使用情况")
    @PostMapping("/usage")
    public BaseResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(
            @RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUsageAnalyzeRequest==null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        SpaceUsageAnalyzeResponse spaceUsageAnalyze = spaceAnalyzeService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceUsageAnalyze);
    }

    /**
     * 获取空间图片分类分析
     * @param spaceCategoryAnalyzeRequest
     * @param request
     * @return
     */
    @ApiOperation("获取空间图片分类分析")
    @PostMapping("/category")
    public BaseResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(
            @RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceCategoryAnalyzeResponse> spaceCategoryAnalyze = spaceAnalyzeService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceCategoryAnalyze);
    }

    /**
     * 获取空间图片标签分析
     * @param spaceTagAnalyzeRequest
     * @param request
     * @return
     */
    @ApiOperation("获取空间图片标签分析")
    @PostMapping("/tag")
    public BaseResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(
            @RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceTagAnalyzeResponse> spaceTagAnalyzeResponseList = spaceAnalyzeService.getSpaceTagAnalyze(spaceTagAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceTagAnalyzeResponseList);
    }

    @ApiOperation("获取空间图片大小分析")
    @PostMapping("/size")
    public BaseResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(
            @RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceSizeAnalyzeResponse> spaceSizeAnalyzeResponseList = spaceAnalyzeService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceSizeAnalyzeResponseList);
    }

    /**
     * 用户上传行为分析
     * @param spaceUserAnalyzeRequest
     * @param request
     * @return
     */
    @ApiOperation("用户上传行为分析")
    @PostMapping("/user")
    public BaseResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(
            @RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, 
            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceUserAnalyzeResponse> spaceUserAnalyzeList = spaceAnalyzeService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceUserAnalyzeList);
    }

    @ApiOperation("空间使用排行分析")
    @PostMapping("/rank")
    public BaseResponse<List<Space>> getSpaceRankAnalyze(
            @RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest,
            HttpServletRequest request) {
        ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<Space> spaceRankAnalyzeList = spaceAnalyzeService.getSpaceRankAnalyze(spaceRankAnalyzeRequest, loginUser);
        return ResultUtils.success(spaceRankAnalyzeList);
    }
}
