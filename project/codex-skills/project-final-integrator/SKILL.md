---
name: project-final-integrator
description: "用于最后整合数据库课程设计项目，检查代码、数据库、接口、前端调用、图表、文档和报告是否一致，并生成 README、运行说明、最终检查清单和提交说明。Use when the user asks “最终整合项目”, “检查项目完整性”, “整理提交版本”, or “生成README”."
---

# Project Final Integrator

## Purpose
Perform final consistency checks and prepare a clean submission version for a database course project.

## Inputs
- Entire project repository.
- SQL scripts, backend, frontend, docs, diagrams, tests, and final report.
- Submission requirements.

## Workflow
1. Check required directories and deliverable files.
2. Compare database tables and fields with backend entities, DTOs, and mappers.
3. Compare backend APIs with frontend calls.
4. Compare report table names, field names, SQL snippets, and diagram labels with actual project files.
5. Check diagrams against implemented modules and database design.
6. Run available build/test commands or document why they cannot run.
7. Generate README, run instructions, final checklist, and submission notes.

## Outputs
- `README.md`
- `docs/最终检查清单.md`
- `docs/运行说明.md`
- `docs/提交说明.md`

## Quality Checklist
- Ensure the project can be run by following README steps.
- Remove contradictions among SQL scripts, backend, frontend, diagrams, and report.
- Make final deliverables clear and course-submission ready.
- Preserve user changes and avoid unrelated rewrites.
- State remaining risks explicitly.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Treat `database/`, `backend/`, `frontend/`, `docs/`, and final report as the minimum submission set. Verify visitor workflow, RBAC, statistics, and test-account documentation before declaring the project ready.

## References
Read `references/final_check_template.md` when generating final checklists or README/run instructions.
