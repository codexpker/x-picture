package com.xpker.xpicture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xpker.xpicture.constant.SortConstant;
import com.xpker.xpicture.constant.UserConstant;
import com.xpker.xpicture.exception.BusinessException;
import com.xpker.xpicture.exception.ErrorCode;
import com.xpker.xpicture.exception.ThrowUtils;
import com.xpker.xpicture.manager.CosManager;
import com.xpker.xpicture.manager.FileManager;
import com.xpker.xpicture.manager.upload.FilePictureUpload;
import com.xpker.xpicture.manager.upload.PictureUploadTemplate;
import com.xpker.xpicture.manager.upload.UrlPictureUpload;
import com.xpker.xpicture.model.dto.file.UploadPictureResult;
import com.xpker.xpicture.model.dto.picture.PictureQueryRequest;
import com.xpker.xpicture.model.dto.picture.PictureReviewRequest;
import com.xpker.xpicture.model.dto.picture.PictureUploadByBatchRequest;
import com.xpker.xpicture.model.dto.picture.PictureUploadRequest;
import com.xpker.xpicture.model.entity.Picture;
import com.xpker.xpicture.model.entity.User;
import com.xpker.xpicture.model.enums.PictureReviewStatusEnum;
import com.xpker.xpicture.model.vo.picture.PictureVO;
import com.xpker.xpicture.model.vo.user.UserVO;
import com.xpker.xpicture.service.PictureService;
import com.xpker.xpicture.mapper.PictureMapper;
import com.xpker.xpicture.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static net.sf.jsqlparser.util.validation.metadata.MetadataContext.exists;

