/**
 * 用户状态管理
 *
 * 使用 Pinia 管理用户登录状态、用户信息等
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性：是否已登录
  const isLoggedIn = computed(() => !!token.value)

  /**
   * 用户登录
   *
   * @param {Object} loginData - 登录信息（用户名、密码）
   * @returns {Promise} 登录结果
   */
  const login = async (loginData) => {
    const res = await authApi.login(loginData)
    // 保存 token 和用户信息
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    // 持久化到 localStorage
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
    return res
  }

  /**
   * 获取用户信息
   *
   * @returns {Promise} 用户信息
   */
  const getUserInfo = async () => {
    const res = await authApi.getUserInfo()
    userInfo.value = res.data
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    return res
  }

  /**
   * 用户登出
   */
  const logout = () => {
    // 清除状态
    token.value = ''
    userInfo.value = null
    // 清除 localStorage
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    // 跳转到登录页
    router.push({ name: 'Login' })
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    getUserInfo,
    logout
  }
})
