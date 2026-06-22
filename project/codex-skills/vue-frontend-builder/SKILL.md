---
name: vue-frontend-builder
description: "用于生成 Vue 3 + Element Plus 前端页面、路由、Axios 接口封装、登录页、仪表盘、业务模块页面和角色菜单控制。Use when the user asks “生成前端”, “开发Vue页面”, “写访客系统页面”, or “对接后端接口”."
---

# Vue Frontend Builder

## Purpose
Build Vue 3 + Element Plus frontend pages for a database course project and connect them to backend APIs.

## Inputs
- Existing frontend project or desired scaffold.
- API documentation and backend base URL.
- Database field names for form alignment.
- Role-menu rules and required pages.

## Workflow
1. Inspect existing frontend structure before editing.
2. Configure Vue Router, Axios, Element Plus, layout, and route guards.
3. Create login and main layout pages.
4. Build business pages with forms, tables, filters, pagination, dialogs, and state-aware action buttons.
5. Encapsulate API calls by module.
6. Map form fields to database and backend DTO names.
7. Implement menu visibility according to roles.
8. Run frontend build or dev server verification when possible.
9. Document pages and run instructions.

## Outputs
- `frontend/`
- `docs/前端页面说明.md`

## Required Pages
Include login, dashboard, appointment application, my appointments, pending appointments, department approval, gate verification, entry registration, exit registration, blacklist management, visitor record query, statistics reports, user management, department management, gate management, and system logs.

## Quality Checklist
- Keep layouts clear and task-focused.
- Match forms to database/backend fields.
- Add common filters on query pages.
- Show valid operations for each business status.
- Ensure code can run or clearly document remaining environment steps.

## Default Rules For CQUPT Visitor Management
Use Element Plus tables, forms, dialogs, tags, date pickers, and pagination for administrative pages. Use role menus matching visitor, host, department approver, gate security, system administrator, and school manager.

## References
Read `references/frontend_page_template.md` when creating pages or page documentation.
