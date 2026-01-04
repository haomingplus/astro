package com.mdmanager.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 *
 * <p>定义文件操作相关的业务方法</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
public interface FileService {

    /**
     * 上传图片到OSS
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    String uploadImage(MultipartFile file);

    /**
     * 保存MD文件到服务器
     *
     * @param fileName 文件名
     * @param content  文件内容
     */
    void saveMdFile(String fileName, String content);

    /**
     * 删除MD文件
     *
     * @param fileName 文件名
     */
    void deleteMdFile(String fileName);

    /**
     * 读取MD文件内容
     *
     * @param fileName 文件名
     * @return 文件内容
     */
    String readMdFile(String fileName);
}
