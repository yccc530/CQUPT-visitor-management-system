---
name: fullstack-integration-skill
description: "用于全栈联调和一致性检查，核对 Vue 前端字段、Axios 调用、Spring Boot Controller/DTO/Service、MyBatis Plus Mapper、MySQL 表字段、权限和状态流转是否一致，并修复接口联调问题。Use when the user asks “前后端联调”, “接口对不上”, “字段不一致”, “全栈集成”, or “修复联调问题”."
---

# Fullstack Integration Skill

## Purpose
Keep frontend pages, backend APIs, database tables, RBAC permissions, workflow status values, demo data, screenshots, and report artifacts aligned.

## Inputs
- `frontend/` routes, pages, API modules, and form fields.
- `backend/` controllers, DTOs, entities, services, mappers, and security config.
- `database/schema.sql`, `seed.sql`, and `query_examples.sql`.
- API documentation and known integration bugs.

## Workflow
1. Build an API contract inventory from backend controllers and frontend Axios modules.
2. Build a field map from MySQL schema, backend entities/DTOs, and frontend form/table columns.
3. Compare route permissions, backend authorization, and role-menu configuration.
4. Compare workflow status constants across SQL seed data, backend code, frontend tags, and diagrams.
5. Run backend and frontend checks when available.
6. Reproduce integration bugs using API calls or UI flow.
7. Fix the smallest consistent layer set: database, backend DTO/entity, API response, frontend call, or UI field.
8. Update API docs, test notes, screenshot plan, and report references after contract changes.

## Outputs
- Fixed frontend/backend/database integration code.
- Updated `docs/API接口说明.md` when contracts change.
- `docs/联调问题记录.md` or updates to `bug_fix_log.md`.
- Consistency checklist for final integration.

## Quality Checklist
- Do not make frontend field names diverge from backend DTOs without explicit mapping.
- Do not let report SQL or screenshots describe fields that do not exist.
- Ensure role permissions are enforced by backend and reflected by frontend menus.
- Ensure state transitions remain valid after fixes.
- Re-run relevant tests or document why they cannot run.

## Default Rules For CQUPT Visitor System
Pay special attention to `visit_apply`, `approval_record`, `pass_code`, `access_record`, `blacklist`, `sys_user`, `sys_role`, `sys_permission`, `department`, and `campus_gate`. Align date-time formats, ID types, status labels, pagination response structure, and unified response format across the stack.

## References
Read `references/integration_check_template.md` before performing contract checks or writing integration notes.