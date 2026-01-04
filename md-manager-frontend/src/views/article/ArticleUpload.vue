<template>
  <!--
    批量上传页面
    支持拖拽上传多个MD文件
  -->
  <div class="page-container">
    <h2 class="page-title">批量上传 MD 文件</h2>

    <!-- 上传区域 -->
    <el-upload
      ref="uploadRef"
      class="upload-area"
      drag
      multiple
      action="#"
      accept=".md,.markdown"
      :auto-upload="false"
      :on-change="handleFileChange"
      :on-remove="handleFileRemove"
      :file-list="fileList"
    >
      <el-icon class="el-icon--upload"><Upload /></el-icon>
      <div class="el-upload__text">
        将 MD 文件拖到此处，或<em>点击选择文件</em>
      </div>
      <template #tip>
        <div class="el-upload__tip">
          支持 .md 和 .markdown 格式文件，可同时上传多个文件
        </div>
      </template>
    </el-upload>

    <!-- 文件列表 -->
    <div v-if="fileList.length > 0" class="file-list">
      <h3>待上传文件（{{ fileList.length }} 个）</h3>
      <el-table :data="fileList" stripe border>
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="name" label="文件名" />
        <el-table-column label="大小" width="120">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link @click="removeFile(row)">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 上传按钮 -->
    <div class="upload-actions">
      <el-button
        type="primary"
        size="large"
        :loading="uploading"
        :disabled="fileList.length === 0"
        @click="handleUpload"
      >
        <el-icon><Upload /></el-icon>
        {{ uploading ? '上传中...' : '开始上传' }}
      </el-button>
      <el-button size="large" @click="clearFiles">
        清空列表
      </el-button>
    </div>

    <!-- 上传结果 -->
    <div v-if="uploadResult" class="upload-result">
      <el-alert
        :title="`上传完成：成功 ${uploadResult.successCount} 个，失败 ${uploadResult.failCount} 个`"
        :type="uploadResult.failCount === 0 ? 'success' : 'warning'"
        show-icon
      />
      <div v-if="uploadResult.failFiles && uploadResult.failFiles.length > 0" class="fail-list">
        <h4>失败文件：</h4>
        <ul>
          <li v-for="(file, index) in uploadResult.failFiles" :key="index">
            {{ file }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 批量上传页面组件
 *
 * 支持拖拽上传多个MD文件
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { articleApi } from '@/api/article'

// 上传组件引用
const uploadRef = ref(null)

// 文件列表
const fileList = ref([])

// 上传状态
const uploading = ref(false)

// 上传结果
const uploadResult = ref(null)

/**
 * 格式化文件大小
 */
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  }
}

/**
 * 文件选择变化
 */
const handleFileChange = (file, newFileList) => {
  // 检查文件类型
  const name = file.name.toLowerCase()
  if (!name.endsWith('.md') && !name.endsWith('.markdown')) {
    ElMessage.warning(`文件 "${file.name}" 不是有效的 Markdown 文件`)
    // 从列表中移除
    const index = newFileList.findIndex(f => f.uid === file.uid)
    if (index > -1) {
      newFileList.splice(index, 1)
    }
    return
  }
  fileList.value = newFileList
}

/**
 * 文件移除
 */
const handleFileRemove = (file, newFileList) => {
  fileList.value = newFileList
}

/**
 * 移除单个文件
 */
const removeFile = (file) => {
  const index = fileList.value.findIndex(f => f.uid === file.uid)
  if (index > -1) {
    fileList.value.splice(index, 1)
  }
}

/**
 * 清空文件列表
 */
const clearFiles = () => {
  fileList.value = []
  uploadResult.value = null
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

/**
 * 开始上传
 */
const handleUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }

  uploading.value = true
  uploadResult.value = null

  try {
    // 构建 FormData
    const formData = new FormData()
    fileList.value.forEach(file => {
      formData.append('files', file.raw)
    })

    // 调用上传接口
    const res = await articleApi.batchUpload(formData)
    uploadResult.value = res.data

    // 显示结果
    if (res.data.failCount === 0) {
      ElMessage.success('所有文件上传成功')
      // 清空文件列表
      fileList.value = []
      if (uploadRef.value) {
        uploadRef.value.clearFiles()
      }
    } else {
      ElMessage.warning(`上传完成，${res.data.failCount} 个文件失败`)
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}
</script>

<style lang="scss" scoped>
.upload-area {
  width: 100%;
  margin-bottom: 30px;

  :deep(.el-upload) {
    width: 100%;
  }

  :deep(.el-upload-dragger) {
    width: 100%;
    height: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }

  .el-icon--upload {
    font-size: 50px;
    color: #c0c4cc;
    margin-bottom: 15px;
  }
}

.file-list {
  margin-bottom: 20px;

  h3 {
    font-size: 16px;
    margin-bottom: 15px;
    color: #303133;
  }
}

.upload-actions {
  margin-bottom: 30px;
  display: flex;
  gap: 15px;
}

.upload-result {
  .fail-list {
    margin-top: 15px;
    padding: 15px;
    background-color: #fef0f0;
    border-radius: 4px;

    h4 {
      font-size: 14px;
      color: #f56c6c;
      margin-bottom: 10px;
    }

    ul {
      margin: 0;
      padding-left: 20px;
      color: #f56c6c;
      font-size: 14px;
    }
  }
}
</style>
