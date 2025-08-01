package com.lxrkk.myalbumbackend.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.lxrkk.myalbumbackend.annotation.AuthCheck;
import com.lxrkk.myalbumbackend.common.BaseResponse;
import com.lxrkk.myalbumbackend.common.DeleteRequest;
import com.lxrkk.myalbumbackend.common.ResultUtils;
import com.lxrkk.myalbumbackend.constant.UserConstant;
import com.lxrkk.myalbumbackend.exception.BusinessException;
import com.lxrkk.myalbumbackend.exception.ErrorCode;
import com.lxrkk.myalbumbackend.exception.ThrowUtils;
import com.lxrkk.myalbumbackend.manager.CosManager;
import com.lxrkk.myalbumbackend.model.dto.picture.*;
import com.lxrkk.myalbumbackend.model.entity.Picture;
import com.lxrkk.myalbumbackend.model.entity.User;
import com.lxrkk.myalbumbackend.model.enums.PictureReviewStatusEnum;
import com.lxrkk.myalbumbackend.model.vo.PictureTagCategory;
import com.lxrkk.myalbumbackend.model.vo.PictureVO;
import com.lxrkk.myalbumbackend.service.PictureService;
import com.lxrkk.myalbumbackend.service.UserService;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 文件管理
 *
 * @author : LXRkk
 * @date : 2025/7/22 13:07
 */
@Slf4j
@Api(tags = "文件管理接口")
@RestController
@RequestMapping("/file")
public class FileController {

    // 本地缓存
    private final Cache<String, String> LOCAL_CACHE =
            Caffeine.newBuilder().initialCapacity(1024)
                    .maximumSize(10000L)
                    // 缓存 5 分钟移除
                    .expireAfterWrite(5L, TimeUnit.MINUTES)
                    .build();


    @Resource
    private CosManager cosManager;

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;
    
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 上传图片，可以重新上传
     */
    @ApiOperation("上传图片")
    @PostMapping("/upload")
    // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
                                                 PictureUploadRequest pictureUploadRequest,
                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 通过 URL 上传图片（可重新上传）
     */
    @ApiOperation("通过 URL 上传图片（可重新上传）")
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPictureByUrl(@RequestBody PictureUploadRequest pictureUploadRequest,
                                                      HttpServletRequest request) {
        String fileUrl = pictureUploadRequest.getFileUrl();
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 批量抓取图片并上传(管理员)
     */
    @ApiOperation("批量抓取图片并上传(管理员)")
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBatch(@RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest,
                                                      HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        Integer uploadCount = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
        return ResultUtils.success(uploadCount);
    }


    /**
     * 删除图片(仅本人或管理员)
     */
    @ApiOperation("删除图片(仅本人或管理员)")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
        // 仅本人或管理员可删除
        if (!userService.isAdmin(loginUser) && !picture.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 删除
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");
        return ResultUtils.success(true);
    }

    /**
     * 更新图片(管理员）
     */
    @ApiOperation("更新图片(管理员)")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        // dto 转 entity
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureUpdateRequest, picture);
        // 标签列表转为字符串
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        Long pictureId = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 补充审核参数
        User loginUser = userService.getLoginUser(request);
        pictureService.fillReviewParams(picture, loginUser);
        // 更新
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取图片(管理员)
     */
    @ApiOperation("根据 id 获取图片(管理员)")
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(picture);
    }

    /**
     * 根据 id 获取图片(普通用户)
     */
    @ApiOperation("根据 id 获取图片(普通用户)")
    @GetMapping("/get/vo")
    public BaseResponse<PictureVO> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * 分页获取图片(管理员)
     */
    @ApiOperation("分页获取图片(管理员)")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        Page<Picture> page = pictureService.page(new Page<>(current, pageSize),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(page);
    }

    /**
     * 分页获取图片列表(普通用户)
     */
    @ApiOperation("分页获取图片列表(普通用户)")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        // 普通用户默认只能查看审核通过的图片
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        Page<Picture> page = pictureService.page(new Page<>(current, pageSize),
                pictureService.getQueryWrapper(pictureQueryRequest));
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(page, request);
        return ResultUtils.success(pictureVOPage);
    }

    /**
     * 使用缓存分页获取图片列表(普通用户)
     */
    @ApiOperation("使用缓存分页获取图片列表(普通用户)")
    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> listPictureVOByPageWithCache(@RequestBody PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        // 普通用户默认只能查看审核通过的图片
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // 构建缓存 key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        // 从本地缓存中查询（Caffeine）
        String cacheKey = "listPictureVOByPage:" + hashKey;
        String cachedValue = LOCAL_CACHE.getIfPresent(cacheKey);
        if (cachedValue != null) {
            // 命中本地缓存，返回缓存数据
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }

        // 从 Redis 缓存中查询
        String redisKey = "my-album:listPictureVOByPage:" + hashKey;
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();
        cachedValue = valueOps.get(redisKey);
        if (cachedValue != null) {
            // 命中 Redis 缓存，存入本地缓存并返回
            LOCAL_CACHE.put(cacheKey, cachedValue);
            Page<PictureVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
            return ResultUtils.success(cachedPage);
        }

        // 缓存未命中，查询数据库
        Page<Picture> page = pictureService.page(new Page<>(current, pageSize),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 数据脱敏
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(page, request);
        // 更新缓存
        String cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        // 更新本地缓存
        LOCAL_CACHE.put(cacheKey, cacheValue);
        // 更新 Redis 缓存, 设置过期时间为 5 分钟
        // 5~10 分钟随机过期，防止缓存雪崩
        // int cacheExpireTime = RandomUtil.randomInt(0, 300) + 300;
        valueOps.set(redisKey, cacheValue, 5, TimeUnit.MINUTES);

        // 返回结果
        return ResultUtils.success(pictureVOPage);
    }

    /**
     * 编辑图片(普通用户)
     */
    @ApiOperation("编辑图片(普通用户)")
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null || pictureEditRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        // dto => entiry
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureEditRequest, picture);
        // 标签列表 => 字符串
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.validPicture(picture);
        // 判断是否存在
        long pictureId = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(pictureId);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可修改
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !oldPicture.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 补充审核参数
        pictureService.fillReviewParams(picture, loginUser);
        // 数据库更新
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败!");
        return ResultUtils.success(true);
    }

    /**
     * 常用标签和分类
     */
    @ApiOperation("常用标签和分类")
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("meme", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * 测试文件上传(仅管理员)
     *
     * @param multipartFile 文件
     * @return 文件访问路径
     */
    @ApiOperation("测试文件上传(仅管理员)")
    @PostMapping("/test/upload")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<String> testUploadFile(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录 /test/filename
        String filename = multipartFile.getOriginalFilename();
        String filepath = String.format("/test/%s", filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问的url
            return ResultUtils.success(filepath);
        } catch (IOException e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 测试文件下载(仅管理员)
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @ApiOperation("测试文件下载(仅管理员)")
    @PostMapping("/test/download")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void testDownloadFile(String filepath, HttpServletResponse response) throws IOException {
        COSObjectInputStream coi = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            coi = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] byteArray = IOUtils.toByteArray(coi);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "下载失败");
        } finally {
            if (coi != null) {
                coi.close();
            }
        }
    }

    /**
     * 图片审核
     */
    @ApiOperation("图片审核(管理员)")
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureReviewRequest == null || pictureReviewRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return ResultUtils.success(true);
    }
}
