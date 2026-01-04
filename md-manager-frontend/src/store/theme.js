/**
 * 主题状态管理
 *
 * 管理系统主题（浅色/深色/跟随系统）
 */
import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

// 主题类型
const THEME_LIGHT = 'light'
const THEME_DARK = 'dark'
const THEME_AUTO = 'auto'

// 本地存储Key
const STORAGE_KEY = 'md-manager-theme'

export const useThemeStore = defineStore('theme', () => {
  // 当前主题设置 (light/dark/auto)
  const themeSetting = ref(localStorage.getItem(STORAGE_KEY) || THEME_AUTO)

  // 实际应用的主题 (light/dark)
  const actualTheme = ref(THEME_LIGHT)

  /**
   * 获取系统主题偏好
   */
  const getSystemTheme = () => {
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return THEME_DARK
    }
    return THEME_LIGHT
  }

  /**
   * 应用主题到DOM
   */
  const applyTheme = (theme) => {
    actualTheme.value = theme
    const html = document.documentElement

    if (theme === THEME_DARK) {
      html.classList.add('dark')
      html.setAttribute('data-theme', 'dark')
    } else {
      html.classList.remove('dark')
      html.setAttribute('data-theme', 'light')
    }
  }

  /**
   * 更新实际主题
   */
  const updateActualTheme = () => {
    if (themeSetting.value === THEME_AUTO) {
      applyTheme(getSystemTheme())
    } else {
      applyTheme(themeSetting.value)
    }
  }

  /**
   * 设置主题
   */
  const setTheme = (theme) => {
    themeSetting.value = theme
    localStorage.setItem(STORAGE_KEY, theme)
    updateActualTheme()
  }

  /**
   * 切换主题（三态循环：浅色 -> 深色 -> 跟随系统）
   */
  const toggleTheme = () => {
    const themeOrder = [THEME_LIGHT, THEME_DARK, THEME_AUTO]
    const currentIndex = themeOrder.indexOf(themeSetting.value)
    const nextIndex = (currentIndex + 1) % themeOrder.length
    setTheme(themeOrder[nextIndex])
  }

  /**
   * 初始化主题
   */
  const initTheme = () => {
    updateActualTheme()

    // 监听系统主题变化
    if (window.matchMedia) {
      window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
        if (themeSetting.value === THEME_AUTO) {
          applyTheme(e.matches ? THEME_DARK : THEME_LIGHT)
        }
      })
    }
  }

  // 监听主题设置变化
  watch(themeSetting, () => {
    updateActualTheme()
  })

  return {
    themeSetting,
    actualTheme,
    setTheme,
    toggleTheme,
    initTheme,
    THEME_LIGHT,
    THEME_DARK,
    THEME_AUTO
  }
})
