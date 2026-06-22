<template>
  <CrudTable title="用户管理页" description="维护校内用户、安保人员、审批人员和管理人员账号，支撑 RBAC 权限控制。" table-title="用户账号" :columns="columns" :filter-items="filters" :form-items="forms" :api="userApi" :mock-data="mockUsers" />
</template>
<script setup>
import CrudTable from '@/components/CrudTable.vue'
import { userApi } from '@/api/crud'
import { mockUsers } from '@/api/mock'
const columns = [{ prop: 'username', label: '用户名' }, { prop: 'realName', label: '姓名' }, { prop: 'phone', label: '手机号' }, { prop: 'departmentId', label: '部门ID' }, { prop: 'userType', label: '用户类型' }, { prop: 'status', label: '状态' }]
const filters = [{ prop: 'username', label: '用户名' }, { prop: 'realName', label: '姓名' }, { prop: 'userType', label: '用户类型' }]
const forms = [{ prop: 'username', label: '用户名', required: true }, { prop: 'realName', label: '姓名', required: true }, { prop: 'phone', label: '手机号' }, { prop: 'email', label: '邮箱' }, { prop: 'departmentId', label: '部门ID', type: 'number' }, { prop: 'userType', label: '用户类型', type: 'select', options: [{ label: '管理员', value: 'ADMIN' }, { label: '被访人', value: 'HOST' }, { label: '审批人员', value: 'APPROVER' }, { label: '安保', value: 'GUARD' }, { label: '校级管理', value: 'MANAGER' }] }, { prop: 'status', label: '状态', type: 'select', options: [{ label: '启用', value: 'ENABLED' }, { label: '禁用', value: 'DISABLED' }] }]
</script>