---
name: visitor-workflow-builder
description: "用于实现访客管理系统核心业务流程和状态流转，包括预约提交、黑名单检查、被访人确认、部门审批、通行凭证、门岗核验、入校登记、离校登记和超时查询。Use when the user asks “实现访客预约流程”, “实现审批流程”, “实现入校离校流程”, or “实现状态流转”."
---

# Visitor Workflow Builder

## Purpose
Implement or document the core visitor-management workflow with explicit states, transitions, preconditions, and database updates.

## Inputs
- Current table design and backend/frontend code.
- Appointment and access status definitions.
- Role-permission rules.
- Required workflow endpoints or pages.

## Workflow
1. Define appointment status and access status values before coding.
2. Implement appointment submission and blacklist check.
3. Implement host confirmation and rejection.
4. Implement department approval and rejection.
5. Generate pass code only after approval.
6. Implement gate verification with pass-code validity and appointment status checks.
7. Implement entry registration and exit registration with duplicate-operation protection.
8. Implement overtime-not-exited query and abnormal handling notes.
9. Record approval and access history for report and audit use.
10. Produce state-transition documentation.

## Outputs
- Core business code.
- State-transition code or constants.
- `docs/访客流程说明.md`
- `docs/状态流转说明.md`

## Quality Checklist
- Prevent rejected applications from generating pass codes.
- Prevent unapproved applications from entering campus.
- Prevent duplicate exit registration.
- Block or warn blacklisted visitors.
- Keep status names consistent across SQL, backend, frontend, diagrams, and report.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Default appointment statuses: `待确认`, `被访人已确认`, `被访人已拒绝`, `待部门审批`, `审批通过`, `审批拒绝`, `已取消`. Default access statuses: `未入校`, `已入校`, `已离校`, `超时未离校`, `异常处理`.

## References
Read `references/visitor_workflow_template.md` before implementing or documenting workflow transitions.
