package com.mdmanager.controller;

import com.mdmanager.common.Result;
import com.mdmanager.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 *
 * <p>处理图片上传到OSS的请求</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private FileService fileService;

    /**
     * 上传单张图片到OSS
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传图片: {}", file.getOriginalFilename());
        String url = fileService.uploadImage(file);

        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success("上传成功", result);
    }

    /**
     * 批量上传图片到OSS
     *
     * @param files 图片文件数组
     * @return 图片访问URL列表
     */
    @PostMapping("/images")
    public Result<Map<String, List<String>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        log.info("批量上传图片, 数量: {}", files.length);

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String url = fileService.uploadImage(file);
                urls.add(url);
            } catch (Exception e) {
                log.error("图片上传失败: {}", file.getOriginalFilename(), e);
                // 继续上传其他图片
            }
        }

        Map<String, List<String>> result = new HashMap<>();
        result.put("urls", urls);
        return Result.success("上传成功", result);
    }
}
