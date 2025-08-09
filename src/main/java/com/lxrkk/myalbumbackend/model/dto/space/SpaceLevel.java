package com.lxrkk.myalbumbackend.model.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 给前端展示空间级别信息
 *
 * @author : LXRkk
 * @date : 2025/8/9 14:36
 */
@Data
@AllArgsConstructor
public class SpaceLevel {

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;
}
