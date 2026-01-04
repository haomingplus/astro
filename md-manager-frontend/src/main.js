/**
 * Vue 应用入口文件
 *
 * 初始化Vue应用，配置Element Plus、路由、状态管理等
 */
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 全局样式
import './styles/global.scss'

// 创建Vue应用实例
const app = createApp(App)

// 注册所有Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用Element Plus（中文）
app.use(ElementPlus, { locale: zhCn })

// 使用Pinia状态管理
app.use(createPinia())

// 使用路由
app.use(router)

// 挂载应用
app.mount('#app')
