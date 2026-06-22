# 重庆邮电大学智慧访客预约与出入校管理系统项目规划

## 1. 项目背景

高校校园出入管理需要同时兼顾开放交流、校园安全、审批效率和数据留痕。传统访客管理方式多依赖线下登记、电话确认和纸质记录，容易出现预约信息不完整、审批链路不清晰、门岗核验效率低、访客出入记录难统计、黑名单拦截不及时等问题。

本项目以“重庆邮电大学智慧访客预约与出入校管理系统”为主题，围绕数据库原理课程设计要求，设计并实现一个以 MySQL 数据库为核心、前后端分离、支持自动截图和自动报告生成的数据库应用系统。系统重点体现需求分析、数据流程、数据字典、概念结构设计、逻辑结构设计、关系模式、SQL 查询、系统实现与测试之间的一致性。

## 2. 系统目标

1. 建立覆盖访客预约、审批、核验、入校、离校、统计和管理的完整业务闭环。
2. 设计结构清晰、约束完整、可支撑查询统计的 MySQL 8 数据库。
3. 实现 Spring Boot 3 + MyBatis Plus 后端接口，支撑核心业务流程运行。
4. 实现 Vue 3 + Element Plus 前端，提供美观、可演示、可截图的管理界面。
5. 通过 Playwright 自动截取系统运行界面，为课程报告提供证据材料。
6. 通过脚本自动整合文档、图表、SQL 和截图，生成课程设计报告初稿。

## 3. 技术栈

| 层次 | 技术 |
|---|---|
| 前端 | Vue 3 + Vite + Element Plus + Axios + Vue Router + ECharts |
| 后端 | Spring Boot 3 + MyBatis Plus + MySQL + JWT |
| 数据库 | MySQL 8 |
| 图表与图形 | Mermaid，必要时使用 PlantUML |
| 自动截图 | Playwright |
| 报告生成 | Markdown + Mermaid + Python-docx 或 Pandoc |
| 接口文档 | Swagger / Knife4j |

## 4. 用户角色

| 角色 | 说明 |
|---|---|
| 访客 | 校外来访人员，提交预约申请、查询预约状态、查看通行凭证 |
| 被访人 | 校内接待人员，确认或拒绝访客预约 |
| 部门审批人员 | 对本部门被访人的访客申请进行审批 |
| 门岗安保人员 | 核验通行凭证，办理入校登记和离校登记 |
| 系统管理员 | 管理用户、角色权限、部门、校门、黑名单、字典和日志 |
| 校级管理人员 | 查看全校访客记录、统计报表和运行情况 |

## 5. 核心业务流程

访客提交预约申请 → 系统检查黑名单 → 被访人确认 → 部门审批 → 生成通行凭证 → 门岗核验 → 入校登记 → 离校登记 → 访问记录归档 → 查询统计。

该流程对应的核心数据包括访客信息、预约申请、审批记录、通行凭证、出入校记录、黑名单、用户、部门、校门和系统日志。

## 6. 系统模块

| 模块 | 主要功能 | 相关数据 |
|---|---|---|
| 登录认证与权限管理 | 登录、JWT、角色权限、菜单权限、接口权限控制 | sys_user, sys_role, sys_permission |
| 访客预约管理 | 提交预约、查看我的预约、取消预约、查询状态 | visitor, visitor_vehicle, visitor_companion, visit_apply |
| 被访人确认 | 查看待确认预约、确认、拒绝 | visit_apply, approval_record, notice |
| 部门审批 | 部门审批、审批意见记录、审批结果通知 | approval_record, visit_apply |
| 通行凭证管理 | 审批通过后生成通行码，记录有效期和使用状态 | pass_code |
| 门岗核验 | 核验通行码、核验访客身份、检查预约有效性 | pass_code, visit_apply, blacklist |
| 出入校登记 | 入校登记、离校登记、当前在校、超时未离校 | access_record, campus_gate |
| 黑名单管理 | 黑名单新增、解除、查询、拦截提示 | blacklist, visitor |
| 查询统计 | 访客记录查询、部门排行、校门通行、趋势、通过率 | visit_apply, access_record, approval_record |
| 系统管理 | 用户、部门、校门、字典、日志、通知管理 | department, campus_gate, dict_type, dict_item, operation_log |
| 自动截图与报告 | 自动截图、生成截图清单、自动汇总报告 | screenshot_record, report_record |

## 7. 数据库设计计划

数据库设计按课程设计流程推进：

