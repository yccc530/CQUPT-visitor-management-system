---
name: screenshot-automation-skill
description: "用于使用 Playwright 自动登录数据库课程设计系统、访问核心页面、保存运行截图并生成 screenshots/manifest.json。Use when the user asks “自动截图”, “生成系统运行截图”, “截图报告页面”, or “用 Playwright 捕获页面”."
---

# Screenshot Automation Skill

## Purpose
Automate report screenshot capture for the CQUPT smart CQUPT smart visitor appointment and campus access system using Playwright. Screenshots must demonstrate real pages, real demo data, role permissions, workflow states, and statistics.

## Inputs
- Running frontend URL and backend API URL.
- Test accounts for each role.
- Required route list and page readiness selectors.
- Screenshot naming rules and output directory.

## Workflow
1. Confirm backend, frontend, and database seed data are running.
2. Create or update `scripts/capture_screenshots.js`.
3. Configure Playwright browser, viewport, base URL, timeout, and screenshot directory.
4. Log in with appropriate accounts for visitor, host, approver, gate security, admin, and manager views.
5. Visit required pages, wait for stable selectors, and capture full-page screenshots.
6. Save screenshots under `screenshots/` with stable names.
7. Generate `screenshots/manifest.json` with title, route, role, filename, description, and report section.
8. Fail clearly if a route, selector, login, or API call is broken so integration can fix it.

## Outputs
- `scripts/capture_screenshots.js`
- `screenshots/*.png`
- `screenshots/manifest.json`
- Optional screenshot notes for report automation.

## Required Screenshots
Capture at least login page, dashboard, visitor appointment page, approval page, gate verification page, entry registration page, exit registration page, blacklist page, statistics report page, user management page, and system log page. Add current in-campus and overdue visitor pages when available.

## Quality Checklist
- Screenshots must show populated data, not empty placeholder pages.
- Use deterministic route, role, and filename mapping.
- Wait for data-loaded selectors before capture.
- Use report-friendly viewport such as 1440x900 unless the user requests otherwise.
- Produce a valid manifest that report automation can consume.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Use admin or school manager for dashboard, statistics, users, logs, and blacklist. Use visitor for appointment/my appointment pages. Use host for pending confirmation. Use department approver for department approval. Use gate security for verification, entry, exit, current-campus, and overdue visitor pages.

## References
Read `references/screenshot_plan_template.md` before writing capture scripts or screenshot manifests.