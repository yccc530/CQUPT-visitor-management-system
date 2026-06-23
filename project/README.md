# 重庆邮电大学智慧访客预约与出入校管理系统

本项目是数据库原理课程设计大作业，主题为“重庆邮电大学智慧访客预约与出入校管理系统”。系统围绕校外访客预约、校内被访人确认、部门审批、通行凭证生成、门岗核验、入校登记、离校登记、访问记录归档、黑名单拦截和统计报表分析等流程，完成数据库设计、前后端实现、自动截图和课程设计报告自动生成。

## 技术栈

| 层级 | 技术 |
|---|---|
| 前端 | Vue 3、Vite、Element Plus、Axios、Vue Router、ECharts |
| 后端 | Spring Boot 3、MyBatis Plus、Spring Security、JWT、Swagger/OpenAPI |
| 数据库 | MySQL 8，InnoDB，utf8mb4 |
| 自动截图 | Playwright，系统 Chrome 兜底 |
| 报告生成 | Markdown、Mermaid、Python、python-docx |
| 构建运行 | JDK 17、Maven 3.9+、Node.js/npm |

## 目录结构

```text
project/
├── backend/                         Spring Boot 后端项目
├── frontend/                        Vue 3 前端项目
├── database/                        数据库脚本和表设计
│   ├── create_database.sql
│   ├── schema.sql
│   ├── seed.sql
│   ├── query_examples.sql
│   └── table_design.md
├── diagrams/                        Mermaid 设计图源码
├── docs/                            课程设计文档、报告、运行说明
│   ├── report_parts/
│   ├── 重庆邮电大学访客管理系统设计报告.md
│   ├── 重庆邮电大学访客管理系统设计报告.docx
│   ├── API接口说明.md
│   ├── 系统测试说明.md
│   ├── 测试用例.md
│   ├── 运行说明.md
│   ├── 提交说明.md
│   └── 最终检查清单.md
├── screenshots/                     自动截图结果和 manifest
├── scripts/                         自动化脚本
│   ├── init_database.sh
│   ├── run_backend.sh
│   ├── run_frontend.sh
│   ├── capture_screenshots.js
│   ├── generate_report.py
│   └── run_all.sh
├── codex-skills/                    本项目复用的 Codex Skills
├── bug_fix_log.md
├── PROJECT_PLAN.md
├── TODO.md
└── README.md
```

## 数据库初始化

确保 MySQL 已启动，并默认存在 `root / root` 账号。进入 `project/` 后执行：

```bash
bash scripts/init_database.sh
```

脚本会依次执行：

```text
database/create_database.sql
database/schema.sql
database/seed.sql
```

可通过环境变量覆盖数据库连接：

```bash
DB_HOST=localhost DB_PORT=3306 DB_USERNAME=root DB_PASSWORD=root bash scripts/init_database.sh
```

初始化后，演示数据包含 13 个系统用户、24 条预约申请、13 条出入校记录、4 条黑名单记录和 20 条操作日志，能够支撑全部核心页面和统计图表展示。

## 后端启动

后端需要 JDK 17 和 Maven 3.9+。

```bash
cd project
bash scripts/run_backend.sh
```

脚本会先执行 `mvn -DskipTests package`，然后运行：

```bash
java -jar backend/target/cqupt-visitor-backend-0.1.0.jar
```

后端默认地址：

```text
http://127.0.0.1:8080
```

Swagger 页面：

```text
http://127.0.0.1:8080/swagger-ui.html
```

## 前端启动

前端需要 Node.js 和 npm。

```bash
cd project
bash scripts/run_frontend.sh
```

前端默认地址：

```text
http://127.0.0.1:5173/login
```

前端接口代理配置位于 `frontend/vite.config.js`，开发环境通过 `/api` 代理到后端 `http://127.0.0.1:8080`。

## 自动截图

先确保数据库、后端和前端可运行，然后执行：

```bash
cd project
node scripts/capture_screenshots.js
```

也可以使用一键脚本自动启动后端和前端并截图：

