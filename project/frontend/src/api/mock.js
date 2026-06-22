export const mockApplies = [
  { id: 1, applyNo: 'VA202606220001', visitorId: 1, visitorName: '王小明', phone: '13900010001', hostUserId: 2, departmentId: 2, departmentName: '计算机科学与技术学院', visitReason: '参加校企合作交流', planStartTime: '2026-06-22T09:00:00', planEndTime: '2026-06-22T11:00:00', applyStatus: 'APPROVED', accessStatus: 'EXITED', companionCount: 1 },
  { id: 2, applyNo: 'VA202606220002', visitorId: 2, visitorName: '李华', phone: '13900010002', hostUserId: 2, departmentId: 2, departmentName: '计算机科学与技术学院', visitReason: '讨论学生实习基地建设', planStartTime: '2026-06-22T14:00:00', planEndTime: '2026-06-22T18:00:00', applyStatus: 'APPROVED', accessStatus: 'ENTERED', companionCount: 0 },
  { id: 3, applyNo: 'VA202606220003', visitorId: 3, visitorName: '赵敏', phone: '13900010003', hostUserId: 6, departmentId: 3, departmentName: '自动化学院', visitReason: '实验室设备调试', planStartTime: '2026-06-22T08:00:00', planEndTime: '2026-06-22T10:00:00', applyStatus: 'APPROVED', accessStatus: 'OVERTIME', companionCount: 2 },
  { id: 4, applyNo: 'VA202606220004', visitorId: 4, visitorName: '陈晨', phone: '13900010004', hostUserId: 2, departmentId: 2, departmentName: '计算机科学与技术学院', visitReason: '参观智慧校园平台', planStartTime: '2026-06-23T09:00:00', planEndTime: '2026-06-23T11:00:00', applyStatus: 'PENDING_HOST', accessStatus: 'NOT_ENTERED', companionCount: 0 },
  { id: 7, applyNo: 'VA202606220007', visitorId: 2, visitorName: '李华', phone: '13900010002', hostUserId: 2, departmentId: 2, departmentName: '计算机科学与技术学院', visitReason: '补充提交合作协议材料', planStartTime: '2026-06-23T09:00:00', planEndTime: '2026-06-23T13:00:00', applyStatus: 'PENDING_DEPT', accessStatus: 'NOT_ENTERED', companionCount: 1 }
]

export const mockAccessRecords = [
  { id: 1, applyId: 1, applyNo: 'VA202606220001', visitorName: '王小明', entryGateId: 1, entryGateName: '北校门', exitGateName: '南校门', entryTime: '2026-06-22T09:00:00', exitTime: '2026-06-22T10:40:00', accessStatus: 'EXITED' },
  { id: 2, applyId: 2, applyNo: 'VA202606220002', visitorName: '李华', entryGateId: 1, entryGateName: '北校门', entryTime: '2026-06-22T14:00:00', exitTime: null, accessStatus: 'ENTERED' },
  { id: 3, applyId: 3, applyNo: 'VA202606220003', visitorName: '赵敏', entryGateId: 3, entryGateName: '东区车行门', entryTime: '2026-06-22T08:00:00', exitTime: null, accessStatus: 'OVERTIME' }
]

export const mockBlacklists = [
  { id: 1, visitorId: 5, visitorName: '周杰', idNumber: '500101198705050055', phone: '13900010005', reason: '历史预约存在冒用身份信息记录', level: 'FORBIDDEN', status: 'ACTIVE' },
  { id: 2, visitorId: null, visitorName: '未知访客', idNumber: '500101198001010099', phone: '13900019999', reason: '校门核验多次失败，待人工复核', level: 'WARNING', status: 'ACTIVE' }
]

export const mockUsers = [
  { id: 1, username: 'admin', realName: '系统管理员', phone: '13800000001', departmentId: 5, userType: 'ADMIN', status: 'ENABLED' },
  { id: 2, username: 'teacher01', realName: '张晓峰', phone: '13800000002', departmentId: 2, userType: 'HOST', status: 'ENABLED' },
  { id: 3, username: 'approver01', realName: '李审批', phone: '13800000003', departmentId: 2, userType: 'APPROVER', status: 'ENABLED' },
  { id: 4, username: 'guard01', realName: '王安保', phone: '13800000004', departmentId: 4, userType: 'GUARD', status: 'ENABLED' }
]

export const mockDepartments = [
  { id: 1, deptCode: 'CQUPT', deptName: '重庆邮电大学', phone: '023-62460000', status: 'ENABLED' },
  { id: 2, deptCode: 'CS', deptName: '计算机科学与技术学院', phone: '023-62461001', status: 'ENABLED' },
  { id: 3, deptCode: 'AUTO', deptName: '自动化学院', phone: '023-62461002', status: 'ENABLED' },
  { id: 4, deptCode: 'SECURITY', deptName: '保卫处', phone: '023-62461003', status: 'ENABLED' }
]

export const mockGates = [
  { id: 1, gateCode: 'NORTH_GATE', gateName: '北校门', gateLocation: '南岸校区北侧主入口', gateType: 'NORMAL', status: 'ENABLED' },
  { id: 2, gateCode: 'SOUTH_GATE', gateName: '南校门', gateLocation: '南岸校区南侧入口', gateType: 'PEDESTRIAN', status: 'ENABLED' },
  { id: 3, gateCode: 'EAST_VEHICLE_GATE', gateName: '东区车行门', gateLocation: '东区停车场入口', gateType: 'VEHICLE', status: 'ENABLED' }
]

export const mockLogs = [
  { id: 1, operatorName: '张晓峰', moduleName: '被访人确认', operationType: 'CONFIRM', operationResult: 'SUCCESS', requestUrl: '/api/workflow/host/4/confirm', operationTime: '2026-06-22T10:12:00' },
  { id: 2, operatorName: '李审批', moduleName: '部门审批', operationType: 'APPROVE', operationResult: 'SUCCESS', requestUrl: '/api/workflow/department/7/approve', operationTime: '2026-06-22T10:30:00' },
  { id: 3, operatorName: '王安保', moduleName: '门岗核验', operationType: 'ENTRY', operationResult: 'SUCCESS', requestUrl: '/api/workflow/access/entry', operationTime: '2026-06-22T14:00:00' }
]