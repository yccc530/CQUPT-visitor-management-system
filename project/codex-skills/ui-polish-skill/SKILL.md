---
name: ui-polish-skill
description: "用于美化 Vue 3 + Element Plus 数据库课程设计前端，统一校园蓝或科技蓝主题、布局、图标、状态标签、表单校验、空状态、统计卡片、图表和演示细节。Use when the user asks “美化前端”, “优化界面”, “让页面更适合演示”, “统一校园蓝主题”, or “提升前端质感”."
---

# UI Polish Skill

## Purpose
Polish the frontend of the CQUPT smart visitor appointment and campus access management system so it feels modern, coherent, and suitable for course demonstration screenshots.

## Inputs
- Existing `frontend/` project.
- Required pages, role menus, and backend API contract.
- Current screenshots or user feedback, if available.
- Preferred theme direction; default to campus blue or technology blue.

## Workflow
1. Inspect the existing frontend structure, routes, components, and style files.
2. Define a theme system: colors, spacing, typography, shadows, border radii, status colors, and chart palette.
3. Build a unified app shell with sidebar, topbar, breadcrumb, account menu, and responsive content area.
4. Polish dashboard with statistics cards, ECharts charts, quick entry actions, pending task area, and campus visitor context.
5. Polish list pages with filter bars, tables, status tags, operation buttons, pagination, empty states, and loading states.
6. Polish form and detail pages with grouped fields, validation, descriptions, dialogs, and state-aware controls.
7. Ensure role-specific menus and disabled actions are visually clear.
8. Run frontend build or visual check when available, then record UI notes for the report.

## Outputs
- Updated `frontend/` styles, layout, components, and pages.
- `docs/前端页面说明.md` UI section updates.
- Screenshot-ready frontend pages for login, dashboard, workflow pages, management pages, and statistics.

## Quality Checklist
- Use a cohesive campus-blue or technology-blue palette without making every surface the same hue.
- Ensure every required page has useful data, polished empty/loading/error states, and consistent controls.
- Ensure buttons, tags, dialogs, forms, tables, and charts follow one visual language.
- Keep text readable and non-overlapping on common desktop and laptop viewports.
- Avoid marketing-style landing pages; make the first screen the usable system.
- Verify pages support screenshots for the final report.

## Default Rules For CQUPT Visitor System
Use dashboard cards for today visitors, current in-campus visitors, pending approvals, overdue visitors, pass-code verifications, and monthly visits. Use status tags for appointment status, access status, approval result, blacklist status, and pass-code validity. Use ECharts for department ranking, gate passage statistics, approval rate, and visit trend.

## References
Read `references/ui_polish_template.md` before applying visual changes or documenting UI quality.