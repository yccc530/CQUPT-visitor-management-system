---
name: springboot-backend-builder
description: "用于生成 Spring Boot 后端项目结构和业务模块，包括 Entity、Mapper 或 Repository、Service、Controller、统一返回、异常处理和接口文档。Use when the user asks “生成后端”, “实现接口”, “写Spring Boot代码”, or “开发访客系统后端”."
---

# Spring Boot Backend Builder

## Purpose
Build or extend a Spring Boot backend for a database course project. Generate runnable layered code for requested modules, keeping it consistent with MySQL tables and course-report documentation.

## Inputs
- Existing backend project or desired package name.
- MySQL table design or `schema.sql`.
- Required modules and API endpoints.
- Authentication approach, if already chosen.

## Workflow
1. Inspect the existing backend structure before editing.
2. Create or follow layers: entity, mapper/repository, service, service implementation, controller, DTO/VO, common response, exception handling, and config.
3. Map database fields to Java types and validation annotations.
4. Implement CRUD and workflow endpoints needed by the requested module.
5. Add parameter validation and consistent error handling.
6. Add API documentation with method, path, parameters, response, and business notes.
7. Run compile or targeted tests when the environment supports it.

## Outputs
- `backend/`
- `docs/API接口说明.md`

## Core Modules
Support visitor management, campus user management, department management, role-permission management, appointment management, approval, gate verification, entry registration, exit registration, blacklist management, statistics, and system logs.

## Quality Checklist
- Keep code layered and names consistent.
- Match Java fields to MySQL table fields.
- Implement real core business behavior, not empty shells.
- Validate parameters for important endpoints.
- Keep APIs RESTful and easy for Vue/Axios to call.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Prefer a course-friendly Spring Boot stack such as Spring Web, validation, MyBatis or MyBatis-Plus, MySQL connector, and Lombok if the project already uses it. Keep security practical for a course project and coordinate with `auth-rbac-builder` when permissions are requested.

## References
Read `references/backend_module_template.md` for module structure and `references/api_doc_template.md` for API documentation format.
