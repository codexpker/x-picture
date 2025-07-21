package com.xpker.xpicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpker.xpicture.model.dto.space.SpaceAddRequest;
import com.xpker.xpicture.model.dto.space.SpaceQueryRequest;
import com.xpker.xpicture.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xpker.xpicture.model.entity.User;
import com.xpker.xpicture.model.vo.space.SpaceVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author 93729
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-07-20 11:55:06
 */
public interface SpaceService extends IService<Space> {
    /**
     * 根据查询请求拼接条件构造器
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 获取空间包装类(单条)
     *
     * @param space   空间
     * @param request 请求
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 获取空间分装（分页）
     *
     * @param spacePage
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage);

    /**
     * 校验空间
     *
     * @param space
     * @param add
     */
    void validSpace(Space space, boolean add);

    /**
     * 自动填充限额数据
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
