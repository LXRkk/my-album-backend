package com.lxrkk.myalbumbackend.api.imageSearch;

import com.lxrkk.myalbumbackend.api.imageSearch.model.ImageSearchResult;
import com.lxrkk.myalbumbackend.api.imageSearch.sub.GetImageFirstUrlApi;
import com.lxrkk.myalbumbackend.api.imageSearch.sub.GetImageListApi;
import com.lxrkk.myalbumbackend.api.imageSearch.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 搜索图片门面类
 *
 * @author : LXRkk
 * @date : 2025/8/13 21:07
 */
@Slf4j
public class ImageSearchApiFacade {

    /**
     * 搜索图片
     *
     * @param imageUrl
     * @return
     */
    public static List<ImageSearchResult> searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        return GetImageListApi.getImageList(imageFirstUrl);
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        List<ImageSearchResult> resultList = searchImage(imageUrl);
        System.out.println("结果列表" + resultList);
    }
}
