const fs = require('fs');
const http = require('http');
const path = require('path');

const projectRoot = path.resolve(__dirname, '..');
const screenshotDir = path.join(projectRoot, 'screenshots');
const frontendUrl = process.env.FRONTEND_URL || 'http://127.0.0.1:5173';
const backendUrl = process.env.BACKEND_URL || 'http://127.0.0.1:8080';
const headless = process.env.PLAYWRIGHT_HEADLESS !== 'false';

const accounts = {
  admin: { username: 'admin', password: '123456' },
  visitor: { username: 'visitor01', password: '123456' },
  host: { username: 'teacher01', password: '123456' },
  approver: { username: 'approver01', password: '123456' },
  guard: { username: 'guard01', password: '123456' },
  manager: { username: 'manager01', password: '123456' }
};

const screenshotPlan = [
  { id: '01_login', pageName: '登录页', route: '/login', role: 'anonymous', fileName: '01_login.png', reportSection: '系统实现与测试', description: '展示系统标题、登录表单和测试账号入口。' },
  { id: '02_dashboard', pageName: '首页统计驾驶舱', route: '/dashboard', role: 'admin', fileName: '02_dashboard.png', reportSection: '主界面截图', description: '展示今日访客、当前在校、超时未离校、审批待处理和统计图表。' },
  { id: '03_visitor_apply', pageName: '访客预约申请页', route: '/visit/apply', role: 'visitor', fileName: '03_visitor_apply.png', reportSection: '模块界面截图', description: '展示访客预约申请表单。' },
  { id: '04_my_apply', pageName: '我的预约页', route: '/visit/mine', role: 'visitor', fileName: '04_my_apply.png', reportSection: '模块界面截图', description: '展示访客本人预约记录和状态。' },
  { id: '05_host_confirm', pageName: '待确认预约页', route: '/approval/host', role: 'host', fileName: '05_host_confirm.png', reportSection: '模块界面截图', description: '展示被访人待确认预约列表。' },
  { id: '06_department_approval', pageName: '部门审批页', route: '/approval/department', role: 'approver', fileName: '06_department_approval.png', reportSection: '模块界面截图', description: '展示部门审批人员处理预约申请。' },
  { id: '07_gate_check', pageName: '门岗核验页', route: '/gate/verify', role: 'guard', fileName: '07_gate_check.png', reportSection: '模块界面截图', description: '展示通行码、预约编号、手机号或证件号核验界面。' },
  { id: '08_enter_register', pageName: '入校登记页', route: '/access/entry', role: 'guard', fileName: '08_enter_register.png', reportSection: '模块界面截图', description: '展示门岗入校登记页面。' },
  { id: '09_leave_register', pageName: '离校登记页', route: '/access/exit', role: 'guard', fileName: '09_leave_register.png', reportSection: '模块界面截图', description: '展示门岗离校登记页面。' },
  { id: '10_current_visitor', pageName: '当前在校访客页', route: '/access/current', role: 'guard', fileName: '10_current_visitor.png', reportSection: '查询页截图', description: '展示当前已入校未离校访客。' },
  { id: '11_overtime_visitor', pageName: '超时未离校访客页', route: '/access/overtime', role: 'guard', fileName: '11_overtime_visitor.png', reportSection: '查询页截图', description: '展示超过计划离校时间仍未离校访客。' },
  { id: '12_blacklist', pageName: '黑名单管理页', route: '/security/blacklist', role: 'admin', fileName: '12_blacklist.png', reportSection: '模块界面截图', description: '展示黑名单管理与风险访客记录。' },
  { id: '13_visit_record', pageName: '访客记录查询页', route: '/records', role: 'manager', fileName: '13_visit_record.png', reportSection: '查询页截图', description: '展示全校访客预约和访问记录查询。' },
  { id: '14_statistics', pageName: '统计报表页', route: '/reports', role: 'manager', fileName: '14_statistics.png', reportSection: '报表页截图', description: '展示访客趋势、部门排行、校门通行和审批通过率。' },
  { id: '15_user_manage', pageName: '用户管理页', route: '/system/users', role: 'admin', fileName: '15_user_manage.png', reportSection: '模块界面截图', description: '展示系统用户管理。' },
  { id: '16_role_permission', pageName: '角色权限管理页', route: '/system/roles', role: 'admin', fileName: '16_role_permission.png', reportSection: '模块界面截图', description: '展示角色权限管理。' },
  { id: '17_department_manage', pageName: '部门管理页', route: '/system/departments', role: 'admin', fileName: '17_department_manage.png', reportSection: '模块界面截图', description: '展示部门基础数据维护。' },
  { id: '18_gate_manage', pageName: '校门管理页', route: '/system/gates', role: 'admin', fileName: '18_gate_manage.png', reportSection: '模块界面截图', description: '展示校门基础数据维护。' },
  { id: '19_system_log', pageName: '系统日志页', route: '/system/logs', role: 'admin', fileName: '19_system_log.png', reportSection: '系统实现与测试', description: '展示关键业务操作日志。' }
];

