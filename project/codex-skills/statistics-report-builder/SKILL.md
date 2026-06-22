---
name: statistics-report-builder
description: "用于实现访客统计报表功能，包括统计 SQL、后端统计接口、前端报表页面、趋势数据和报告说明。Use when the user asks “实现统计报表”, “统计访客数量”, “生成报表页面”, or “写统计SQL”."
---

# Statistics Report Builder

## Purpose
Implement statistics reporting from real database tables, then expose the data through backend APIs and frontend report pages.

## Inputs
- Table design and seed data.
- Backend and frontend project structure.
- Required metrics and date range.
- Preferred chart library, if any.

## Workflow
1. Identify source tables and fields for each metric.
2. Write SQL for total, grouped, and trend metrics.
3. Implement backend endpoints with clear DTO/VO response shapes.
4. Build frontend report cards, tables, and charts.
5. Add filters such as date range, department, gate, and status.
6. Check results against seed data and sample queries.
7. Write report explanation and screenshot guidance.

## Outputs
- Statistics SQL.
- Backend statistics interfaces.
- Frontend statistics page.
- `docs/统计报表说明.md`

## Required Metrics
Include today, week, and month visitor counts; counts by department; passage counts by gate; visitors not exited; overtime visitors; and visit trend data.

## Quality Checklist
- Base statistics on actual database tables.
- Keep SQL executable.
- Return frontend-friendly structures.
- Make reports suitable for course-design demonstration screenshots.
- Explain each metric's business meaning and source table.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Use `visit_apply`, `approval_record`, `access_record`, `department`, `gate`, and `blacklist` as primary report sources. Align date filters with appointment time, entry time, or exit time as appropriate.

## References
Read `references/statistics_template.md` when writing metrics, SQL, interfaces, or report notes.