/**
 * @author 93729
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-07-10 11:23:01
 */
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
        implements PictureService {

    //    @Resource
//    private FileManager fileManager;
    @Resource
    private FilePictureUpload filePictureUpload;
    @Resource
    private UrlPictureUpload urlPictureUpload;
    @Resource
    private UserService userService;
    @Resource
    private CosManager cosManager;

    /**
     * 上传图片
     *
     * @param inputSource          文件源
     * @param pictureUploadRequest 文件上传请求
     * @param loginUser            当前用户
     * @return
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(inputSource == null, ErrorCode.PARAMS_ERROR, "图片为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_AUTH_ERROR);
        // 判断是新增图片还是修改图片
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新图片，需要校验图片是否存在
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
            // 仅本人和管理员可更新
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
                throw new BusinessException(ErrorCode.NOT_AUTH_ERROR);
            }
        }
        // 上传图片得到信息
        // 按照用户id划分目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        // 根据inputSource类型区分上传方法
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        // 将上传图片结果传入图片实体类
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
        String picName=  uploadPictureResult.getPicName();
        // 可以接收前端传送过来的图片名称(批量抓取时使用)
        if(pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
            picName = pictureUploadRequest.getPicName();
        }
        // 可以接收前端传递过来的分类和标签
        if(pictureUploadRequest.getTags() != null){
            String tags = JSONUtil.toJsonStr(pictureUploadRequest.getTags());
            if(StrUtil.isNotBlank(tags)){
                picture.setTags(tags);
            }
        }
        if(StrUtil.isNotBlank(pictureUploadRequest.getCategory())){
            picture.setCategory(JSONUtil.toJsonStr(pictureUploadRequest.getCategory()));
        }
        picture.setName(picName);
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 补充审核信息
        fillReviewParams(picture, loginUser);
        if (pictureId != null) {
            // 如果是更新，需要补充id和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        // 操作数据库
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
        // 返回图片封装类
        return PictureVO.objToVo(picture);
    }

    /**
     * 拼接条件查询构造器
     *re
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.PARAMS_ERROR, "查询参数为空");
        // 构建条件查询器
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        // 从对象中获取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        // 填充审核信息
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        // 从多字段搜索
        // 加and是为了保证or只对这一个片段生效
        // queryWrapper.and()拼接复杂查询逻辑，通常内涵or
        // and (name like "%xxx%" or introduction like "%xxx%")
        if (StrUtil.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText));
        }
        // 拼接条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.eq(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);

        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            /* and(tag like "%\"Java\""%" and like "%\"Python\"%" */
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(ObjUtil.isNotEmpty(sortField), SortConstant.SORT_ORDER_ASC.equals(sortOrder), sortField);
        return queryWrapper;
    }

    /**
     * 获取图片包装类
     *
     * @param picture 图片
     * @param request 请求
     * @return
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转为封装类
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUserVO(userVO);
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     *
     * @param picturePage
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 将对象 -> 包装类
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).toList();
        // 关联用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 填充信息
        pictureVOList.forEach(pictureVO -> {
            long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUserVO(userService.getUserVO(user));
        });
        // 将信息存入分页对象
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 编辑或更新前校验图片
     *
     * @param picture
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id不能为空
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 500, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 图片审核
     *
     * @param pictureReviewRequest 图片审核请求
     * @param loginUser            登录用户
     */
    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        Long id = pictureReviewRequest.getId();
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        if (id == null || reviewStatusEnum == null || reviewStatusEnum.equals(PictureReviewStatusEnum.REVIEWING)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.PARAMS_ERROR);
        if (oldPicture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请勿重复审核");
        }
        // 更新审核状态
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, updatePicture);
        // 补充信息
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

    /**
     * 填充审核信息
     *
     * @param picture
     * @param loginUser
     */
    @Override
    public void fillReviewParams(Picture picture, User loginUser) {
        if (userService.isAdmin(loginUser)) {
            //管理员自动过审
            picture.setReviewerId(loginUser.getId());
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewTime(new Date());
            picture.setReviewMessage("管理员自动过审核");
        } else {
            //一般用户,设置为待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }
    }

    /**
     * 批量抓取上传图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */

    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        String searchText = pictureUploadByBatchRequest.getSearchText();
        ThrowUtils.throwIf(searchText == null, ErrorCode.PARAMS_ERROR, "输入的关键词不能为空");
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if(StrUtil.isBlank(namePrefix)){
            //默认前缀名称为搜索词
            namePrefix = searchText;
        }
        // 格式化数量
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, ErrorCode.PARAMS_ERROR, "最多 30 条");
        // 要抓取的地址
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取页面失败");
        }
        Elements div = document.getElementsByClass("dgControl");
        // 原图存放在[class="iusc"]标签下的m属性的murl中
        Elements imgElementList = div.select(".iusc");
        int uploadCount = 0;
        for (Element imgElement : imgElementList) {
            //获取data-m属性中的JSON字符串
            String dataM = imgElement.attr("m");
            String fileUrl;
            try {
                JSONObject jsonObject = JSONUtil.parseObj(dataM);
                fileUrl = jsonObject.getStr("murl");
            } catch (Exception e) {
                log.info("解析图片数据失败", e);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "解析图片数据失败");
            }
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过：{}", fileUrl);
                continue;
            }
            // 处理图片上传地址，防止转义
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            String category = pictureUploadByBatchRequest.getCategory();
            List<String> tags = pictureUploadByBatchRequest.getTags();
            if(StrUtil.isNotBlank(category)){
                pictureUploadRequest.setCategory(category);
            }
            if(ObjectUtil.isNotEmpty(tags)){
                pictureUploadRequest.setTags(tags);
            }
            if(StrUtil.isNotBlank(namePrefix)){
                //设置图片名称，序号递增
                pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
            }

            // 上传图片
            try{
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info("图片上传成功：id = {}",pictureVO.getId());
                uploadCount++;
            }catch (Exception e){
                log.info("图片上传失败：{}",fileUrl, e);
                continue;
            }
            if(uploadCount >= count){
                //抓到目标数量，停止抓取
                break;
            }
        }
        return uploadCount;
    }

    @Async
    @Override
    public void clearPictureFile(Picture oldPicture) {
        if (oldPicture == null) {
            return;
        }
        // 判断该图片是否被多条记录引用
        String picUrl = oldPicture.getUrl();
        Long count = this.lambdaQuery().eq(Picture::getUrl, picUrl).count();
        // 有不止一条记录用到了该图片，不清理
        if(count > 1){
            return;
        }
        // 从url中取出相对路径
        try {
            String path = new URL(picUrl).getPath();
            // 调用方法删除图片
            cosManager.deleteObject(path);
            // 删除缩略图
            String thumbnailUrl = oldPicture.getThumbnailUrl();
            if(StrUtil.isNotBlank(thumbnailUrl)){
                cosManager.deleteObject(thumbnailUrl);
            }
        } catch (MalformedURLException e) {
            log.info("picture delete error",e);
        }
    }
}




