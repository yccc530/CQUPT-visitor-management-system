const fs = require('fs');
const http = require('http');
const path = require('path');
const { spawnSync } = require('child_process');

const projectRoot = path.resolve(__dirname, '..');
const screenshotDir = path.join(projectRoot, 'screenshots');
const latexFigureDir = path.join(projectRoot, 'report-latex', 'figures');
const frontendUrl = process.env.FRONTEND_URL || 'http://127.0.0.1:5173';
const backendUrl = process.env.BACKEND_URL || 'http://127.0.0.1:8080';
const headless = process.env.PLAYWRIGHT_HEADLESS !== 'false';
const shouldInitDatabase = process.env.INIT_DATABASE_BEFORE_SCREENSHOTS !== 'false';
const viewport = {
  width: Number(process.env.SCREENSHOT_WIDTH || 1440),
  height: Number(process.env.SCREENSHOT_HEIGHT || 900)
};

const accounts = {
  admin: { username: 'admin', password: '123456' },
  visitor: { username: 'visitor01', password: '123456' },
  host: { username: 'teacher01', password: '123456' },
  approver: { username: 'approver01', password: '123456' },
  guard: { username: 'guard01', password: '123456' },
  manager: { username: 'manager01', password: '123456' }
};

const screenshotPlan = [
  { id: 'login', code: '01_login', title: '登录页', route: '/login', role: 'anonymous', fileName: '01_login.png', chapter: '系统实现与测试', description: '展示系统标题、登录表单、测试账号提示和校园蓝视觉风格。' },
  { id: 'dashboard', code: '02_dashboard', title: '首页统计驾驶舱', route: '/dashboard', role: 'admin', fileName: '02_dashboard.png', chapter: '系统实现与测试', description: '展示今日访客数、当前在校访客、超时未离校、部门访客排行、校门通行统计等核心指标。' },
  { id: 'visitor_apply', code: '03_visitor_apply', title: '访客预约申请页', route: '/visit/apply', role: 'visitor', fileName: '03_visitor_apply.png', chapter: '系统实现与测试', description: '展示访客填写来访事由、被访人、访问时间、车辆和随行人员等预约申请信息。' },
  { id: 'my_apply', code: '04_my_apply', title: '我的预约页', route: '/visit/mine', role: 'admin', fileName: '04_my_apply.png', chapter: '系统实现与测试', description: '展示预约记录、审批状态和访问状态，便于说明访客端状态查询功能。' },
  { id: 'host_confirm', code: '05_host_confirm', title: '被访人待确认页', route: '/approval/host', role: 'admin', fileName: '05_host_confirm.png', chapter: '系统实现与测试', description: '展示被访人确认或拒绝预约申请的业务界面。' },
  { id: 'department_approval', code: '06_department_approval', title: '部门审批页', route: '/approval/department', role: 'admin', fileName: '06_department_approval.png', chapter: '系统实现与测试', description: '展示部门审批人员处理预约申请、填写审批意见和查看审批状态的功能。' },
  { id: 'gate_check', code: '07_gate_check', title: '门岗核验页', route: '/gate/verify', role: 'guard', fileName: '07_gate_check.png', chapter: '系统实现与测试', description: '展示门岗按预约编号、手机号、证件号或通行码进行访客核验的界面。' },
  { id: 'enter_register', code: '08_enter_register', title: '入校登记页', route: '/access/entry', role: 'guard', fileName: '08_enter_register.png', chapter: '系统实现与测试', description: '展示核验通过后记录入校时间、校门和安保人员的登记页面。' },
  { id: 'leave_register', code: '09_leave_register', title: '离校登记页', route: '/access/exit', role: 'guard', fileName: '09_leave_register.png', chapter: '系统实现与测试', description: '展示已入校访客离校登记和访问状态更新功能。' },
  { id: 'current_visitor', code: '10_current_visitor', title: '当前在校访客页', route: '/access/current', role: 'guard', fileName: '10_current_visitor.png', chapter: '系统实现与测试', description: '展示当前已入校但尚未离校的访客列表。' },
  { id: 'overtime_visitor', code: '11_overtime_visitor', title: '超时未离校访客页', route: '/access/overtime', role: 'guard', fileName: '11_overtime_visitor.png', chapter: '系统实现与测试', description: '展示超过预计离校时间仍未离校的访客及风险提示。' },
  { id: 'blacklist', code: '12_blacklist', title: '黑名单管理页', route: '/security/blacklist', role: 'admin', fileName: '12_blacklist.png', chapter: '系统实现与测试', description: '展示身份异常、多次超时、虚假预约等风险访客黑名单管理。' },
  { id: 'visit_record', code: '13_visit_record', title: '访客记录查询页', route: '/records', role: 'manager', fileName: '13_visit_record.png', chapter: '系统实现与测试', description: '展示按访客、部门、时间和状态查询全校访客记录的功能。' },
  { id: 'statistics', code: '14_statistics', title: '统计报表页', route: '/reports', role: 'manager', fileName: '14_statistics.png', chapter: '系统实现与测试', description: '展示近七天访客趋势、部门访客排行、校门通行统计、审批状态和黑名单风险等图表。' },
  { id: 'user_manage', code: '15_user_manage', title: '用户管理页', route: '/system/users', role: 'admin', fileName: '15_user_manage.png', chapter: '系统实现与测试', description: '展示系统用户账号、角色类型、所属部门和账号状态管理。' },
  { id: 'role_permission', code: '16_role_permission', title: '角色权限管理页', route: '/system/roles', role: 'admin', fileName: '16_role_permission.png', chapter: '系统实现与测试', description: '展示系统角色和权限配置，支撑 RBAC 权限控制。' },
  { id: 'department_manage', code: '17_department_manage', title: '部门管理页', route: '/system/departments', role: 'admin', fileName: '17_department_manage.png', chapter: '系统实现与测试', description: '展示学院、职能部门等组织结构基础数据维护。' },
  { id: 'gate_manage', code: '18_gate_manage', title: '校门管理页', route: '/system/gates', role: 'admin', fileName: '18_gate_manage.png', chapter: '系统实现与测试', description: '展示腾飞门、北门、崇文门等校门基础数据维护。' },
  { id: 'system_log', code: '19_system_log', title: '系统日志页', route: '/system/logs', role: 'admin', fileName: '19_system_log.png', chapter: '系统实现与测试', description: '展示登录、预约、审批、出入校登记、黑名单维护和报表查询等关键操作日志。' }
];

