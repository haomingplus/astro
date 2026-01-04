<template>
  <!--
    布局组件
    包含侧边栏、顶部导航和主内容区域
  -->
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <!-- Logo -->
      <div class="logo">
        <span v-if="!isCollapse">MD管理系统</span>
        <span v-else>MD</span>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/article">
          <el-icon><Document /></el-icon>
          <template #title>文章管理</template>
        </el-menu-item>
        <el-menu-item index="/article/add">
          <el-icon><EditPen /></el-icon>
          <template #title>写文章</template>
        </el-menu-item>
        <el-menu-item index="/article/upload">
          <el-icon><Upload /></el-icon>
          <template #title>批量上传</template>
        </el-menu-item>
        <el-menu-item index="/tag">
          <el-icon><PriceTag /></el-icon>
          <template #title>标签管理</template>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 右侧内容 -->
    <el-container class="layout-main-container">
      <!-- 顶部导航 -->
      <el-header class="layout-header">
        <div class="header-left">
          <!-- 折叠按钮 -->
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <!-- 面包屑 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentRouteName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 主题切换 -->
          <el-tooltip :content="themeTooltip" placement="bottom">
            <el-icon class="theme-btn" @click="themeStore.toggleTheme">
              <Sunny v-if="themeStore.themeSetting === 'light'" />
              <Moon v-else-if="themeStore.themeSetting === 'dark'" />
              <Monitor v-else />
            </el-icon>
          </el-tooltip>

          <!-- 用户信息 -->
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区域 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
/**
 * 布局组件
 *
 * 提供整体页面布局，包含侧边栏导航和顶部用户信息
 */
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Document,
  EditPen,
  Upload,
  PriceTag,
  User,
  Fold,
  Expand,
  UserFilled,
  ArrowDown,
  SwitchButton,
  Sunny,
  Moon,
  Monitor
} from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { useThemeStore } from '@/store/theme'

// 路由
const route = useRoute()

// 用户状态
const userStore = useUserStore()

// 主题状态
const themeStore = useThemeStore()

// 主题提示文本
const themeTooltip = computed(() => {
  const map = {
    light: '当前：浅色主题',
    dark: '当前：深色主题',
    auto: '当前：跟随系统'
  }
  return map[themeStore.themeSetting] || '切换主题'
})

// 侧边栏折叠状态
const isCollapse = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  const path = route.path
  // 编辑页面也高亮文章管理菜单
  if (path.startsWith('/article/edit') || path.startsWith('/article/view')) {
    return '/article'
  }
  return path
})

// 当前路由名称
const currentRouteName = computed(() => {
  return route.meta.title || ''
})

/**
 * 切换侧边栏折叠状态
 */
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

/**
 * 处理下拉菜单命令
 */
const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    }).then(() => {
      userStore.logout()
    }).catch(() => {})
  }
}

// 组件挂载时初始化主题
onMounted(() => {
  themeStore.initTheme()
})
</script>

<style lang="scss" scoped>
.layout-container {
  width: 100%;
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: 600;
    background-color: #263445;
    white-space: nowrap;
  }

  .el-menu {
    border-right: none;
  }
}

.layout-main-container {
  flex-direction: column;
}

.layout-header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .header-left {
    display: flex;
    align-items: center;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 15px;
      color: #606266;

      &:hover {
        color: #409eff;
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;

    .theme-btn {
      font-size: 20px;
      cursor: pointer;
      margin-right: 20px;
      color: #606266;

      &:hover {
        color: #409eff;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;

      .username {
        margin: 0 8px;
        color: #606266;
      }
    }
  }
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
