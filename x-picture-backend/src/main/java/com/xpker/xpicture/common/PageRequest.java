package com.xpker.xpicture.common;

import com.xpker.xpicture.constant.SortConstant;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用的分页请求类
 */
@Data
public class PageRequest{
    /**
     * 当前页号
     */
    private int current = 1;
    /**
     * 页面大小
     */
    private int pageSize = 10;
    /**
     * 搜索字段
     */
    private String sortField;
    /**
     * 排序方式
     */
    private String sortOrder = SortConstant.SORT_ORDER_DESC;
}
