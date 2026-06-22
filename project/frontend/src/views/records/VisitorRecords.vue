<template>
  <CrudTable title="访客记录查询页" description="按访客、预约编号、审批状态和访问状态查询历史预约与出入校归档记录。" table-title="访客访问记录" :columns="columns" :filter-items="filters" :api="visitApplyApi" :mock-data="mockApplies" :creatable="false" :editable="false" :removable="false" />
</template>
<script setup>
import CrudTable from '@/components/CrudTable.vue'
import { visitApplyApi } from '@/api/crud'
import { mockApplies } from '@/api/mock'
const columns = [
  { prop: 'applyNo', label: '预约编号', width: 170 }, { prop: 'visitorName', label: '访客姓名' }, { prop: 'departmentName', label: '访问部门', width: 180 },
  { prop: 'visitReason', label: '来访事由', width: 220 }, { prop: 'planStartTime', label: '计划开始', width: 170 },
  { prop: 'applyStatus', label: '预约状态', statusKind: 'apply' }, { prop: 'accessStatus', label: '访问状态', statusKind: 'access' }
]
const filters = [{ prop: 'applyNo', label: '预约编号' }, { prop: 'visitorName', label: '访客姓名' }, { prop: 'phone', label: '手机号' }]
</script>