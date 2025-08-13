package com.lxrkk.myalbumbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lxrkk.myalbumbackend.model.dto.picture.*;
import com.lxrkk.myalbumbackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Chris
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-07-22 15:48:27
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource        文件源
     * @param pictureUploadRequest 上传图片参数
     * @param loginUser            登录用户
     * @return 脱敏后的图片信息
     */
    PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 颜色搜索图片
     *
     * @param spaceId
     * @param picColor
     * @param loginUser
     * @return
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 将查询请求转为 QueryWrapper 对象
     *
     * @param pictureQueryRequest 查询请求
     * @return QueryWrapper 对象
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取单个图片信息
     * @param picture  图片
     * @param request 请求
     * @return 脱敏图片信息
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 编辑图片
     * @param pictureEditRequest
     * @param loginUser
     */
    void editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 批量修改图片
     * @param pictureEditByBatchRequest
     * @param loginUser
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

    /**
     * 删除图片
     * @param pictureId 图片id
     * @param loginUser 登录用户
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 清理图片文件
     * @param oldPicture  图片
     */
    void clearPictureFile(Picture oldPicture);

    /**
     * 图片数据校验
     * @param picture  图片
     */
    void validPicture(Picture picture);

    /**
     * 图片权限校验
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 图片审核
     * @param pictureReviewRequest 图片审核请求
     * @param loginUser 管理员
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 填充审核参数
     * @param picture 图片
     * @param loginUser 当前登录用户
     */
    void fillReviewParams(Picture picture, User loginUser);

    /**
     * 批量抓取并上传图片
     * @param pictureUploadByBatchRequest 图片上传批量请求
     * @param loginUser 当前登录用户
     * @return 成功上传的图片数量
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);
}
