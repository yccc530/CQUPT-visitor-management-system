---
name: report-automation-skill
description: "用于自动生成数据库课程设计报告，读取 docs、database、diagrams、screenshots 中的内容，整合系统定义、需求分析、E-R 图、逻辑设计、SQL、实现测试和截图，并尽量导出 Word 文档。Use when the user asks “自动生成报告”, “生成课程设计报告”, “导出 Word 报告”, or “把截图和SQL插入报告”."
---

# Report Automation Skill

## Purpose
Generate a database principles final project report from project artifacts with minimal manual copying. The report must match the teacher's requirements and stay consistent with the actual SQL, diagrams, screenshots, and implementation.

## Inputs
- `docs/report_parts/`
- `docs/diagrams.md` and `diagrams/`
- `database/create_database.sql`, `schema.sql`, `seed.sql`, `query_examples.sql`
- `screenshots/manifest.json` and PNG files
- Backend/frontend implementation notes and test documents
- Teacher-provided document requirements, when available

## Workflow
1. Inventory source files and detect missing report sections.
2. Create or update `scripts/generate_report.py`.
3. Read report parts in the required order and merge them into one Markdown report.
4. Insert Mermaid/PlantUML code blocks or diagram image references with explanations.
5. Insert table design summaries, relation schemas, and representative SQL query blocks with purpose descriptions.
6. Insert screenshot images from `screenshots/manifest.json` with captions and matching report sections.
7. Add system implementation, testing, and conclusion sections.
8. Export Markdown first; attempt Word export through Pandoc or Python-docx when available.
9. Produce a generation log that lists missing files and generated outputs.

## Outputs
- `scripts/generate_report.py`
- `docs/重庆邮电大学访客管理系统设计报告.md`
- Optional `docs/重庆邮电大学访客管理系统设计报告.docx`
- `docs/报告素材清单.md`
- Optional report generation log.

## Required Report Structure
Include system definition, requirement analysis, data flow diagram, data dictionary, conceptual design, E-R diagram, logical design, relation schema, function module diagram, other design diagrams, detailed SQL design, system implementation and testing, runtime screenshots, and conclusion.

## Quality Checklist
- Ensure report table names, field names, SQL, screenshots, and feature descriptions match actual project files.
- Keep language formal and database-course oriented.
- Explain every major diagram, table group, SQL query, and screenshot.
- Prefer generated content from source artifacts over duplicated hand-written fragments.
- Clearly report missing artifacts instead of silently inventing final evidence.

## Default Rules For CQUPT Visitor System
Use Markdown as the canonical report. Use Pandoc for DOCX export when installed; otherwise use Python-docx to create a simplified Word document or document the missing dependency. Keep screenshot captions tied to `screenshots/manifest.json`.

## References
Read `references/report_automation_template.md` before writing the report generation script or report assembly rules.