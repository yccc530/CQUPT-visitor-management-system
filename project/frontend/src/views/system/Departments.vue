<template>
  <CrudTable title="部门管理页" description="维护重庆邮电大学组织部门，用于被访人归属、部门审批和访客排行统计。" table-title="部门列表" :columns="columns" :filter-items="filters" :form-items="forms" :api="departmentApi" :mock-data="mockDepartments" />
</template>
<script setup>
import CrudTable from '@/components/CrudTable.vue'
import { departmentApi } from '@/api/crud'
import { mockDepartments } from '@/api/mock'
const columns = [{ prop: 'deptCode', label: '部门编码' }, { prop: 'deptName', label: '部门名称', width: 220 }, { prop: 'phone', label: '联系电话' }, { prop: 'status', label: '状态' }]
const filters = [{ prop: 'deptCode', label: '部门编码' }, { prop: 'deptName', label: '部门名称' }]
const forms = [{ prop: 'deptCode', label: '部门编码', required: true }, { prop: 'deptName', label: '部门名称', required: true }, { prop: 'parentId', label: '上级部门ID', type: 'number' }, { prop: 'phone', label: '联系电话' }, { prop: 'sortOrder', label: '排序', type: 'number' }, { prop: 'status', label: '状态', type: 'select', options: [{ label: '启用', value: 'ENABLED' }, { label: '禁用', value: 'DISABLED' }] }]
</script>