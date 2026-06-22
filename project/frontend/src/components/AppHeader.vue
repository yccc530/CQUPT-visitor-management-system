<template>
  <header class="app-header">
    <div class="header-left">
      <div class="header-title">{{ currentTitle }}</div>
      <div class="header-subtitle">重庆邮电大学智慧访客预约与出入校管理系统</div>
    </div>
    <div class="header-user">
      <el-tag type="primary" effect="light">{{ roleText }}</el-tag>
      <span>{{ user?.realName || user?.username || '当前用户' }}</span>
      <el-button :icon="SwitchButton" @click="handleLogout">退出</el-button>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { SwitchButton } from '@element-plus/icons-vue'
import { logout } from '@/api/auth'
import { getCurrentUser, removeToken } from '@/utils/auth'

const route = useRoute()
const router = useRouter()
const user = computed(() => getCurrentUser())
const currentTitle = computed(() => route.meta.title || '业务工作台')
const roleText = computed(() => {
  const role = user.value?.roles?.[0]
  return {
    ADMIN: '系统管理员', HOST: '被访人', DEPT_APPROVER: '部门审批人员',
    GATE_GUARD: '门岗安保人员', SCHOOL_MANAGER: '校级管理人员', VISITOR: '访客'
  }[role] || '已登录'
})

async function handleLogout() {
  try { await logout() } catch (error) { removeToken() }
  router.replace('/login')
}
</script>