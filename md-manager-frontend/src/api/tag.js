/**
 * 标签相关 API
 *
 * 包含标签的增删改查接口
 */
import request from '@/utils/request'

export const tagApi = {
  /**
   * 获取标签列表
   *
   * @param {string} keyword - 搜索关键词（可选）
   * @returns {Promise} 标签列表
   */
  getList(keyword) {
    return request.get('/tag/list', { params: { keyword } })
  },

  /**
   * 获取标签详情
   *
   * @param {number} id - 标签ID
   * @returns {Promise} 标签详情
   */
  getDetail(id) {
    return request.get(`/tag/${id}`)
  },

  /**
   * 新增标签
   *
   * @param {Object} data - 标签数据
   * @returns {Promise} 新增结果
   */
  add(data) {
    return request.post('/tag', data)
  },

  /**
   * 编辑标签
   *
   * @param {number} id - 标签ID
   * @param {Object} data - 标签数据
   * @returns {Promise} 编辑结果
   */
  update(id, data) {
    return request.put(`/tag/${id}`, data)
  },

  /**
   * 删除标签
   *
   * @param {number} id - 标签ID
   * @returns {Promise} 删除结果
   */
  delete(id) {
    return request.delete(`/tag/${id}`)
  }
}
