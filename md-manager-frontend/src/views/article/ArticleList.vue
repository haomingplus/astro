<template>
  <!--
    文章列表页面
    展示文章列表，支持搜索、筛选、分页
  -->
  <div class="page-container">
    <h2 class="page-title">文章管理</h2>

    <!-- 搜索表单 -->
    <div class="search-form">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="关键词">
          <el-input
            v-model="queryParams.keyword"
            placeholder="请输入文章标题"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="queryParams.tagId"
            placeholder="请选择标签"
            clearable
          >
            <el-option
              v-for="tag in tagList"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增文章
      </el-button>
    </div>

    <!-- 文章列表表格 -->
    <el-table
      v-loading="loading"
      :data="articleList"
      stripe
      border
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
      <el-table-column label="标签" width="200">
        <template #default="{ row }">
          <el-tag
            v-for="tag in row.tags"
            :key="tag.id"
            size="small"
            style="margin-right: 5px; margin-bottom: 5px;"
          >
            {{ tag.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="fileName" label="文件名" width="280" show-overflow-tooltip />
      <el-table-column label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">
            <el-icon><View /></el-icon> 查看
          </el-button>
          <el-button type="primary" link @click="handleEdit(row)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
/**
 * 文章列表页面组件
 *
 * 展示文章列表，提供搜索、筛选、分页、增删改查功能
 */
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, View, Edit, Delete } from '@element-plus/icons-vue'
import { articleApi } from '@/api/article'
import { tagApi } from '@/api/tag'
import dayjs from 'dayjs'

// 路由
const router = useRouter()

// 加载状态
const loading = ref(false)

// 文章列表
const articleList = ref([])

// 标签列表
const tagList = ref([])

// 总数
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  tagId: null,
  status: null
})

/**
 * 格式化日期
 */
const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD HH:mm:ss') : ''
}

/**
 * 获取文章列表
 */
const getArticleList = async () => {
  loading.value = true
  try {
    const res = await articleApi.getList(queryParams)
    articleList.value = res.data.list
    total.value = res.data.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 获取标签列表
 */
const getTagList = async () => {
  try {
    const res = await tagApi.getList()
    tagList.value = res.data
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  queryParams.pageNum = 1
  getArticleList()
}

/**
 * 重置搜索条件
 */
const handleReset = () => {
  queryParams.keyword = ''
  queryParams.tagId = null
  queryParams.status = null
  queryParams.pageNum = 1
  getArticleList()
}

/**
 * 页大小改变
 */
const handleSizeChange = (size) => {
  queryParams.pageSize = size
  getArticleList()
}

/**
 * 当前页改变
 */
const handleCurrentChange = (page) => {
  queryParams.pageNum = page
  getArticleList()
}

/**
 * 新增文章
 */
const handleAdd = () => {
  router.push('/article/add')
}

/**
 * 查看文章
 */
const handleView = (row) => {
  router.push(`/article/view/${row.id}`)
}

/**
 * 编辑文章
 */
const handleEdit = (row) => {
  router.push(`/article/edit/${row.id}`)
}

/**
 * 删除文章
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除文章"${row.title}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await articleApi.delete(row.id)
      ElMessage.success('删除成功')
      getArticleList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  getArticleList()
  getTagList()
})
</script>

<style lang="scss" scoped>
.action-bar {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
