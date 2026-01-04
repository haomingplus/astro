<template>
  <!--
    文章编辑页面
    支持新增和编辑文章，使用Markdown编辑器
  -->
  <div class="page-container">
    <h2 class="page-title">{{ isEdit ? '编辑文章' : '新增文章' }}</h2>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="article-form"
    >
      <!-- 文章标题 -->
      <el-form-item label="文章标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入文章标题"
          maxlength="200"
          show-word-limit
        />
      </el-form-item>

      <!-- 标签选择 -->
      <el-form-item label="标签">
        <el-select
          v-model="formData.tagIds"
          multiple
          placeholder="请选择标签"
          style="width: 100%"
        >
          <el-option
            v-for="tag in tagList"
            :key="tag.id"
            :label="tag.name"
            :value="tag.id"
          />
        </el-select>
      </el-form-item>

      <!-- 文章状态 -->
      <el-form-item label="状态">
        <el-radio-group v-model="formData.status">
          <el-radio :label="0">草稿</el-radio>
          <el-radio :label="1">发布</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 文章摘要 -->
      <el-form-item label="文章摘要">
        <el-input
          v-model="formData.summary"
          type="textarea"
          :rows="3"
          placeholder="请输入文章摘要，不填则自动从内容截取"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <!-- Markdown 编辑器 -->
      <el-form-item label="文章内容" prop="content">
        <div class="editor-container">
          <MdEditor
            v-model="formData.content"
            :theme="'light'"
            :preview-theme="'github'"
            :code-theme="'github'"
            @onUploadImg="handleUploadImg"
          />
        </div>
      </el-form-item>

      <!-- 操作按钮 -->
      <el-form-item>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
        <el-button @click="handleBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
/**
 * 文章编辑页面组件
 *
 * 支持新增和编辑文章，集成Markdown编辑器
 */
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { articleApi } from '@/api/article'
import { tagApi } from '@/api/tag'
import { uploadApi } from '@/api/upload'

// 路由
const router = useRouter()
const route = useRoute()

// 判断是否是编辑模式
const isEdit = computed(() => !!route.params.id)

// 表单引用
const formRef = ref(null)

// 保存状态
const saving = ref(false)

// 标签列表
const tagList = ref([])

// 表单数据
const formData = reactive({
  title: '',
  content: '',
  summary: '',
  tagIds: [],
  status: 0,
  coverImage: '',
  author: ''
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' }
  ]
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
 * 获取文章详情（编辑模式）
 */
const getArticleDetail = async () => {
  const id = route.params.id
  if (!id) return

  try {
    const res = await articleApi.getDetail(id)
    const data = res.data
    formData.title = data.title
    formData.content = data.content
    formData.summary = data.summary || ''
    formData.tagIds = data.tagIds || []
    formData.status = data.status
    formData.coverImage = data.coverImage || ''
    formData.author = data.author || ''
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章详情失败')
  }
}

/**
 * 处理图片上传
 *
 * @param {File[]} files - 图片文件数组
 * @param {Function} callback - 回调函数，传入图片URL数组
 */
const handleUploadImg = async (files, callback) => {
  const urls = []
  for (const file of files) {
    try {
      const formData = new FormData()
      formData.append('file', file)
      const res = await uploadApi.uploadImage(formData)
      urls.push(res.data.url)
    } catch (error) {
      console.error('图片上传失败:', error)
      ElMessage.error(`图片 ${file.name} 上传失败`)
    }
  }
  callback(urls)
}

/**
 * 保存文章
 */
const handleSave = async () => {
  // 表单验证
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    if (isEdit.value) {
      // 编辑模式
      await articleApi.update(route.params.id, formData)
      ElMessage.success('保存成功')
    } else {
      // 新增模式
      await articleApi.add(formData)
      ElMessage.success('新增成功')
    }
    router.push('/article')
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

/**
 * 返回列表
 */
const handleBack = () => {
  router.push('/article')
}

// 初始化
onMounted(() => {
  getTagList()
  if (isEdit.value) {
    getArticleDetail()
  }
})
</script>

<style lang="scss" scoped>
.article-form {
  max-width: 1200px;
}

.editor-container {
  width: 100%;

  :deep(.md-editor) {
    height: 500px;
  }
}
</style>
