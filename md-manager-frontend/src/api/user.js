/**
 * 用户管理 API
 *
 * 包含用户的增删改查等接口
 */
import request from '@/utils/request'

export const userApi = {
  /**
   * 分页查询用户列表
   *
   * @param {Object} params - 查询参数
   * @param {number} params.pageNum - 页码
   * @param {number} params.pageSize - 每页条数
   * @param {string} params.keyword - 搜索关键词
   * @param {number} params.status - 状态筛选
   * @returns {Promise} 用户列表
   */
  getUserPage(params) {
    return request.get('/users', { params })
  },

  /**
   * 获取用户详情
   *
   * @param {number} id - 用户ID
   * @returns {Promise} 用户信息
   */
  getUserDetail(id) {
    return request.get(`/users/${id}`)
  },

  /**
   * 新增用户
   *
   * @param {Object} data - 用户数据
   * @returns {Promise} 新增结果
   */
  addUser(data) {
    return request.post('/users', data)
  },

  /**
   * 编辑用户
   *
   * @param {number} id - 用户ID
   * @param {Object} data - 用户数据
   * @returns {Promise} 编辑结果
   */
  updateUser(id, data) {
    return request.put(`/users/${id}`, data)
  },

  /**
   * 删除用户
   *
   * @param {number} id - 用户ID
   * @returns {Promise} 删除结果
   */
  deleteUser(id) {
    return request.delete(`/users/${id}`)
  },

  /**
   * 修改密码
   *
   * @param {Object} data - 密码数据
   * @param {string} data.oldPassword - 旧密码
   * @param {string} data.newPassword - 新密码
   * @returns {Promise} 修改结果
   */
  changePassword(data) {
    return request.put('/users/password', data)
  },

  /**
   * 重置用户密码
   *
   * @param {number} id - 用户ID
   * @returns {Promise} 新密码
   */
  resetPassword(id) {
    return request.post(`/users/${id}/reset-password`)
  }
}
