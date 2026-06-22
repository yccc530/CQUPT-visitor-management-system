---
name: db-requirement-analysis
description: "用于数据库应用系统需求分析，输出系统定义、用户角色、业务流程、功能需求、非功能需求、数据流程说明和数据字典初稿。Use when the user asks “写需求分析”, “分析系统需求”, “分析访客系统业务流程”, “生成数据流程图”, or “生成数据字典”."
---

# Database Requirement Analysis

## Purpose
Produce formal requirement-analysis documents for a database application system. Keep every requirement traceable to later database tables, SQL queries, backend modules, or report sections.

## Inputs
- Project topic and system background.
- Known users, departments, business processes, and required functions.
- Course document requirements or report outline.
- Existing project files, if any.

## Workflow
1. Define the system, problem domain, management objects, and project boundary.
2. Identify users and describe their responsibilities and data permissions.
3. Describe the full business lifecycle from data creation to archiving and statistics.
4. Convert business activities into use cases and functional requirements.
5. Add non-functional requirements for performance, security, usability, maintainability, and data integrity.
6. Produce data-flow text and diagram code when requested.
7. Draft a data dictionary with data items, sources, destinations, and constraints.
8. Check that each functional requirement maps to a future entity, table, or module.

## Outputs
- `docs/01_系统定义.md`
- `docs/02_需求分析.md`
- `docs/03_数据流程图.md`
- `docs/04_数据字典.md`
- `docs/use_cases.md`

## Quality Checklist
- Use formal report language.
- Tie requirements to database design artifacts.
- Avoid vague claims; include concrete visitor-management data and actions.
- Cover roles, workflows, data flows, functions, and non-functional needs.
- Keep names consistent with future entities and tables.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Default roles are visitor, host, department approver, gate security staff, system administrator, and school-level manager. Default process: visitor submits appointment, system checks blacklist, host confirms, department approves, pass code is generated, gate verifies, entry is registered, exit is registered, record is archived, and statistics are queried.

## References
Read `references/requirement_template.md` for document structure. Read `references/data_dictionary_template.md` when drafting data dictionary entries.