async function main() {
  fs.mkdirSync(screenshotDir, { recursive: true });
  await assertService(`${frontendUrl}/login`, '前端服务');
  await assertService(`${backendUrl}/swagger-ui.html`, '后端服务');
  const { chromium } = loadPlaywright();
  const browser = await launchBrowser(chromium, headless);
  const contexts = new Map();
  const manifest = [];

  try {
    for (const item of screenshotPlan) {
      const page = await getPage(browser, contexts, item.role);
      await page.goto(`${frontendUrl}${item.route}`, { waitUntil: 'networkidle' });
      await waitForReady(page);
      const filePath = path.join(screenshotDir, item.fileName);
      await page.screenshot({ path: filePath, fullPage: true });
      manifest.push(toManifestItem(item, 'SUCCESS'));
      console.log(`Captured ${item.fileName}`);
    }
  } finally {
    await browser.close();
  }

  fs.writeFileSync(path.join(screenshotDir, 'manifest.json'), JSON.stringify(manifest, null, 2), 'utf8');
}

async function getPage(browser, contexts, role) {
  if (role === 'anonymous') {
    const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    return context.newPage();
  }
  if (contexts.has(role)) {
    return contexts.get(role);
  }
  const account = accounts[role];
  if (!account) throw new Error(`Unknown role ${role}`);
  const context = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const page = await context.newPage();
  await page.goto(`${frontendUrl}/login`, { waitUntil: 'networkidle' });
  await page.locator('input').nth(0).fill(account.username);
  await page.locator('input').nth(1).fill(account.password);
  await page.locator('button.el-button--primary').first().click();
  await page.waitForURL(/dashboard/, { timeout: 15000 });
  await waitForReady(page);
  contexts.set(role, page);
  return page;
}

async function waitForReady(page) {
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('#app', { timeout: 15000 });
  await page.waitForTimeout(800);
}

function toManifestItem(item, status) {
  return {
    screenshotCode: item.id,
    pageName: item.pageName,
    routePath: item.route,
    roleCode: item.role.toUpperCase(),
    filePath: `screenshots/${item.fileName}`,
    description: item.description,
    reportSection: item.reportSection,
    captureTime: new Date().toISOString(),
    status
  };
}

function loadPlaywright() {
  const candidates = [
    'playwright',
    path.join(projectRoot, 'node_modules', 'playwright'),
    path.join(projectRoot, 'frontend', 'node_modules', 'playwright')
  ];
  for (const candidate of candidates) {
    try { return require(candidate); } catch (error) { /* try next */ }
  }
  throw new Error('未检测到 Playwright。请先执行：cd project/frontend && npm install -D playwright && npx playwright install chromium');
}

function assertService(url, name) {
  return new Promise((resolve, reject) => {
    const req = http.get(url, (res) => {
      res.resume();
      if (res.statusCode >= 200 && res.statusCode < 500) resolve();
      else reject(new Error(`${name} 响应异常：${res.statusCode}`));
    });
    req.setTimeout(8000, () => req.destroy(new Error(`${name} 未启动或访问超时：${url}`)));
    req.on('error', reject);
  });
}

async function launchBrowser(chromium, isHeadless) {
  try {
    return await chromium.launch({ headless: isHeadless });
  } catch (error) {
    const chromePath = findSystemChrome();
    if (!chromePath) throw error;
    console.log(`Playwright Chromium not installed, using system Chrome: ${chromePath}`);
    return chromium.launch({
      headless: isHeadless,
      executablePath: chromePath
    });
  }
}

function findSystemChrome() {
  const candidates = [
    process.env.CHROME_PATH,
    'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
    'C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe'
  ].filter(Boolean);
  return candidates.find((item) => fs.existsSync(item));
}
main().catch((error) => {
  console.error(error.message || error);
  process.exit(1);
});