<template>
  <!--
    应用根组件
    使用 router-view 渲染路由匹配的组件
  -->
  <router-view />
</template>

<script setup>
/**
 * App 根组件
 *
 * 作为应用的入口组件，只负责渲染路由视图
 */
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
}
</style>
