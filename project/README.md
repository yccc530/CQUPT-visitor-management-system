# 重庆邮电大学智慧访客预约与出入校管理系统

本目录是数据库原理期末大作业的主体项目目录。项目目标是设计并实现一个完整的智慧访客预约与出入校管理系统，最终交付可运行系统、数据库脚本、设计文档、运行截图和课程设计报告。

## 技术栈

- 前端：Vue 3 + Vite + Element Plus + Axios + Vue Router + ECharts
- 后端：Spring Boot 3 + MyBatis Plus + MySQL + JWT
- 数据库：MySQL 8
- 截图：Playwright
- 报告：Markdown + Mermaid + Python-docx 或 Pandoc
- 接口文档：Swagger / Knife4j

## 当前阶段

当前已完成：

1. Skills 检查与补齐。
2. 项目规划与目录初始化。
3. 系统定义、需求分析、数据流程图和数据字典初稿。

## 目录结构

```text
project/
  backend/
  frontend/
  database/
  docs/
    report_parts/
  diagrams/
  screenshots/
  scripts/
  codex-skills/
  PROJECT_PLAN.md
  TODO.md
  README.md
```

## 后续运行说明

后续阶段会补充以下脚本：

- `scripts/init_database.sh`：初始化数据库。
- `scripts/run_backend.sh`：启动后端。
- `scripts/run_frontend.sh`：启动前端。
- `scripts/capture_screenshots.js`：自动截图。
- `scripts/generate_report.py`：自动生成报告。
- `scripts/run_all.sh`：一键启动、截图和生成报告。

目前本文件为初稿，后续会随着数据库、后端、前端和自动化脚本完成而持续完善。