```bash
cd project
bash scripts/run_all.sh
```

截图会保存到 `screenshots/`，并生成 `screenshots/manifest.json`。当前已生成 19 张截图，包括登录页、首页驾驶舱、访客预约、我的预约、待确认、部门审批、门岗核验、入校登记、离校登记、当前在校、超时未离校、黑名单、访客记录、统计报表、用户管理、角色权限、部门管理、校门管理和系统日志。

如果 Playwright Chromium 下载较慢，脚本会自动使用系统 Chrome；也可通过 `CHROME_PATH` 指定浏览器路径。

## 自动生成报告

报告生成脚本会读取 `docs/report_parts/`、`docs/diagrams.md`、`database/`、`screenshots/`、`docs/API接口说明.md`、`docs/系统测试说明.md` 和 `docs/测试用例.md`。

```bash
cd project
python scripts/generate_report.py
```

输出文件：

```text
docs/重庆邮电大学访客管理系统设计报告.md
docs/重庆邮电大学访客管理系统设计报告.docx
```

Word 导出依赖：

```bash
python -m pip install python-docx
```

## 测试账号

所有账号默认密码均为 `123456`。

| 角色 | 账号 | 用途 |
|---|---|---|
| 系统管理员 | admin | 访问全部功能，维护用户、角色、部门、校门、黑名单、日志等 |
| 访客 | visitor01 | 提交预约、查看我的预约和通行凭证 |
| 被访人 | teacher01 | 处理本人相关待确认预约 |
| 部门审批人员 | approver01 | 审批本部门预约申请 |
| 门岗安保人员 | guard01 | 门岗核验、入校登记、离校登记、当前在校和超时查询 |
| 校级管理人员 | manager01 | 查看全校访客记录和统计报表 |

## 核心功能

- 登录认证、JWT Token、退出登录、当前用户信息。
- RBAC 角色权限、菜单权限和接口权限控制。
- 访客预约申请、我的预约、预约详情、取消和修改未审批预约。
- 黑名单手机号/证件号检查和风险访客管理。
- 被访人确认或拒绝预约。
- 部门审批通过或拒绝，审批通过后生成通行凭证。
- 门岗按预约编号、手机号、证件号或通行码核验。
- 入校登记、离校登记、当前在校访客、超时未离校访客。
- 用户、角色权限、部门、校门、字典、系统日志管理。
- 今日访客、本周/本月访客、当前在校、超时未离校、部门排行、校门通行、访问趋势和审批通过率统计。
- Playwright 自动截图和 Markdown/Word 报告自动生成。

## 常见问题

1. `mysql: command not found`  
   将 MySQL Server 的 `bin` 目录加入 PATH，或使用已配置 PATH 的终端运行脚本。

2. 后端无法连接数据库  
   检查 MySQL 是否启动，确认 `DB_HOST`、`DB_PORT`、`DB_USERNAME`、`DB_PASSWORD` 是否正确。

3. `mvn` 不可用  
   安装 Maven 3.9+，或保留项目本地 `project/.tools/apache-maven-3.9.9`。该目录已被 `.gitignore` 忽略，不作为提交内容。

4. 前端打开后接口 404 或 401  
   确认后端已启动，前端 `/api` 代理指向 `http://127.0.0.1:8080`，并使用正确账号登录。

5. Playwright 下载 Chromium 失败  
   可安装系统 Chrome，并设置 `CHROME_PATH`。截图脚本已经支持系统 Chrome 兜底。

6. Word 报告无法生成  
   执行 `python -m pip install python-docx` 后重新运行 `python scripts/generate_report.py`。

## 推荐提交流程

```bash
cd project
bash scripts/init_database.sh
mvn -f backend/pom.xml -DskipTests package
cd frontend && npm run build && cd ..
bash scripts/run_all.sh
python scripts/generate_report.py
```

执行完成后，确认 `docs/重庆邮电大学访客管理系统设计报告.md`、`docs/重庆邮电大学访客管理系统设计报告.docx`、`screenshots/manifest.json` 和 19 张截图均已生成。