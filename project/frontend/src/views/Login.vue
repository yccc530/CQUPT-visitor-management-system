<template>
  <main class="login-shell">
    <section class="login-visual">
      <div class="login-brand">
        <h1>重庆邮电大学智慧访客预约与出入校管理系统</h1>
        <p>面向访客、被访人、部门审批人员、门岗安保和校级管理人员，覆盖预约申请、审批确认、通行核验、出入校登记、黑名单管控和统计报表。</p>
      </div>
      <div class="login-intro">
        <p>Smart Visitor Reservation and Campus Access Management System of Chongqing University of Posts and Telecommunications</p>
      </div>
    </section>
    <section class="login-panel">
      <el-card class="login-card">
        <template #header>
          <div>
            <h2 style="margin: 0; font-size: 24px">系统登录</h2>
            <p style="margin: 8px 0 0; color: #64748b">请选择测试账号或输入账号密码</p>
          </div>
        </template>
        <el-form :model="form" :rules="rules" ref="formRef" label-position="top" @keyup.enter="handleLogin">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" size="large" placeholder="请输入用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" size="large" show-password placeholder="请输入密码" :prefix-icon="Lock" />
          </el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">登录系统</el-button>
        </el-form>
        <div class="account-grid">
          <button v-for="item in accounts" :key="item.username" class="account-chip" @click="fillAccount(item)">
            <strong>{{ item.role }}</strong><br />{{ item.username }} / 123456
          </button>
        </div>
      </el-card>
    </section>
  </main>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { login } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const accounts = [
  { role: '系统管理员', username: 'admin' },
  { role: '被访人', username: 'teacher01' },
  { role: '部门审批', username: 'approver01' },
  { role: '门岗安保', username: 'guard01' },
  { role: '校级管理', username: 'manager01' }
]

function fillAccount(item) {
  form.username = item.username
  form.password = '123456'
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await login(form)
    ElMessage.success('登录成功')
    router.replace('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>