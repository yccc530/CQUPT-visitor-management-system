import request from '@/utils/request'

export const workflowApi = {
  submitApply: (data) => request.post('/workflow/visit-applies', data),
  myApplications: (params) => request.get('/workflow/visit-applies/my', { params }),
  detail: (id) => request.get(`/workflow/visit-applies/${id}`),
  updateApply: (id, data) => request.put(`/workflow/visit-applies/${id}`, data),
  cancelApply: (id, reason) => request.post(`/workflow/visit-applies/${id}/cancel`, null, { params: { reason } }),
  hostPending: (params) => request.get('/workflow/host/pending', { params }),
  hostConfirm: (id, data) => request.post(`/workflow/host/${id}/confirm`, data),
  hostReject: (id, data) => request.post(`/workflow/host/${id}/reject`, data),
  departmentPending: (params) => request.get('/workflow/department/pending', { params }),
  departmentApprove: (id, data) => request.post(`/workflow/department/${id}/approve`, data),
  departmentReject: (id, data) => request.post(`/workflow/department/${id}/reject`, data),
  getPassCode: (params) => request.get('/workflow/pass-codes', { params }),
  verifyGate: (data) => request.post('/workflow/gate/verify', data),
  entry: (data) => request.post('/workflow/access/entry', data),
  exit: (data) => request.post('/workflow/access/exit', data),
  overtime: (params) => request.get('/workflow/access/overtime', { params })
}