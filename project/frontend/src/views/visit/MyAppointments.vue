<template>
  <CrudTable title="我的预约页" description="查看本人或当前角色可见的预约申请，支持按手机号、证件号和预约状态筛选。" table-title="预约记录" :columns="columns" :filter-items="filters" :form-items="[]" :api="api" :mock-data="mockApplies" :creatable="false" :editable="false" :removable="false">
    <template #actions="{ row }">
      <el-button size="small" type="primary" plain @click="showDetail(row)">详情</el-button>
      <el-button v-if="['PENDING_HOST','HOST_CONFIRMED','PENDING_DEPT'].includes(row.applyStatus)" size="small" type="warning" plain @click="cancel(row)">取消</el-button>
    </template>
  </CrudTable>
</template>
<script setup>
import { ElMessage } from 'element-plus'
import CrudTable from '@/components/CrudTable.vue'
import { workflowApi } from '@/api/workflow'
import { mockApplies } from '@/api/mock'
const columns = [
  { prop: 'applyNo', label: '预约编号', width: 170 }, { prop: 'visitorName', label: '访客姓名' }, { prop: 'phone', label: '手机号' },
  { prop: 'visitReason', label: '来访事由', width: 200 }, { prop: 'planStartTime', label: '开始时间', width: 170 },
  { prop: 'applyStatus', label: '预约状态', statusKind: 'apply' }, { prop: 'accessStatus', label: '访问状态', statusKind: 'access' }
]
const filters = [{ prop: 'phone', label: '手机号' }, { prop: 'idNumber', label: '证件号' }]
const api = { list: (params) => workflowApi.myApplications(params) }
function showDetail(row) { ElMessage.info(`${row.applyNo}：${row.visitReason}`) }
async function cancel(row) { await workflowApi.cancelApply(row.id, '前端页面取消'); ElMessage.success('预约已取消') }
</script>