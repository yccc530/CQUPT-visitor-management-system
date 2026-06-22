<template>
  <CrudTable title="待确认预约页" description="被访人处理与本人相关的访客预约，确认后进入部门审批，拒绝后终止入校流程。" table-title="待被访人确认" :columns="columns" :filter-items="filters" :api="api" :mock-data="mockData" :creatable="false" :editable="false" :removable="false">
    <template #actions="{ row }">
      <el-button size="small" type="success" plain @click="confirm(row)">确认</el-button>
      <el-button size="small" type="danger" plain @click="reject(row)">拒绝</el-button>
    </template>
  </CrudTable>
</template>
<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import CrudTable from '@/components/CrudTable.vue'
import { workflowApi } from '@/api/workflow'
import { mockApplies } from '@/api/mock'
const columns = [
  { prop: 'applyNo', label: '预约编号', width: 170 }, { prop: 'visitorName', label: '访客姓名' }, { prop: 'phone', label: '手机号' },
  { prop: 'visitReason', label: '来访事由', width: 220 }, { prop: 'planStartTime', label: '访问开始', width: 170 }, { prop: 'applyStatus', label: '状态', statusKind: 'apply' }
]
const filters = [{ prop: 'visitorName', label: '访客姓名' }, { prop: 'phone', label: '手机号' }]
const api = { list: workflowApi.hostPending }
const mockData = mockApplies.filter((item) => item.applyStatus === 'PENDING_HOST')
async function confirm(row) { await workflowApi.hostConfirm(row.id, { comment: '被访人确认接待' }); ElMessage.success('已确认，进入部门审批') }
async function reject(row) { const { value } = await ElMessageBox.prompt('请输入拒绝原因', '被访人拒绝'); await workflowApi.hostReject(row.id, { comment: value }); ElMessage.success('已拒绝预约') }
</script>