# 重庆邮电大学智慧访客预约与出入校管理系统

本项目是数据库原理课程设计大作业，主题为“重庆邮电大学智慧访客预约与出入校管理系统”。系统围绕访客预约、黑名单检查、被访人确认、部门审批、通行凭证生成、门岗核验、入校登记、离校登记、访问记录归档、统计报表和课程报告生成等流程建设，形成一个包含数据库设计、后端接口、前端页面、自动截图和 LaTeX 报告的完整可运行数据库应用。

## 技术栈

| 层级 | 技术 |
|---|---|
| 前端 | Vue 3、Vite、Element Plus、Axios、Vue Router、ECharts |
| 后端 | Spring Boot 3、MyBatis Plus、Spring Security、JWT、Swagger/OpenAPI |
| 数据库 | MySQL 8、InnoDB、utf8mb4 |
| 自动截图 | Playwright、系统 Chrome/Chromium |
| 报告 | LaTeX、XeLaTeX、ctex、listings、longtable |
| 自动化脚本 | Bash、Node.js、Python |

## 目录结构

```text
project/
├── backend/                         Spring Boot 后端项目
├── frontend/                        Vue 3 前端项目
├── database/                        数据库脚本和表设计
│   ├── create_database.sql
│   ├── schema.sql
│   ├── seed.sql
│   ├── demo_seed.sql
│   ├── query_examples.sql
│   └── table_design.md
├── diagrams/                        Mermaid / Graphviz 设计图源文件
├── docs/                            课程设计文档、说明和检查清单
├── report-latex/                    LaTeX 课程设计报告
│   ├── main.tex
│   ├── main.pdf
│   ├── chapters/
│   ├── figures/
│   ├── listings/
│   └── styles/
├── screenshots/                     自动截图结果和 manifest.json
├── scripts/                         初始化、启动、截图和报告脚本
├── codex-skills/                    本项目复用的 Codex Skills
├── bug_fix_log.md
├── PROJECT_PLAN.md
├── TODO.md
└── README.md
```

## 数据库初始化

确保 MySQL 已启动，并默认存在 `root / root` 账号。进入仓库根目录后执行：

```bash
cd project
bash scripts/init_database.sh
```

脚本会依次执行 `database/create_database.sql`、`database/schema.sql`、`database/seed.sql`。可通过环境变量覆盖数据库连接：

```bash
DB_HOST=localhost DB_PORT=3306 DB_USERNAME=root DB_PASSWORD=root bash scripts/init_database.sh
```

当前演示数据已经过初始化验证：13 个部门、7 个校门、31 个系统用户、140 个访客、220 条预约申请、450 条审批记录、150 条通行凭证、125 条出入校记录、10 条黑名单、70 条车辆、90 条随行人员、360 条操作日志和 120 条通知消息。

## 后端启动

后端需要 JDK 17 和 Maven 3.9+。

```bash
cd project
bash scripts/run_backend.sh
```

也可以直接打包验证：

```bash
mvn -f backend/pom.xml -DskipTests package
```

后端默认地址为 `http://127.0.0.1:8080`，Swagger 页面为 `http://127.0.0.1:8080/swagger-ui.html`。

## 前端启动

前端需要 Node.js 和 npm。

```bash
cd project
bash scripts/run_frontend.sh
```

也可以直接构建验证：

```bash
npm --prefix frontend run build
```

前端默认地址为 `http://127.0.0.1:5173/login`，开发环境通过 `/api` 代理到后端 `http://127.0.0.1:8080`。

## 自动截图

先确保数据库、后端和前端可运行，然后执行：

```bash
cd project
node scripts/capture_screenshots.js
```

也可以使用一键脚本自动初始化数据库、启动服务、截图并生成报告：

```bash
cd project
bash scripts/run_all.sh
```

截图保存到 `screenshots/`，清单保存到 `screenshots/manifest.json`。当前已生成 19 张截图，覆盖登录页、首页驾驶舱、访客预约、我的预约、待确认、部门审批、门岗核验、入校登记、离校登记、当前在校、超时未离校、黑名单、访客记录、统计报表、用户管理、角色权限、部门管理、校门管理和系统日志。

## LaTeX 报告

LaTeX 报告位于 `report-latex/`，最终 PDF 为：

```text
project/report-latex/main.pdf
```

重新生成 LaTeX 内容：

```bash
cd project
python scripts/generate_latex_report.py
```

编译 PDF：

```bash
cd project/report-latex
xelatex main.tex
xelatex main.tex
```

Windows 也可以执行 `build.bat`。报告结构严格对应数据库原理课程设计要求：系统定义、需求分析、系统设计、详细设计、系统实现与测试、总结。

## 测试账号

所有账号默认密码均为 `123456`。

| 角色 | 账号 | 用途 |
|---|---|---|
| 系统管理员 | admin | 访问全部功能，维护用户、角色、部门、校门、黑名单和日志 |
| 被访人 | teacher01 | 处理本人相关待确认预约 |
| 部门审批人员 | approver01 | 审批本部门预约申请 |
| 门岗安保人员 | guard01 | 门岗核验、入校登记、离校登记、当前在校和超时查询 |
| 校级管理人员 | manager01 | 查看全校访客记录和统计报表 |
| 访客 | visitor01 | 提交预约、查看预约状态和通行凭证 |

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
- 今日访客、本周访客、本月访客、当前在校、超时未离校、部门排行、校门通行、访问趋势和审批通过率统计。
- Playwright 自动截图和 LaTeX 课程设计报告生成。

## 常见问题

1. `mysql: command not found`：将 MySQL Server 的 `bin` 目录加入 PATH，或使用已配置 PATH 的终端运行脚本。
2. 后端无法连接数据库：检查 MySQL 是否启动，确认 `DB_HOST`、`DB_PORT`、`DB_USERNAME`、`DB_PASSWORD` 是否正确。
3. `mvn` 不可用：安装 Maven 3.9+，或使用项目本地 `project/.tools/apache-maven-3.9.9`。该目录被 `.gitignore` 忽略，不作为提交内容。
4. 前端打开后接口 404 或 401：确认后端已启动，前端 `/api` 代理指向 `http://127.0.0.1:8080`，并使用正确账号登录。
5. Playwright 无法下载 Chromium：可安装系统 Chrome，并通过 `CHROME_PATH` 指定浏览器路径。截图脚本已支持系统 Chrome 兜底。
6. LaTeX 无法编译：安装 TeX Live 或 MiKTeX，确认 `xelatex` 可用。详见 `docs/LaTeX报告编译说明.md`。

## 推荐提交前验证流程

```bash
cd project
bash scripts/init_database.sh
mvn -f backend/pom.xml -DskipTests package
npm --prefix frontend run build
bash scripts/run_all.sh
python scripts/generate_latex_report.py
cd report-latex && xelatex main.tex && xelatex main.tex
```

提交前重点确认 `database/`、`backend/`、`frontend/`、`diagrams/`、`screenshots/`、`scripts/`、`report-latex/`、`docs/`、`README.md` 和 `PROJECT_PLAN.md` 均存在，且 `report-latex/main.pdf` 已生成。