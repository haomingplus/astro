package com.mdmanager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mdmanager.common.BusinessException;
import com.mdmanager.common.PageResult;
import com.mdmanager.common.ResultCode;
import com.mdmanager.config.FileConfig;
import com.mdmanager.dto.ArticleDTO;
import com.mdmanager.dto.ArticleQueryDTO;
import com.mdmanager.entity.Article;
import com.mdmanager.entity.ArticleTag;
import com.mdmanager.mapper.ArticleMapper;
import com.mdmanager.mapper.ArticleTagMapper;
import com.mdmanager.service.ArticleService;
import com.mdmanager.service.FileService;
import com.mdmanager.utils.UserContext;
import com.mdmanager.vo.ArticleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * <p>实现文章相关的业务逻辑</p>
 *
 * @author mdmanager
 * @since 1.0.0
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileConfig fileConfig;

    /**
     * 分页查询文章列表
     *
     * @param queryDTO 查询参数
     * @return 文章分页列表
     */
    @Override
    public PageResult<ArticleVO> getArticlePage(ArticleQueryDTO queryDTO) {
        // 创建分页对象
        Page<ArticleVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 执行分页查询
        baseMapper.selectArticlePageWithTags(
                page,
                queryDTO.getKeyword(),
                queryDTO.getTagId(),
                queryDTO.getStatus()
        );

        // 构建分页结果
        return PageResult.of(
                page.getRecords(),
                page.getTotal(),
                queryDTO.getPageNum(),
                queryDTO.getPageSize()
        );
    }

    /**
     * 获取文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    @Override
    public ArticleVO getArticleDetail(Long id) {
        ArticleVO articleVO = baseMapper.selectArticleDetailById(id);
        if (articleVO == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 设置标签ID列表
        if (articleVO.getTags() != null) {
            List<Long> tagIds = articleVO.getTags().stream()
                    .map(tag -> tag.getId())
                    .collect(Collectors.toList());
            articleVO.setTagIds(tagIds);
        }

        return articleVO;
    }

    /**
     * 新增文章
     *
     * @param articleDTO 文章数据
     * @return 新增结果（包含文章ID和文件名）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addArticle(ArticleDTO articleDTO) {
        // 生成UUID文件名
        String fileName = UUID.randomUUID().toString() + ".md";

        // 创建文章实体
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        article.setFileName(fileName);
        article.setFilePath(fileConfig.getUploadPath() + "/" + fileName);
        article.setCreateUserId(UserContext.getUserId());

        // 如果摘要为空，自动从内容截取
        if (StrUtil.isBlank(article.getSummary())) {
            article.setSummary(generateSummary(articleDTO.getContent()));
        }

        // 如果状态为发布，设置发布时间
        if (articleDTO.getStatus() != null && articleDTO.getStatus() == 1) {
            article.setPublishTime(LocalDateTime.now());
        }

        // 保存文章到数据库
        this.save(article);
        log.info("文章保存到数据库成功, ID: {}", article.getId());

        // 保存文章标签关联
        saveArticleTags(article.getId(), articleDTO.getTagIds());

        // 生成MD文件并保存到服务器
        try {
            String mdContent = generateMdFileContent(article, articleDTO.getTagIds());
            fileService.saveMdFile(fileName, mdContent);
            log.info("MD文件生成成功: {}", fileName);
        } catch (Exception e) {
            log.error("MD文件生成失败: {}", e.getMessage());
            throw new BusinessException("MD文件生成失败: " + e.getMessage());
        }

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", article.getId());
        result.put("fileName", fileName);
        return result;
    }

    /**
     * 编辑文章
     *
     * @param id         文章ID
     * @param articleDTO 文章数据
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(Long id, ArticleDTO articleDTO) {
        // 查询文章是否存在
        Article existArticle = this.getById(id);
        if (existArticle == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 更新文章实体
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        article.setId(id);

        // 如果摘要为空，自动从内容截取
        if (StrUtil.isBlank(article.getSummary())) {
            article.setSummary(generateSummary(articleDTO.getContent()));
        }

        // 如果状态变为发布且之前未发布，设置发布时间
        if (articleDTO.getStatus() != null && articleDTO.getStatus() == 1
                && (existArticle.getPublishTime() == null)) {
            article.setPublishTime(LocalDateTime.now());
        }

        // 更新数据库
        this.updateById(article);
        log.info("文章更新成功, ID: {}", id);

        // 更新文章标签关联
        articleTagMapper.deleteByArticleId(id);
        saveArticleTags(id, articleDTO.getTagIds());

        // 更新MD文件
        try {
            String mdContent = generateMdFileContent(existArticle, articleDTO.getTagIds());
            // 使用新内容更新
            Article updatedArticle = this.getById(id);
            mdContent = generateMdFileContent(updatedArticle, articleDTO.getTagIds());
            fileService.saveMdFile(existArticle.getFileName(), mdContent);
            log.info("MD文件更新成功: {}", existArticle.getFileName());
        } catch (Exception e) {
            log.error("MD文件更新失败: {}", e.getMessage());
            throw new BusinessException("MD文件更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(Long id) {
        // 查询文章是否存在
        Article article = this.getById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.ARTICLE_NOT_FOUND);
        }

        // 逻辑删除文章
        this.removeById(id);

        // 删除文章标签关联
        articleTagMapper.deleteByArticleId(id);

        // 删除MD文件
        try {
            fileService.deleteMdFile(article.getFileName());
            log.info("MD文件删除成功: {}", article.getFileName());
        } catch (Exception e) {
            log.warn("MD文件删除失败: {}", e.getMessage());
            // 文件删除失败不影响数据库删除
        }

        log.info("文章删除成功, ID: {}", id);
    }

    /**
     * 批量上传MD文件
     *
     * @param files MD文件数组
     * @return 上传结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchUploadMdFiles(MultipartFile[] files) {
        int successCount = 0;
        int failCount = 0;
        List<String> failFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // 检查文件类型
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null ||
                        (!originalFilename.endsWith(".md") && !originalFilename.endsWith(".markdown"))) {
                    failFiles.add(originalFilename + " (不支持的文件类型)");
                    failCount++;
                    continue;
                }

                // 读取文件内容
                String content = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                // 解析MD文件内容
                Map<String, Object> parsedContent = parseMdFile(content, originalFilename);

                // 生成新的UUID文件名
                String fileName = UUID.randomUUID().toString() + ".md";

                // 创建文章实体
                Article article = new Article();
                article.setTitle((String) parsedContent.get("title"));
                article.setContent((String) parsedContent.get("content"));
                article.setSummary(generateSummary((String) parsedContent.get("content")));
                article.setFileName(fileName);
                article.setFilePath(fileConfig.getUploadPath() + "/" + fileName);
                article.setStatus(0);  // 默认为草稿
                article.setCreateUserId(UserContext.getUserId());

                // 保存到数据库
                this.save(article);

                // 保存MD文件到服务器
                fileService.saveMdFile(fileName, content);

                successCount++;
                log.info("批量上传成功: {} -> {}", originalFilename, fileName);

            } catch (Exception e) {
                failFiles.add(file.getOriginalFilename() + " (" + e.getMessage() + ")");
                failCount++;
                log.error("批量上传失败: {}", file.getOriginalFilename(), e);
            }
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failFiles", failFiles);
        return result;
    }

    /**
     * 保存文章标签关联
     *
     * @param articleId 文章ID
     * @param tagIds    标签ID列表
     */
    private void saveArticleTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        for (Long tagId : tagIds) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tagId);
            articleTagMapper.insert(articleTag);
        }
    }

    /**
     * 生成文章摘要
     *
     * @param content 文章内容
     * @return 摘要（最多200字符）
     */
    private String generateSummary(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }
        // 移除Markdown语法标记
        String text = content
                .replaceAll("#+ ", "")           // 标题
                .replaceAll("\\*+", "")          // 加粗/斜体
                .replaceAll("\\[.*?\\]\\(.*?\\)", "")  // 链接
                .replaceAll("`+", "")            // 代码
                .replaceAll("\\n+", " ")         // 换行
                .trim();

        // 截取前200个字符
        if (text.length() > 200) {
            return text.substring(0, 200) + "...";
        }
        return text;
    }

    /**
     * 生成MD文件内容（包含frontmatter）
     *
     * @param article 文章实体
     * @param tagIds  标签ID列表
     * @return MD文件内容
     */
    private String generateMdFileContent(Article article, List<Long> tagIds) {
        StringBuilder sb = new StringBuilder();

        // 添加frontmatter（Astro博客需要的元数据）
        sb.append("---\n");
        sb.append("title: \"").append(article.getTitle()).append("\"\n");
        sb.append("pubDatetime: ").append(article.getCreateTime() != null ?
                article.getCreateTime().toString() : LocalDateTime.now().toString()).append("\n");

        if (article.getUpdateTime() != null) {
            sb.append("modDatetime: ").append(article.getUpdateTime().toString()).append("\n");
        }

        if (StrUtil.isNotBlank(article.getAuthor())) {
            sb.append("author: \"").append(article.getAuthor()).append("\"\n");
        }

        // 添加标签
        if (tagIds != null && !tagIds.isEmpty()) {
            sb.append("tags:\n");
            for (Long tagId : tagIds) {
                // 这里应该查询标签名，暂时使用ID
                sb.append("  - tag").append(tagId).append("\n");
            }
        }

        if (StrUtil.isNotBlank(article.getSummary())) {
            sb.append("description: \"").append(article.getSummary().replace("\"", "'")).append("\"\n");
        }

        sb.append("draft: ").append(article.getStatus() == 0 ? "true" : "false").append("\n");
        sb.append("---\n\n");

        // 添加文章内容
        sb.append(article.getContent());

        return sb.toString();
    }

    /**
     * 解析MD文件内容
     *
     * @param content  文件内容
     * @param filename 文件名
     * @return 解析结果
     */
    private Map<String, Object> parseMdFile(String content, String filename) {
        Map<String, Object> result = new HashMap<>();
        String title = filename.replace(".md", "").replace(".markdown", "");
        String body = content;

        // 尝试解析frontmatter
        if (content.startsWith("---")) {
            int endIndex = content.indexOf("---", 3);
            if (endIndex > 0) {
                String frontmatter = content.substring(3, endIndex).trim();
                body = content.substring(endIndex + 3).trim();

                // 解析frontmatter中的title
                for (String line : frontmatter.split("\n")) {
                    if (line.startsWith("title:")) {
                        title = line.substring(6).trim()
                                .replace("\"", "")
                                .replace("'", "");
                        break;
                    }
                }
            }
        }

        result.put("title", title);
        result.put("content", body);
        return result;
    }
}
