---
name: db-course-project-planner
description: "用于数据库课程设计项目总体规划，生成技术路线、目录结构、开发阶段、TODO 清单和最终交付物清单。Use when the user asks “规划数据库课程设计项目”, “规划访客系统项目”, “生成项目计划”, or “确定项目结构”."
---

# Database Course Project Planner

## Purpose
Plan a database principles course design project before implementation. Keep the plan course-oriented: emphasize database design, SQL queries, diagrams, testing evidence, and the final report, not only software engineering tasks.

## Inputs
- Project topic and short background.
- Required or preferred technology stack.
- Course requirements, scoring rubric, or teacher-provided document.
- Whether a runnable system and final report are required.
- Constraints such as deadline, team size, and required diagrams.

## Workflow
1. Extract the course deliverables and required database-design artifacts.
2. Define the project goal, scope, roles, main modules, and boundaries.
3. Propose a repository structure with `docs/`, `database/`, `backend/`, `frontend/`, and test/report files when applicable.
4. Split work into phases: requirement analysis, conceptual design, logical design, physical SQL design, backend, frontend, testing, report, and final integration.
5. Create a task list with concrete outputs, dependencies, and suggested order.
6. Identify risks such as inconsistent table fields, missing report diagrams, or code/report mismatch.
7. Write repository-ready Markdown files instead of a loose chat-only plan.

## Outputs
- `PROJECT_PLAN.md`
- `TODO.md`
- Project directory tree.
- Development phase explanation.
- Final deliverables checklist.

## Quality Checklist
- Verify the plan fits a database principles final project.
- Include database design, SQL examples, diagrams, implementation, testing, and report tasks.
- Make each task traceable to a file or deliverable.
- Keep the plan realistic for a student course project.
- Avoid starting full project code unless the user explicitly asks for implementation.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Use Vue 3 + Element Plus + Axios + Vue Router for frontend, Spring Boot for backend, MySQL for database, Mermaid or PlantUML for diagrams, and Markdown for documents. Plan around visitor appointment, host confirmation, department approval, gate verification, entry/exit registration, blacklist management, record query, statistics, and system administration.

## References
Read `references/project_plan_template.md` when producing `PROJECT_PLAN.md`. Read `references/todo_template.md` when producing a detailed task checklist.
