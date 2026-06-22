import request from '@/utils/request'

export function createCrudApi(basePath) {
  return {
    list: (params) => request.get(basePath, { params }),
    detail: (id) => request.get(`${basePath}/${id}`),
    create: (data) => request.post(basePath, data),
    update: (id, data) => request.put(`${basePath}/${id}`, data),
    remove: (id) => request.delete(`${basePath}/${id}`)
  }
}

export const visitorApi = createCrudApi('/visitors')
export const visitApplyApi = createCrudApi('/visit-applies')
export const accessRecordApi = createCrudApi('/access-records')
export const blacklistApi = createCrudApi('/blacklists')
export const userApi = createCrudApi('/sys-users')
export const roleApi = createCrudApi('/sys-roles')
export const departmentApi = createCrudApi('/departments')
export const gateApi = createCrudApi('/campus-gates')
export const logApi = createCrudApi('/operation-logs')
export const approvalApi = createCrudApi('/approval-records')
export const passCodeApi = createCrudApi('/pass-codes')