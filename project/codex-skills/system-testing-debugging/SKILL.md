---
name: system-testing-debugging
description: "用于系统测试、前后端联调、Bug 修复和测试文档生成，覆盖数据库连接、接口、页面、登录权限、访客预约审批、入校离校、黑名单和统计报表。Use when the user asks “测试系统”, “联调前后端”, “修复bug”, or “写测试说明”."
---

# System Testing Debugging

## Purpose
Test, debug, and document a database course project so the runnable system and report evidence align.

## Inputs
- Current backend, frontend, and SQL scripts.
- Test account requirements.
- Known bugs, logs, screenshots, or failing commands.
- Expected business workflows.

## Workflow
1. Inspect project run instructions and environment configuration.
2. Test database initialization and connection.
3. Test backend APIs by module, including authorization cases.
4. Test frontend pages and API integration.
5. Execute full visitor workflows: appointment, confirmation, approval, gate verification, entry, exit, blacklist, and statistics.
6. Record bugs with cause, fix, affected files, and verification.
7. Generate test cases and testing report suitable for the final course report.

## Outputs
- `docs/系统测试说明.md`
- `docs/测试用例.md`
- `bug_fix_log.md`
- Test account explanation.
- System run notes.

## Quality Checklist
- Cover core business workflows.
- Include input, steps, expected result, actual result, and conclusion for each test case.
- Fix discovered problems when possible.
- Keep testing evidence consistent with system features and report text.
- State clearly when a test cannot be run and why.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Include role-based test accounts for visitor, host, department approver, gate security staff, system administrator, and school manager. Include positive and negative tests for invalid status transitions and unauthorized access.

## References
Read `references/test_case_template.md` when writing test cases or bug logs.
