# CQUPT Visitor Management System

本仓库用于数据库原理期末大作业，项目主题为“重庆邮电大学智慧访客预约与出入校管理系统”。完整项目位于：

```text
project/
```

主要交付内容包括：

- `project/backend/`：Spring Boot 3 + MyBatis Plus 后端。
- `project/frontend/`：Vue 3 + Vite + Element Plus 前端。
- `project/database/`：MySQL 8 建库、建表、演示数据和典型查询脚本。
- `project/diagrams/`：E-R 图、数据流程图、系统模块图、业务流程图和架构图。
- `project/screenshots/`：Playwright 自动截图和 `manifest.json`。
- `project/report-latex/`：LaTeX 课程设计报告，最终 PDF 为 `project/report-latex/main.pdf`。
- `project/docs/`：需求、设计、API、测试、运行、提交和最终检查文档。

推荐从 `project/README.md` 开始阅读和运行项目。

## 快速验证

```bash
cd project
bash scripts/init_database.sh
mvn -f backend/pom.xml -DskipTests package
npm --prefix frontend run build
python scripts/generate_latex_report.py
cd report-latex && xelatex main.tex && xelatex main.tex
```

测试账号默认密码均为 `123456`：`admin`、`teacher01`、`approver01`、`guard01`、`manager01`、`visitor01`。