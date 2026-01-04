import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

/**
 * Vite 配置文件
 *
 * 配置了Vue插件、路径别名、开发服务器代理等
 */
export default defineConfig({
  // 插件配置
  plugins: [vue()],

  // 路径别名配置
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },

  // 开发服务器配置
  server: {
    port: 5173,
    host: '0.0.0.0',
    // 代理配置，解决跨域问题
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },

  // 构建配置
  build: {
    outDir: 'dist',
    // 关闭 sourcemap
    sourcemap: false,
    // 分包配置
    rollupOptions: {
      output: {
        manualChunks: {
          'element-plus': ['element-plus'],
          'md-editor': ['md-editor-v3']
        }
      }
    }
  }
})
