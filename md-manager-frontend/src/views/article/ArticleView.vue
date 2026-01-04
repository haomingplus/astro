<template>
  <!--
    文章查看页面
    展示文章详情和Markdown预览
  -->
  <div class="page-container">
    <div class="article-header">
      <el-button @click="handleBack">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
      <el-button type="primary" @click="handleEdit">
        <el-icon><Edit /></el-icon> 编辑
      </el-button>
    </div>

    <div v-loading="loading" class="article-content">
      <!-- 文章标题 -->
      <h1 class="article-title">{{ article.title }}</h1>

      <!-- 文章元信息 -->
      <div class="article-meta">
        <span class="meta-item">
          <el-icon><Clock /></el-icon>
          {{ formatDate(article.createTime) }}
        </span>
        <span class="meta-item">
          <el-icon><Document /></el-icon>
          {{ article.fileName }}
        </span>
        <span class="meta-item">
          <el-tag :type="article.status === 1 ? 'success' : 'info'" size="small">
            {{ article.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </span>
      </div>

      <!-- 标签 -->
      <div v-if="article.tags && article.tags.length" class="article-tags">
        <el-tag
          v-for="tag in article.tags"
          :key="tag.id"
          style="margin-right: 8px;"
        >
          {{ tag.name }}
        </el-tag>
      </div>

      <!-- Markdown 预览 -->
      <div class="markdown-preview">
        <MdPreview
          :model-value="article.content"
          :theme="'light'"
          :preview-theme="'github'"
          :code-theme="'github'"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 文章查看页面组件
 *
 * 展示文章详情，使用Markdown预览组件渲染内容
 */
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Edit, Clock, Document } from '@element-plus/icons-vue'
import { MdPreview } from 'md-editor-v3'
import 'md-editor-v3/lib/preview.css'
import { articleApi } from '@/api/article'
import dayjs from 'dayjs'

// 路由
const router = useRouter()
const route = useRoute()

// 加载状态
const loading = ref(false)

// 文章数据
const article = ref({
  title: '',
  content: '',
  tags: [],
  status: 0,
  fileName: '',
  createTime: ''
})

/**
 * 格式化日期
 */
const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

/**
 * 获取文章详情
 */
const getArticleDetail = async () => {
  const id = route.params.id
  if (!id) return

  loading.value = true
  try {
    const res = await articleApi.getDetail(id)
    article.value = res.data
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

/**
 * 返回列表
 */
const handleBack = () => {
  router.push('/article')
}

/**
 * 编辑文章
 */
const handleEdit = () => {
  router.push(`/article/edit/${route.params.id}`)
}

// 初始化
onMounted(() => {
  getArticleDetail()
})
</script>

<style lang="scss" scoped>
.article-header {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.article-content {
  background-color: #fff;
  padding: 30px;
  border-radius: 4px;
}

.article-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 15px;
  color: #909399;
  font-size: 14px;

  .meta-item {
    display: flex;
    align-items: center;
    gap: 5px;
  }
}

.article-tags {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.markdown-preview {
  :deep(.md-editor-preview-wrapper) {
    padding: 0;
  }
}
</style>
