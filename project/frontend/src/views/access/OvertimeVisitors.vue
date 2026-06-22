<template>
  <CrudTable title="超时未离校访客页" description="查询预计离校时间已过但未离校的访客，可一键标记为超时未离校。" table-title="超时未离校列表" :columns="columns" :filter-items="filters" :api="api" :mock-data="mockData" :creatable="false" :editable="false" :removable="false">
    <template #actions="{ row }"><el-button size="small" type="warning" plain @click="mark(row)">标记超时</el-button></template>
  </CrudTable>
</template>
<script setup>
import { ElMessage } from 'element-plus'
import CrudTable from '@/components/CrudTable.vue'
import { workflowApi } from '@/api/workflow'
import { mockAccessRecords } from '@/api/mock'
const columns = [
  { prop: 'applyNo', label: '预约编号', width: 170 }, { prop: 'visitorName', label: '访客姓名' }, { prop: 'entryGateName', label: '入校校门' },
  { prop: 'entryTime', label: '入校时间', width: 170 }, { prop: 'accessStatus', label: '访问状态', statusKind: 'access' }
]
const filters = [{ prop: 'visitorName', label: '访客姓名' }, { prop: 'applyNo', label: '预约编号' }]
const api = { list: workflowApi.overtime }
const mockData = mockAccessRecords.filter((i) => i.accessStatus === 'OVERTIME')
async function mark() { await workflowApi.overtime({ current: 1, size: 10, mark: true }); ElMessage.success('已标记超时未离校') }
</script>