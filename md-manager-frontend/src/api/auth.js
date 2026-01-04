/**
 * 认证相关 API
 *
 * 包含登录、获取用户信息、登出等接口
 */
import request from '@/utils/request'

export const authApi = {
  /**
   * 用户登录
   *
   * @param {Object} data - 登录信息
   * @param {string} data.username - 用户名
   * @param {string} data.password - 密码
   * @returns {Promise} 登录结果
   */
  login(data) {
    return request.post('/auth/login', data)
  },

  /**
   * 获取当前用户信息
   *
   * @returns {Promise} 用户信息
   */
  getUserInfo() {
    return request.get('/auth/info')
  },

  /**
   * 用户登出
   *
   * @returns {Promise} 登出结果
   */
  logout() {
    return request.post('/auth/logout')
  }
}
