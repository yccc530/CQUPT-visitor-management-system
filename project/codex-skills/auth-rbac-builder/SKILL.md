---
name: auth-rbac-builder
description: "用于实现登录认证和基于角色的权限控制，包括密码加密、Token 或 Session、角色权限表、后端拦截校验、前端菜单路由控制和权限设计文档。Use when the user asks “实现登录”, “实现权限”, “实现RBAC”, or “不同角色不同菜单”."
---

# Auth RBAC Builder

## Purpose
Implement authentication and role-based access control for a course-project web system. Keep the design understandable and demonstrable without overbuilding enterprise security.

## Inputs
- Existing backend and frontend structure.
- User, role, permission, and menu table design.
- Required login method: Token, JWT-like token, or Session.
- Role-permission matrix.

## Workflow
1. Inspect existing user and permission tables.
2. Define authentication flow: login, password verification, token/session creation, current-user lookup, and logout.
3. Store passwords with hashing and avoid plaintext seed passwords except documented test accounts.
4. Implement backend permission checks through interceptor, filter, annotation, or service checks matching the existing stack.
5. Implement frontend route/menu guards according to roles and permissions.
6. Document the role-permission matrix and protected endpoints.
7. Test positive and negative access cases for each role.

## Outputs
- Login authentication code.
- Permission interceptor or filter.
- Frontend route and menu permission configuration.
- `docs/权限设计.md`

## Quality Checklist
- Make permission granularity clear.
- Prevent roles from calling unauthorized endpoints.
- Keep backend permission checks authoritative; frontend hiding is only a usability layer.
- Document each role and its allowed actions.
- Use a simplified but coherent course-project implementation when needed.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Default roles: visitor, host, department approver, gate security staff, system administrator, and school-level manager. Map each role to appointment, confirmation, approval, gate verification, entry/exit, blacklist, user management, log, and statistics permissions.

## References
Read `references/rbac_template.md` for role-permission matrix and implementation checklist.
