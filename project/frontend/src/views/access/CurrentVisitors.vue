<template>
  <CrudTable title="当前在校访客页" description="展示已完成入校登记但尚未离校的访客，便于门岗和校级管理人员实时掌握在校状态。" table-title="当前在校访客" :columns="columns" :filter-items="filters" :api="api" :mock-data="mockData" :creatable="false" :editable="false" :removable="false" />
</template>
<script setup>
import CrudTable from '@/components/CrudTable.vue'
import { accessRecordApi } from '@/api/crud'
import { mockAccessRecords } from '@/api/mock'
const columns = [
  { prop: 'applyNo', label: '预约编号', width: 170 }, { prop: 'visitorName', label: '访客姓名' }, { prop: 'entryGateName', label: '入校校门' },
  { prop: 'entryTime', label: '入校时间', width: 170 }, { prop: 'accessStatus', label: '访问状态', statusKind: 'access' }
]
const filters = [{ prop: 'visitorName', label: '访客姓名' }, { prop: 'applyNo', label: '预约编号' }]
const api = { list: (params) => accessRecordApi.list({ ...params, accessStatus: 'ENTERED' }) }
const mockData = mockAccessRecords.filter((i) => i.accessStatus === 'ENTERED')
</script>