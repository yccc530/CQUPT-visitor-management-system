import { accessRecordApi, approvalApi, visitApplyApi } from './crud'

export async function loadDashboardData() {
  const [applies, accessRecords, approvals] = await Promise.allSettled([
    visitApplyApi.list({ current: 1, size: 200 }),
    accessRecordApi.list({ current: 1, size: 200 }),
    approvalApi.list({ current: 1, size: 200 })
  ])
  return {
    applies: normalizeRecords(applies),
    accessRecords: normalizeRecords(accessRecords),
    approvals: normalizeRecords(approvals)
  }
}

function normalizeRecords(result) {
  if (result.status !== 'fulfilled') return []
  return result.value?.records || []
}