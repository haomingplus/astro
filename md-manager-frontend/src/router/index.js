/**
 * Vue Router 路由配置
 *
 * 定义应用的路由规则和导航守卫
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/article',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'article',
        name: 'ArticleList',
        component: () => import('@/views/article/ArticleList.vue'),
        meta: { title: '文章列表' }
      },
      {
        path: 'article/add',
        name: 'ArticleAdd',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: { title: '新增文章' }
      },
      {
        path: 'article/edit/:id',
        name: 'ArticleEdit',
        component: () => import('@/views/article/ArticleEdit.vue'),
        meta: { title: '编辑文章' }
      },
      {
        path: 'article/view/:id',
        name: 'ArticleView',
        component: () => import('@/views/article/ArticleView.vue'),
        meta: { title: '查看文章' }
      },
      {
        path: 'article/upload',
        name: 'ArticleUpload',
        component: () => import('@/views/article/ArticleUpload.vue'),
        meta: { title: '批量上传' }
      },
      {
        path: 'tag',
        name: 'TagList',
        component: () => import('@/views/tag/TagList.vue'),
        meta: { title: '标签管理' }
      }
    ]
  },
  {
    // 404 页面
    path: '/:pathMatch(.*)*',
    redirect: '/article'
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - MD管理系统` : 'MD管理系统'

  // 不需要认证的页面直接放行
  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  // 检查是否已登录
  const userStore = useUserStore()
  if (!userStore.isLoggedIn) {
    // 未登录，跳转到登录页
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  next()
})

export default router
