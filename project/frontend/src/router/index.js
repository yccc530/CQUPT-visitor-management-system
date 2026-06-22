import { createRouter, createWebHistory } from 'vue-router'
import { getToken, getCurrentUser } from '@/utils/auth'
import { menuItems } from '@/utils/menu'
import AdminLayout from '@/layouts/AdminLayout.vue'

const routes = [
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue'), meta: { public: true, title: '登录' } },
  {
    path: '/',
    component: AdminLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '首页统计驾驶舱' } },
      { path: 'visit/apply', component: () => import('@/views/visit/Apply.vue'), meta: { title: '访客预约申请页' } },
      { path: 'visit/mine', component: () => import('@/views/visit/MyAppointments.vue'), meta: { title: '我的预约页' } },
      { path: 'approval/host', component: () => import('@/views/approval/HostPending.vue'), meta: { title: '待确认预约页' } },
      { path: 'approval/department', component: () => import('@/views/approval/DepartmentApproval.vue'), meta: { title: '部门审批页' } },
      { path: 'gate/verify', component: () => import('@/views/gate/GateVerify.vue'), meta: { title: '门岗核验页' } },
      { path: 'access/entry', component: () => import('@/views/access/Entry.vue'), meta: { title: '入校登记页' } },
      { path: 'access/exit', component: () => import('@/views/access/Exit.vue'), meta: { title: '离校登记页' } },
      { path: 'access/current', component: () => import('@/views/access/CurrentVisitors.vue'), meta: { title: '当前在校访客页' } },
      { path: 'access/overtime', component: () => import('@/views/access/OvertimeVisitors.vue'), meta: { title: '超时未离校访客页' } },
      { path: 'security/blacklist', component: () => import('@/views/security/Blacklist.vue'), meta: { title: '黑名单管理页' } },
      { path: 'records', component: () => import('@/views/records/VisitorRecords.vue'), meta: { title: '访客记录查询页' } },
      { path: 'reports', component: () => import('@/views/reports/Statistics.vue'), meta: { title: '统计报表页' } },
      { path: 'system/users', component: () => import('@/views/system/Users.vue'), meta: { title: '用户管理页' } },
      { path: 'system/roles', component: () => import('@/views/system/Roles.vue'), meta: { title: '角色权限管理页' } },
      { path: 'system/departments', component: () => import('@/views/system/Departments.vue'), meta: { title: '部门管理页' } },
      { path: 'system/gates', component: () => import('@/views/system/Gates.vue'), meta: { title: '校门管理页' } },
      { path: 'system/logs', component: () => import('@/views/system/Logs.vue'), meta: { title: '系统日志页' } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  document.title = `${to.meta.title || '智慧访客系统'} - 重庆邮电大学`
  if (to.meta.public) return true
  if (!getToken()) return '/login'
  const user = getCurrentUser()
  const path = '/' + to.path.replace(/^\//, '')
  const menu = menuItems.find((item) => item.path === path)
  if (menu && user?.roles?.length && !menu.roles.some((role) => user.roles.includes(role))) {
    return '/dashboard'
  }
  return true
})

export default router