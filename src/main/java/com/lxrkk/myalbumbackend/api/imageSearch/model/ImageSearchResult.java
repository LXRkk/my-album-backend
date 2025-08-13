package com.lxrkk.myalbumbackend.api.imageSearch.model;

import lombok.Data;

/**
 * 图片搜索结果，用于接收 API 的返回值
 *
 * @author : LXRkk
 * @date : 2025/8/13 17:06
 */
@Data
public class ImageSearchResult {

    /**
     * 缩略图地址
     */
    private String thumbUrl;

    /**
     * 来源地址
     */
    private String fromUrl;
}