async function main() {
  fs.mkdirSync(screenshotDir, { recursive: true });
  fs.mkdirSync(latexFigureDir, { recursive: true });

  if (shouldInitDatabase) {
    initDatabase();
  }

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
      await waitForReady(page, item);
      await preparePage(page, item);
      const filePath = path.join(screenshotDir, item.fileName);
      await page.screenshot({ path: filePath, fullPage: true });
      const latexPath = path.join(latexFigureDir, item.fileName);
      fs.copyFileSync(filePath, latexPath);
      manifest.push(toManifestItem(item, 'SUCCESS'));
      console.log(`Captured ${item.fileName}`);
    }
  } finally {
    await browser.close();
  }

  fs.writeFileSync(path.join(screenshotDir, 'manifest.json'), JSON.stringify(manifest, null, 2), 'utf8');
  fs.writeFileSync(path.join(latexFigureDir, 'screenshot_manifest.json'), JSON.stringify(manifest, null, 2), 'utf8');
}

function initDatabase() {
  const script = path.join(projectRoot, 'scripts', 'init_database.sh');
  if (!fs.existsSync(script)) {
    console.warn('未找到数据库初始化脚本，跳过初始化。');
    return;
  }
  console.log('Initializing demo database before screenshots...');
  const result = spawnSync('bash', [script], {
    cwd: projectRoot,
    stdio: 'inherit',
  });
  if (result.status !== 0) {
    throw new Error('数据库初始化失败，请检查 MySQL 服务、账号密码和 Git Bash 环境。');
  }
}

async function getPage(browser, contexts, role) {
  if (role === 'anonymous') {
    const context = await browser.newContext({ viewport });
    return context.newPage();
  }
  if (contexts.has(role)) {
    return contexts.get(role);
  }
  const account = accounts[role];
  if (!account) throw new Error(`Unknown role ${role}`);
  const context = await browser.newContext({ viewport });
  const page = await context.newPage();
  await page.goto(`${frontendUrl}/login`, { waitUntil: 'networkidle' });
  await page.locator('input').nth(0).fill(account.username);
  await page.locator('input').nth(1).fill(account.password);
  await page.locator('button.el-button--primary').first().click();
  await page.waitForURL(/dashboard/, { timeout: 20000 });
  await waitForReady(page, { id: 'dashboard' });
  contexts.set(role, page);
  return page;
}

async function waitForReady(page, item) {
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('#app', { timeout: 20000 });
  if (!['login', 'visitor_apply', 'gate_check'].includes(item.id)) {
    await page.waitForSelector('.el-table, .chart-box, .metric-card', { timeout: 15000 }).catch(() => {});
  }
  await page.waitForTimeout(1400);
}

async function preparePage(page, item) {
  if (item.id === 'gate_check') {
    const inputs = await page.locator('input').all();
    if (inputs.length) {
      await inputs[0].fill('PC100001').catch(() => {});
    }
    await page.waitForTimeout(500);
  }
  await page.evaluate(() => {
    document.querySelectorAll('.el-table__body-wrapper').forEach((el) => { el.scrollTop = 0; });
    window.scrollTo(0, 0);
  });
}

function toManifestItem(item, status) {
  const file = `screenshots/${item.fileName}`;
  const latexFile = `figures/${item.fileName}`;
  return {
    id: item.id,
    screenshotCode: item.code,
    title: item.title,
    pageName: item.title,
    routePath: item.route,
    roleCode: item.role.toUpperCase(),
    file,
    filePath: file,
    latexFile,
    chapter: item.chapter,
    reportSection: item.chapter,
    description: item.description,
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
    req.setTimeout(10000, () => req.destroy(new Error(`${name} 未启动或访问超时：${url}`)));
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
    return chromium.launch({ headless: isHeadless, executablePath: chromePath });
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