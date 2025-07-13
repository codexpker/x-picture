package com.xpker.xpicture.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum PictureReviewStatusEnum {
    REVIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);

    private final String text;
    private final Integer value;

    PictureReviewStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value值获取枚举
     */
    public static PictureReviewStatusEnum getEnumByValue(Integer value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (PictureReviewStatusEnum pictureReviewStatusEnum : PictureReviewStatusEnum.values()) {
            if(Objects.equals(pictureReviewStatusEnum.value, value)){
                return pictureReviewStatusEnum;
            }
        }
        return null;
    }
}
