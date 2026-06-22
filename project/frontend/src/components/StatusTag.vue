<template>
  <el-tag :type="meta.type" effect="light" round>
    <span class="status-dot" :style="{ backgroundColor: dotColor }" />{{ meta.text }}
  </el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { accessStatusMap, applyStatusMap, approvalResultMap, blacklistStatusMap, statusMeta } from '@/utils/status'

const props = defineProps({
  value: { type: String, default: '' },
  kind: { type: String, default: 'apply' }
})
const maps = { apply: applyStatusMap, access: accessStatusMap, approval: approvalResultMap, blacklist: blacklistStatusMap }
const meta = computed(() => statusMeta(maps[props.kind] || applyStatusMap, props.value))
const dotColor = computed(() => ({ success: '#16a34a', warning: '#f59e0b', danger: '#dc2626', info: '#1d4ed8' }[meta.value.type] || '#64748b'))
</script>