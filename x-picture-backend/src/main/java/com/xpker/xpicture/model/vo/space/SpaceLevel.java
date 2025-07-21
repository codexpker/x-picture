package com.xpker.xpicture.model.vo.space;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpaceLevel {
    /**
     * 空间级别信息
     */
    private String text;
    /**
     * 空间级别枚举值
     */
    private Integer value;
    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;
}
