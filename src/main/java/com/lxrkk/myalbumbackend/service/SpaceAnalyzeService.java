package com.lxrkk.myalbumbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lxrkk.myalbumbackend.model.dto.space.analyze.*;
import com.lxrkk.myalbumbackend.model.entity.Space;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.vo.space.analyze.*;

import java.util.List;

/**
 * @author Chris
 * @createDate 2025-08-06 20:31:37
 */
public interface SpaceAnalyzeService extends IService<Space> {

    /**
     * 获取空间使用情况分析
     *
     * @param request
     * @param loginUser
     * @return
     */
    SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest request, User loginUser);

    /**
     * 获取空间图片分类分析
     *
     * @param request
     * @param loginUser
     * @return
     */
    List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest request, User loginUser);

    /**
     * 获取空间图片标签分析
     *
     * @param spaceTagAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, User loginUser);

    /**
     * 获取空间图片大小分析
     *
     * @param spaceSizeAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, User loginUser);

    /**
     * 用户上传行为分析
     * @param spaceUserAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, User loginUser);

    /**
     * 空间使用排序分析
     * @param spaceRankAnalyzeRequest
     * @param loginUser
     * @return
     */
    List<Space> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, User loginUser);
}