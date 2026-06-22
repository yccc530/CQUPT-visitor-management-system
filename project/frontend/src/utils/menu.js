import {
  DataBoard, Calendar, Tickets, Bell, Finished, Aim, Promotion, SwitchButton,
  UserFilled, WarningFilled, Search, TrendCharts, User, Key, OfficeBuilding,
  Location, Document
} from '@element-plus/icons-vue'

export const menuItems = [
  { title: '首页驾驶舱', path: '/dashboard', icon: DataBoard, roles: ['ADMIN', 'SCHOOL_MANAGER', 'HOST', 'DEPT_APPROVER', 'GATE_GUARD', 'VISITOR'] },
  { title: '访客预约', path: '/visit/apply', icon: Calendar, roles: ['ADMIN', 'VISITOR'] },
  { title: '我的预约', path: '/visit/mine', icon: Tickets, roles: ['ADMIN', 'SCHOOL_MANAGER', 'HOST', 'DEPT_APPROVER', 'VISITOR'] },
  { title: '待确认预约', path: '/approval/host', icon: Bell, roles: ['ADMIN', 'HOST'] },
  { title: '部门审批', path: '/approval/department', icon: Finished, roles: ['ADMIN', 'DEPT_APPROVER'] },
  { title: '门岗核验', path: '/gate/verify', icon: Aim, roles: ['ADMIN', 'GATE_GUARD'] },
  { title: '入校登记', path: '/access/entry', icon: Promotion, roles: ['ADMIN', 'GATE_GUARD'] },
  { title: '离校登记', path: '/access/exit', icon: SwitchButton, roles: ['ADMIN', 'GATE_GUARD'] },
  { title: '当前在校访客', path: '/access/current', icon: UserFilled, roles: ['ADMIN', 'SCHOOL_MANAGER', 'GATE_GUARD'] },
  { title: '超时未离校', path: '/access/overtime', icon: WarningFilled, roles: ['ADMIN', 'SCHOOL_MANAGER', 'GATE_GUARD'] },
  { title: '黑名单管理', path: '/security/blacklist', icon: WarningFilled, roles: ['ADMIN'] },
  { title: '访客记录查询', path: '/records', icon: Search, roles: ['ADMIN', 'SCHOOL_MANAGER', 'HOST', 'DEPT_APPROVER'] },
  { title: '统计报表', path: '/reports', icon: TrendCharts, roles: ['ADMIN', 'SCHOOL_MANAGER'] },
  { title: '用户管理', path: '/system/users', icon: User, roles: ['ADMIN'] },
  { title: '角色权限管理', path: '/system/roles', icon: Key, roles: ['ADMIN'] },
  { title: '部门管理', path: '/system/departments', icon: OfficeBuilding, roles: ['ADMIN'] },
  { title: '校门管理', path: '/system/gates', icon: Location, roles: ['ADMIN'] },
  { title: '系统日志', path: '/system/logs', icon: Document, roles: ['ADMIN'] }
]

export function visibleMenus(roles = []) {
  if (!roles.length) return menuItems
  return menuItems.filter((item) => item.roles.some((role) => roles.includes(role)))
}

export function pageTitleByPath(path) {
  return menuItems.find((item) => item.path === path)?.title || '业务工作台'
}