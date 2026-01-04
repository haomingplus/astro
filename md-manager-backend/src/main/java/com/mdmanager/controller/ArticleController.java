package com.mdmanager.controller;

import com.mdmanager.common.PageResult;
import com.mdmanager.common.Result;
import com.mdmanager.dto.ArticleDTO;
import com.mdmanager.dto.ArticleQueryDTO;
import com.mdmanager.service.ArticleService;
import com.mdmanager.vo.ArticleVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文章控制器
 *
 * <p>处理文章的增删改查、批量上传等请求</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 分页查询文章列表
     *
     * @param queryDTO 查询参数
     * @return 文章分页列表
     */
    @GetMapping("/list")
    public Result<PageResult<ArticleVO>> getArticleList(ArticleQueryDTO queryDTO) {
        log.info("查询文章列表, 参数: {}", queryDTO);
        PageResult<ArticleVO> pageResult = articleService.getArticlePage(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleDetail(@PathVariable Long id) {
        log.info("查询文章详情, ID: {}", id);
        ArticleVO articleVO = articleService.getArticleDetail(id);
        return Result.success(articleVO);
    }

    /**
     * 新增文章
     *
     * @param articleDTO 文章数据
     * @return 新增结果
     */
    @PostMapping
    public Result<Map<String, Object>> addArticle(@Valid @RequestBody ArticleDTO articleDTO) {
        log.info("新增文章: {}", articleDTO.getTitle());
        Map<String, Object> result = articleService.addArticle(articleDTO);
        return Result.success("新增成功", result);
    }

    /**
     * 编辑文章
     *
     * @param id         文章ID
     * @param articleDTO 文章数据
     * @return 编辑结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDTO articleDTO) {
        log.info("编辑文章, ID: {}, 标题: {}", id, articleDTO.getTitle());
        articleService.updateArticle(id, articleDTO);
        return Result.success("编辑成功", null);
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        log.info("删除文章, ID: {}", id);
        articleService.deleteArticle(id);
        return Result.success("删除成功", null);
    }

    /**
     * 批量上传MD文件
     *
     * @param files MD文件数组
     * @return 上传结果
     */
    @PostMapping("/batch-upload")
    public Result<Map<String, Object>> batchUpload(@RequestParam("files") MultipartFile[] files) {
        log.info("批量上传MD文件, 文件数量: {}", files.length);
        Map<String, Object> result = articleService.batchUploadMdFiles(files);
        return Result.success("上传完成", result);
    }
}
