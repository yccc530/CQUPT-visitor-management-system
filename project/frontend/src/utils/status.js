export const applyStatusMap = {
  PENDING_HOST: { text: '待确认', type: 'warning', class: 'status-pending' },
  HOST_CONFIRMED: { text: '被访人已确认', type: 'info', class: 'status-info' },
  PENDING_DEPT: { text: '待部门审批', type: 'warning', class: 'status-pending' },
  HOST_REJECTED: { text: '被访人已拒绝', type: 'danger', class: 'status-danger' },
  APPROVED: { text: '审批通过', type: 'success', class: 'status-approved' },
  REJECTED: { text: '审批拒绝', type: 'danger', class: 'status-danger' },
  CANCELED: { text: '已取消', type: 'info', class: 'status-muted' },
  REJECTED_BLACKLIST: { text: '黑名单拦截', type: 'danger', class: 'status-danger' }
}

export const accessStatusMap = {
  NOT_ENTERED: { text: '未入校', type: 'info', class: 'status-info' },
  ENTERED: { text: '已入校', type: 'success', class: 'status-approved' },
  EXITED: { text: '已离校', type: 'info', class: 'status-muted' },
  OVERTIME: { text: '超时未离校', type: 'warning', class: 'status-pending' },
  ABNORMAL: { text: '异常处理', type: 'danger', class: 'status-danger' }
}

export const approvalResultMap = {
  APPROVED: { text: '同意', type: 'success', class: 'status-approved' },
  REJECTED: { text: '拒绝', type: 'danger', class: 'status-danger' },
  RETURNED: { text: '退回修改', type: 'warning', class: 'status-pending' },
  PENDING: { text: '待处理', type: 'warning', class: 'status-pending' }
}

export const blacklistStatusMap = {
  ACTIVE: { text: '生效中', type: 'danger', class: 'status-danger' },
  EXPIRED: { text: '已失效', type: 'info', class: 'status-muted' },
  REMOVED: { text: '已移除', type: 'info', class: 'status-muted' }
}

export function statusMeta(map, value) {
  return map[value] || { text: value || '未知', type: 'info', class: 'status-muted' }
}

export function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}
