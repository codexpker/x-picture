package com.xpker.xpicture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpker.xpicture.model.dto.picture.PictureQueryRequest;
import com.xpker.xpicture.model.dto.picture.PictureUploadRequest;
import com.xpker.xpicture.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xpker.xpicture.model.entity.User;
import com.xpker.xpicture.model.vo.picture.PictureVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 93729
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-07-10 11:23:01
 */
public interface PictureService extends IService<Picture> {
    /**
     * 上传图片
     *
     * @param multipartFile        文件
     * @param pictureUploadRequest 文件上传请求
     * @param loginUser            当前用户
     * @return 图片封装类
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 根据查询请求拼接条件构造器
     *
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片包装类(单条)
     * @param picture   图片
     * @param request   请求
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片分装（分页）
     * @param picturePage
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage);

    /**
     * 校验图片
     * @param picture
     */
    void validPicture(Picture picture);
}
