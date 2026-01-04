<template>
  <!--
    标签管理页面
    支持标签的增删改查
  -->
  <div class="page-container">
    <h2 class="page-title">标签管理</h2>

    <!-- 操作按钮 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon> 新增标签
      </el-button>
    </div>

    <!-- 标签列表表格 -->
    <el-table
      v-loading="loading"
      :data="tagList"
      stripe
      border
      style="width: 100%"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="标签名称" min-width="150" />
      <el-table-column prop="slug" label="别名" width="150" />
      <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
      <el-table-column prop="articleCount" label="文章数" width="100" align="center" />
      <el-table-column prop="sortOrder" label="排序" width="100" align="center" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">
            <el-icon><Edit /></el-icon> 编辑
          </el-button>
          <el-button type="danger" link @click="handleDelete(row)">
            <el-icon><Delete /></el-icon> 删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑标签' : '新增标签'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="标签名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="别名">
          <el-input v-model="formData.slug" placeholder="用于URL，不填自动生成" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入标签描述"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="9999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 标签管理页面组件
 *
 * 提供标签的增删改查功能
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { tagApi } from '@/api/tag'

// 加载状态
const loading = ref(false)

// 保存状态
const saving = ref(false)

// 标签列表
const tagList = ref([])

// 对话框显示状态
const dialogVisible = ref(false)

// 是否是编辑模式
const isEdit = ref(false)

// 当前编辑的标签ID
const currentId = ref(null)

// 表单引用
const formRef = ref(null)

// 表单数据
const formData = reactive({
  name: '',
  slug: '',
  description: '',
  sortOrder: 0
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' }
  ]
}

/**
 * 获取标签列表
 */
const getTagList = async () => {
  loading.value = true
  try {
    const res = await tagApi.getList()
    tagList.value = res.data
  } catch (error) {
    console.error('获取标签列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 重置表单
 */
const resetForm = () => {
  formData.name = ''
  formData.slug = ''
  formData.description = ''
  formData.sortOrder = 0
}

/**
 * 新增标签
 */
const handleAdd = () => {
  isEdit.value = false
  currentId.value = null
  resetForm()
  dialogVisible.value = true
}

/**
 * 编辑标签
 */
const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  formData.name = row.name
  formData.slug = row.slug || ''
  formData.description = row.description || ''
  formData.sortOrder = row.sortOrder || 0
  dialogVisible.value = true
}

/**
 * 保存标签
 */
const handleSave = async () => {
  // 表单验证
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    if (isEdit.value) {
      await tagApi.update(currentId.value, formData)
      ElMessage.success('编辑成功')
    } else {
      await tagApi.add(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getTagList()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

/**
 * 删除标签
 */
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除标签"${row.name}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await tagApi.delete(row.id)
      ElMessage.success('删除成功')
      getTagList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 初始化
onMounted(() => {
  getTagList()
})
</script>

<style lang="scss" scoped>
.action-bar {
  margin-bottom: 20px;
}
</style>
