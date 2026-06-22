---
name: diagram-generator
description: "用于生成系统设计报告所需图形，包括功能模块图、用例图、数据流程图、E-R 图、数据库表关系图、系统架构图和说明文字。Use when the user asks “生成系统图”, “生成E-R图”, “生成用例图”, “生成数据流程图”, or “生成模块图”."
---

# Diagram Generator

## Purpose
Generate report-ready diagrams in Mermaid or PlantUML for database course design documents.

## Inputs
- Requirement, entity, table, module, or workflow descriptions.
- Preferred diagram language: Mermaid or PlantUML.
- Required diagram type and target report section.

## Workflow
1. Identify the diagram purpose and audience.
2. Select the simplest notation that can express the needed structure.
3. Keep nodes and edges consistent with current project names.
4. Generate renderable Mermaid or PlantUML code.
5. Add a short report paragraph explaining the diagram.
6. Avoid overcrowding; split large diagrams into top-level and detail diagrams.
7. Check syntax and consistency before finalizing.

## Outputs
- `docs/diagrams.md`
- `docs/*.mmd` or `docs/*.puml`
- Report-ready explanation text for each diagram.

## Required Diagram Types
Support system function module diagram, visitor appointment workflow, gate verification workflow, top-level data flow diagram, second-level data flow diagram, E-R diagram, use case diagram, system architecture diagram, and database table relationship diagram.

## Quality Checklist
- Keep diagrams clear and not overly complex.
- Use project-consistent modules, entities, tables, and statuses.
- Ensure code can be copied into Mermaid or PlantUML renderers.
- Provide a concise explanation for every diagram.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Prefer Mermaid for fast Markdown rendering. Use PlantUML when a use case diagram or detailed UML layout is clearer. Keep Chinese labels report-friendly and table/entity IDs stable.

## References
Read `references/diagram_template.md` for common diagram skeletons.
