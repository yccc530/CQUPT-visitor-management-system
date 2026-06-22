<template>
  <aside class="app-sidebar">
    <div class="sidebar-brand">
      <div class="brand-mark">邮</div>
      <div class="brand-title">智慧访客<br />出入校管理</div>
    </div>
    <el-scrollbar>
      <el-menu :default-active="route.path" router>
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { getCurrentUser } from '@/utils/auth'
import { visibleMenus } from '@/utils/menu'

const route = useRoute()
const menus = computed(() => visibleMenus(getCurrentUser()?.roles || []))
</script>