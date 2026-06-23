import request from '@/utils/request'
import { accessRecordApi, approvalApi, visitApplyApi } from './crud'

export async function loadDashboardData() {
  const [dashboard, applies, accessRecords, approvals] = await Promise.allSettled([
    request.get('/statistics/dashboard'),
    visitApplyApi.list({ current: 1, size: 200 }),
    accessRecordApi.list({ current: 1, size: 200 }),
    approvalApi.list({ current: 1, size: 200 })
  ])
  return {
    dashboard: normalizeValue(dashboard),
    applies: normalizeRecords(applies),
    accessRecords: normalizeRecords(accessRecords),
    approvals: normalizeRecords(approvals)
  }
}

export function loadStatisticsDashboard() {
  return request.get('/statistics/dashboard')
}

function normalizeValue(result) {
  if (result.status !== 'fulfilled') return null
  return result.value || null
}

function normalizeRecords(result) {
  if (result.status !== 'fulfilled') return []
  return result.value?.records || []
}