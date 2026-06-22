<template>
  <CrudTable title="角色权限管理页" description="维护访客、被访人、部门审批、门岗安保、系统管理员和校级管理人员角色。" table-title="角色列表" :columns="columns" :filter-items="filters" :form-items="forms" :api="roleApi" :mock-data="mockRoles" />
</template>
<script setup>
import CrudTable from '@/components/CrudTable.vue'
import { roleApi } from '@/api/crud'
const mockRoles = [
  { id: 1, roleCode: 'VISITOR', roleName: '访客', roleDesc: '提交预约与查看预约状态', status: 'ENABLED' },
  { id: 2, roleCode: 'HOST', roleName: '被访人', roleDesc: '确认本人相关预约', status: 'ENABLED' },
  { id: 3, roleCode: 'DEPT_APPROVER', roleName: '部门审批人员', roleDesc: '审批本部门预约申请', status: 'ENABLED' },
  { id: 4, roleCode: 'GATE_GUARD', roleName: '门岗安保人员', roleDesc: '核验通行码并登记出入校', status: 'ENABLED' },
  { id: 5, roleCode: 'ADMIN', roleName: '系统管理员', roleDesc: '拥有全部功能', status: 'ENABLED' }
]
const columns = [{ prop: 'roleCode', label: '角色编码' }, { prop: 'roleName', label: '角色名称' }, { prop: 'roleDesc', label: '说明', width: 240 }, { prop: 'status', label: '状态' }]
const filters = [{ prop: 'roleCode', label: '角色编码' }, { prop: 'roleName', label: '角色名称' }]
const forms = [{ prop: 'roleCode', label: '角色编码', required: true }, { prop: 'roleName', label: '角色名称', required: true }, { prop: 'roleDesc', label: '说明', type: 'textarea' }, { prop: 'status', label: '状态', type: 'select', options: [{ label: '启用', value: 'ENABLED' }, { label: '禁用', value: 'DISABLED' }] }]
</script>