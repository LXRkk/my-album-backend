package com.lxrkk.myalbumbackend.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 空间等级枚举
 *
 * @author : LXRkk
 * @date : 2025/8/6 21:01
 */
@Getter
public enum SpaceLevelEnum {
    COMMON("普通版", 0, 100L * 1024 * 1024, 100),
    PROFESSIONAL("专业版", 1, 1000L * 1024 * 1024, 1000),
    FLAGSHIP("旗舰版", 2, 10000L * 1024 * 1024, 10000);

    private final String text;

    private final int value;

    private final long maxSize;

    private final long maxCount;

    /**
     * @param text     等级描述
     * @param value    值
     * @param maxSize  空间最大额度
     * @param maxCount 空间允许存储图片的最大数量
     */
    SpaceLevelEnum(String text, int value, long maxSize, long maxCount) {
        this.text = text;
        this.value = value;
        this.maxSize = maxSize;
        this.maxCount = maxCount;
    }

    /**
     * 根据 value 获取枚举
     */
    public static SpaceLevelEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()) {
            if (spaceLevelEnum.value == value) {
                return spaceLevelEnum;
            }
        }
        return null;
    }
}
