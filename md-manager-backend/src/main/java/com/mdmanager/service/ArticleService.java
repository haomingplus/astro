package com.mdmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mdmanager.common.PageResult;
import com.mdmanager.dto.ArticleDTO;
import com.mdmanager.dto.ArticleQueryDTO;
import com.mdmanager.entity.Article;
import com.mdmanager.vo.ArticleVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文章服务接口
 *
 * <p>定义文章相关的业务操作方法</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询文章列表
     *
     * @param queryDTO 查询参数
     * @return 文章分页列表
     */
    PageResult<ArticleVO> getArticlePage(ArticleQueryDTO queryDTO);

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    ArticleVO getArticleDetail(Long id);

    /**
     * 新增文章
     *
     * @param articleDTO 文章数据
     * @return 新增结果（包含文章ID和文件名）
     */
    Map<String, Object> addArticle(ArticleDTO articleDTO);

    /**
     * 编辑文章
     *
     * @param id         文章ID
     * @param articleDTO 文章数据
     */
    void updateArticle(Long id, ArticleDTO articleDTO);

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    void deleteArticle(Long id);

    /**
     * 批量上传MD文件
     *
     * @param files MD文件数组
     * @return 上传结果
     */
    Map<String, Object> batchUploadMdFiles(MultipartFile[] files);
}
