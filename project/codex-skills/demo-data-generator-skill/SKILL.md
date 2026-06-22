---
name: demo-data-generator-skill
description: "用于生成高质量演示测试数据和 MySQL seed 数据，覆盖部门、用户、角色权限、访客、车辆、随行人员、预约、审批、通行码、出入校、黑名单、通知、日志、截图记录和报告记录。Use when the user asks “生成演示数据”, “生成测试数据”, “填充报表数据”, or “让页面有真实数据”."
---

# Demo Data Generator Skill

## Purpose
Generate realistic, coherent seed data that supports every frontend page, backend workflow, SQL query example, statistics chart, screenshot, and final report section.

## Inputs
- Current `database/schema.sql` or table design.
- Required roles, pages, workflow states, and statistics metrics.
- Desired data volume and date range.
- Password hashing/authentication rules, if seed accounts are needed.

## Workflow
1. Inspect schema fields, foreign keys, status values, and enum/dict items.
2. Define seed accounts for visitor, host, department approver, gate security, system administrator, and school manager.
3. Generate departments, campus gates, permissions, roles, users, visitors, vehicles, and companions.
4. Generate visit applications across all statuses and date ranges.
5. Generate approval records, pass codes, access records, blacklist records, notices, and operation logs.
6. Ensure statistics pages have meaningful trends, rankings, rates, and edge cases.
7. Keep data internally consistent: every foreign key exists, time order is valid, and status matches records.
8. Output executable SQL and documentation of test accounts and demo scenarios.

## Outputs
- Updated `database/seed.sql`.
- Optional `database/demo_seed_notes.md`.
- Test account descriptions for README and testing docs.
- Demo scenario list for screenshots and report.

## Quality Checklist
- Cover every required page and statistic.
- Include normal, pending, rejected, cancelled, entered, exited, overdue, and abnormal cases.
- Include blacklisted visitors and blocked workflow examples.
- Ensure all foreign keys and unique constraints are respected.
- Ensure generated data can produce attractive charts and meaningful screenshots.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Include departments such as 计算机科学与技术学院, 软件工程学院, 自动化学院, 通信与信息工程学院, 教务处, 保卫处. Include gates such as 南门, 北门, 东门, 西门. Use realistic Chinese names, phone numbers, ID/passport placeholders, car plates, visit reasons, and campus appointment times.

## References
Read `references/demo_data_template.md` before writing seed SQL or demo data notes.