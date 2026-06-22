<template>
  <CrudTable title="校门管理页" description="维护校门基础信息，支撑门岗核验、入校登记、离校登记和校门通行统计。" table-title="校门列表" :columns="columns" :filter-items="filters" :form-items="forms" :api="gateApi" :mock-data="mockGates" />
</template>
<script setup>
import CrudTable from '@/components/CrudTable.vue'
import { gateApi } from '@/api/crud'
import { mockGates } from '@/api/mock'
const columns = [{ prop: 'gateCode', label: '校门编码' }, { prop: 'gateName', label: '校门名称' }, { prop: 'gateLocation', label: '位置', width: 240 }, { prop: 'gateType', label: '类型' }, { prop: 'status', label: '状态' }]
const filters = [{ prop: 'gateCode', label: '校门编码' }, { prop: 'gateName', label: '校门名称' }]
const forms = [{ prop: 'gateCode', label: '校门编码', required: true }, { prop: 'gateName', label: '校门名称', required: true }, { prop: 'gateLocation', label: '位置' }, { prop: 'gateType', label: '类型', type: 'select', options: [{ label: '普通通道', value: 'NORMAL' }, { label: '行人通道', value: 'PEDESTRIAN' }, { label: '车行通道', value: 'VEHICLE' }] }, { prop: 'status', label: '状态', type: 'select', options: [{ label: '启用', value: 'ENABLED' }, { label: '禁用', value: 'DISABLED' }] }, { prop: 'remark', label: '备注', type: 'textarea' }]
</script>