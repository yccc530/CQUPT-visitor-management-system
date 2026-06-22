<template>
  <CrudTable title="系统日志页" description="查看登录认证、预约审批、门岗核验、出入校登记等关键操作审计记录。" table-title="操作日志" :columns="columns" :filter-items="filters" :api="logApi" :mock-data="mockLogs" :creatable="false" :editable="false" :removable="false" />
</template>
<script setup>
import CrudTable from '@/components/CrudTable.vue'
import { logApi } from '@/api/crud'
import { mockLogs } from '@/api/mock'
const columns = [
  { prop: 'operatorName', label: '操作人' }, { prop: 'moduleName', label: '模块' }, { prop: 'operationType', label: '操作类型' },
  { prop: 'requestUrl', label: '接口路径', width: 260 }, { prop: 'operationResult', label: '结果' }, { prop: 'operationTime', label: '时间', width: 170 }
]
const filters = [{ prop: 'operatorName', label: '操作人' }, { prop: 'moduleName', label: '模块' }, { prop: 'operationType', label: '操作类型' }]
</script>