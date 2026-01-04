package com.mdmanager.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.mdmanager.common.BusinessException;
import com.mdmanager.common.ResultCode;
import com.mdmanager.config.FileConfig;
import com.mdmanager.config.OssConfig;
import com.mdmanager.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 文件服务实现类
 *
 * <p>实现文件上传、保存、删除等操作</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    @Autowired
    private FileConfig fileConfig;

    /**
     * 允许的图片类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );

    /**
     * 上传图片到OSS
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @Override
    public String uploadImage(MultipartFile file) {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException(ResultCode.FILE_TYPE_ERROR);
        }

        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名：日期目录/UUID.扩展名
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String newFileName = ossConfig.getImageDir() + dateDir + "/" + IdUtil.simpleUUID() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            // 上传到OSS
            ossClient.putObject(ossConfig.getBucketName(), newFileName, inputStream);
            log.info("图片上传OSS成功: {}", newFileName);

            // 返回图片访问URL
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + newFileName;

        } catch (IOException e) {
            log.error("图片上传失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.UPLOAD_ERROR);
        }
    }

    /**
     * 保存MD文件到服务器
     *
     * @param fileName 文件名
     * @param content  文件内容
     */
    @Override
    public void saveMdFile(String fileName, String content) {
        try {
            // 确保上传目录存在
            Path uploadPath = Paths.get(fileConfig.getUploadPath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("创建上传目录: {}", uploadPath);
            }

            // 写入文件
            Path filePath = uploadPath.resolve(fileName);
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
            log.info("MD文件保存成功: {}", filePath);

        } catch (IOException e) {
            log.error("MD文件保存失败: {}", e.getMessage());
            throw new BusinessException("MD文件保存失败: " + e.getMessage());
        }
    }

    /**
     * 删除MD文件
     *
     * @param fileName 文件名
     */
    @Override
    public void deleteMdFile(String fileName) {
        try {
            Path filePath = Paths.get(fileConfig.getUploadPath(), fileName);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("MD文件删除成功: {}", filePath);
            } else {
                log.warn("MD文件不存在，无需删除: {}", filePath);
            }
        } catch (IOException e) {
            log.error("MD文件删除失败: {}", e.getMessage());
            throw new BusinessException("MD文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 读取MD文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    @Override
    public String readMdFile(String fileName) {
        try {
            Path filePath = Paths.get(fileConfig.getUploadPath(), fileName);
            if (!Files.exists(filePath)) {
                throw new BusinessException("文件不存在: " + fileName);
            }
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("MD文件读取失败: {}", e.getMessage());
            throw new BusinessException("MD文件读取失败: " + e.getMessage());
        }
    }
}
