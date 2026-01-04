/**
 * 文章相关 API
 *
 * 包含文章的增删改查、批量上传等接口
 */
import request from '@/utils/request'

export const articleApi = {
  /**
   * 获取文章列表（分页）
   *
   * @param {Object} params - 查询参数
   * @param {number} params.pageNum - 页码
   * @param {number} params.pageSize - 每页条数
   * @param {string} params.keyword - 搜索关键词
   * @param {number} params.tagId - 标签ID
   * @param {number} params.status - 状态
   * @returns {Promise} 文章列表
   */
  getList(params) {
    return request.get('/article/list', { params })
  },

  /**
   * 获取文章详情
   *
   * @param {number} id - 文章ID
   * @returns {Promise} 文章详情
   */
  getDetail(id) {
    return request.get(`/article/${id}`)
  },

  /**
   * 新增文章
   *
   * @param {Object} data - 文章数据
   * @returns {Promise} 新增结果
   */
  add(data) {
    return request.post('/article', data)
  },

  /**
   * 编辑文章
   *
   * @param {number} id - 文章ID
   * @param {Object} data - 文章数据
   * @returns {Promise} 编辑结果
   */
  update(id, data) {
    return request.put(`/article/${id}`, data)
  },

  /**
   * 删除文章
   *
   * @param {number} id - 文章ID
   * @returns {Promise} 删除结果
   */
  delete(id) {
    return request.delete(`/article/${id}`)
  },

  /**
   * 批量上传MD文件
   *
   * @param {FormData} formData - 包含文件的表单数据
   * @returns {Promise} 上传结果
   */
  batchUpload(formData) {
    return request.post('/article/batch-upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}
