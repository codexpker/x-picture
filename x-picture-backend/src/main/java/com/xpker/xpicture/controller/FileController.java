package com.xpker.xpicture.controller;

import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import com.xpker.xpicture.annotation.AuthCheck;
import com.xpker.xpicture.common.BaseResponse;
import com.xpker.xpicture.common.ResultUtils;
import com.xpker.xpicture.constant.UserConstant;
import com.xpker.xpicture.exception.BusinessException;
import com.xpker.xpicture.exception.ErrorCode;
import com.xpker.xpicture.manager.CosManager;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;

    /**
     * 文件上传测试
     *
     * @param multipartFile
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/test/upload")
    public BaseResponse<String> upload(@RequestPart("file") MultipartFile multipartFile) {
        // 文件目录
        String fileName = multipartFile.getOriginalFilename();
        String filepath = String.format("test/%s", fileName);
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(filepath, null);
            // 将传递来的文件保存在临时文件中
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            return ResultUtils.success(fileName);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            if (file != null) {
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath:{}", filepath);
                }
            }
        }
    }

    /**
     * 文件下载测试
     *
     * @param filepath 文件路径
     * @param response 响应对象
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @GetMapping("/test/download")
    // 自己控制接口的返回
    public void testDownloadFile(String filepath, HttpServletResponse response) throws Exception {
        COSObjectInputStream inputStream = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            inputStream = cosObject.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            // 设置响应头
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filepath);
            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("testDownloadFile error, filepath:{}", filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            // 释放流
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }
}