1. 需求分析阶段：明确数据流、数据存储、外部实体、处理过程和核心数据项。
2. 概念结构设计阶段：识别访客、用户、部门、校门、预约、审批、通行码、出入记录、黑名单等实体，并绘制 E-R 图。
3. 逻辑结构设计阶段：将实体和联系转换为 MySQL 关系模式，设计主键、外键、唯一约束、索引、默认值和字段说明。
4. 物理 SQL 设计阶段：生成 `create_database.sql`、`schema.sql`、`seed.sql` 和 `query_examples.sql`。
5. 查询设计阶段：围绕访客历史、待确认预约、当前在校、超时未离校、部门统计、校门通行、黑名单和日志等场景设计典型 SQL。

建议核心表包括：`sys_user`、`sys_role`、`sys_permission`、`sys_user_role`、`sys_role_permission`、`department`、`campus_gate`、`visitor`、`visitor_vehicle`、`visitor_companion`、`visit_apply`、`approval_record`、`pass_code`、`access_record`、`blacklist`、`notice`、`operation_log`、`dict_type`、`dict_item`、`screenshot_record`、`report_record`。

## 8. 后端开发计划

1. 搭建 Spring Boot 3 项目，配置 MySQL、MyBatis Plus、Swagger / Knife4j。
2. 根据数据库表生成 Entity、Mapper、Service、Controller。
3. 实现统一返回结构、统一异常处理、参数校验和日志记录。
4. 实现 JWT 登录认证和 RBAC 权限控制。
5. 实现访客预约、审批、通行码、门岗核验、入校登记、离校登记、黑名单和统计接口。
6. 输出 `docs/API接口说明.md`，保证接口与前端调用一致。

## 9. 前端开发计划

1. 搭建 Vue 3 + Vite 项目，配置 Element Plus、Axios、Vue Router、ECharts。
2. 实现登录页、统一布局、侧边菜单、顶部栏和路由守卫。
3. 实现预约、我的预约、待确认、部门审批、门岗核验、入校登记、离校登记、当前在校、超时未离校等业务页面。
4. 实现黑名单、访客记录、统计报表、用户管理、角色权限、部门管理、校门管理、系统日志等管理页面。
5. 使用校园蓝或科技蓝主题进行 UI 美化，使页面适合演示和截图。

## 10. 自动截图计划

使用 Playwright 编写 `scripts/capture_screenshots.js`：

1. 启动后自动访问前端系统。
2. 使用不同角色测试账号登录。
3. 自动访问登录页、首页驾驶舱、预约页、审批页、门岗核验页、入校登记页、离校登记页、黑名单页、统计报表页、用户管理页和系统日志页。
4. 将截图保存至 `screenshots/`。
5. 生成 `screenshots/manifest.json`，记录截图文件、页面名称、角色、说明和报告章节。

## 11. 自动报告生成计划

使用 `scripts/generate_report.py` 自动读取：

- `docs/report_parts/` 中的阶段文档；
- `database/` 中的建库、建表、初始化和查询 SQL；
- `diagrams/` 与 `docs/diagrams.md` 中的图表；
- `screenshots/manifest.json` 与截图文件；
- 测试文档与接口说明。

最终生成 `docs/重庆邮电大学智慧访客预约与出入校管理系统设计报告.md`，并尽量通过 Pandoc 或 Python-docx 导出 Word 文档。

## 12. 测试计划

| 测试类型 | 重点内容 |
|---|---|
| 数据库测试 | 建库建表、外键约束、初始化数据、典型查询 |
| 后端接口测试 | 登录、权限、预约、审批、核验、出入校、统计 |
| 前端页面测试 | 表单校验、表格查询、分页、弹窗、状态按钮 |
| 业务流程测试 | 从预约到离校的完整流程和异常流程 |
| 权限测试 | 不同角色菜单和接口访问范围 |
| 自动化测试 | Playwright 截图脚本和报告生成脚本 |
| 一致性测试 | 数据库字段、后端 DTO、前端字段、报告描述一致 |

## 13. 项目目录结构

```text
project/
  backend/
  frontend/
  database/
    create_database.sql
    schema.sql
    seed.sql
    query_examples.sql
  docs/
    report_parts/
    diagrams.md
    API接口说明.md
    系统测试说明.md
    重庆邮电大学智慧访客预约与出入校管理系统设计报告.md
  diagrams/
  screenshots/
  scripts/
  codex-skills/
  README.md
  PROJECT_PLAN.md
  TODO.md
```

## 14. 最终交付物

1. 可运行的前端项目 `frontend/`。
2. 可运行的后端项目 `backend/`。
3. MySQL 数据库脚本 `database/*.sql`。
4. 需求、数据字典、E-R 图、逻辑结构、关系模式、接口、测试等设计文档。
5. Mermaid 或 PlantUML 图表源文件。
6. 自动截图脚本和运行截图。
7. 自动报告生成脚本和最终课程设计报告。
8. README、运行说明、提交说明和最终检查清单。
