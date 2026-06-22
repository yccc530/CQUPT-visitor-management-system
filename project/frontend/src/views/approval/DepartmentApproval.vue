<template>
  <CrudTable title="部门审批页" description="部门审批人员审核本部门预约申请，审批通过后系统自动生成通行凭证。" table-title="待部门审批" :columns="columns" :filter-items="filters" :api="api" :mock-data="mockData" :creatable="false" :editable="false" :removable="false">
    <template #actions="{ row }">
      <el-button size="small" type="success" plain @click="approve(row)">通过</el-button>
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
  { prop: 'applyNo', label: '预约编号', width: 170 }, { prop: 'visitorName', label: '访客姓名' }, { prop: 'departmentName', label: '访问部门', width: 180 },
  { prop: 'visitReason', label: '来访事由', width: 220 }, { prop: 'planStartTime', label: '访问开始', width: 170 }, { prop: 'applyStatus', label: '状态', statusKind: 'apply' }
]
const filters = [{ prop: 'visitorName', label: '访客姓名' }, { prop: 'applyNo', label: '预约编号' }]
const api = { list: workflowApi.departmentPending }
const mockData = mockApplies.filter((item) => ['HOST_CONFIRMED', 'PENDING_DEPT'].includes(item.applyStatus))
async function approve(row) { await workflowApi.departmentApprove(row.id, { comment: '部门审批通过' }); ElMessage.success('审批通过，已生成通行凭证') }
async function reject(row) { const { value } = await ElMessageBox.prompt('请输入审批拒绝原因', '部门审批拒绝'); await workflowApi.departmentReject(row.id, { comment: value }); ElMessage.success('已拒绝预约') }
</script>