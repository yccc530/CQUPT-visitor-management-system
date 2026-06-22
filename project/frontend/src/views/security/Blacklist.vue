<template>
  <CrudTable title="黑名单管理页" description="维护有效黑名单，预约提交和门岗核验会按手机号、证件号进行拦截。" table-title="黑名单记录" :columns="columns" :filter-items="filters" :form-items="forms" :api="blacklistApi" :mock-data="mockBlacklists">
    <template #actions="{ row }"><el-button size="small" type="warning" plain @click="review(row)">复核</el-button></template>
  </CrudTable>
</template>
<script setup>
import { ElMessage } from 'element-plus'
import CrudTable from '@/components/CrudTable.vue'
import { blacklistApi } from '@/api/crud'
import { mockBlacklists } from '@/api/mock'
const columns = [
  { prop: 'visitorName', label: '访客姓名' }, { prop: 'idNumber', label: '证件号', width: 190 }, { prop: 'phone', label: '手机号' },
  { prop: 'level', label: '等级' }, { prop: 'reason', label: '原因', width: 240 }, { prop: 'status', label: '状态', statusKind: 'blacklist' }
]
const filters = [{ prop: 'phone', label: '手机号' }, { prop: 'idNumber', label: '证件号' }, { prop: 'status', label: '状态', type: 'select', options: [{ label: '生效中', value: 'ACTIVE' }, { label: '已失效', value: 'EXPIRED' }] }]
const forms = [{ prop: 'visitorId', label: '访客ID', type: 'number' }, { prop: 'idNumber', label: '证件号', required: true }, { prop: 'phone', label: '手机号', required: true }, { prop: 'level', label: '等级', type: 'select', options: [{ label: '禁止入校', value: 'FORBIDDEN' }, { label: '预警复核', value: 'WARNING' }] }, { prop: 'reason', label: '原因', type: 'textarea' }, { prop: 'status', label: '状态', type: 'select', options: [{ label: '生效中', value: 'ACTIVE' }, { label: '已移除', value: 'REMOVED' }] }]
function review(row) { ElMessage.info(`${row.visitorName || row.phone} 已进入人工复核队列`) }
</script>