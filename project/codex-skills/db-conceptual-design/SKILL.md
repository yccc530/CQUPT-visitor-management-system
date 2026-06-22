---
name: db-conceptual-design
description: "用于数据库概念结构设计，识别实体、属性、联系、主键候选和 E-R 图，并生成报告可用的概念结构说明。Use when the user asks “设计E-R图”, “进行概念结构设计”, “提取实体”, or “设计实体关系”."
---

# Database Conceptual Design

## Purpose
Transform requirements into a conceptual database model. Focus on entities, attributes, relationships, cardinalities, and E-R diagrams rather than physical table DDL.

## Inputs
- Requirement-analysis documents or business descriptions.
- Data dictionary draft, if available.
- Required notation: Mermaid or PlantUML.

## Workflow
1. Extract candidate entities from nouns, data stores, managed objects, and workflow records.
2. Remove pure UI concepts and merge duplicates with clear naming.
3. Assign attributes and identify a stable primary-key candidate for each entity.
4. Identify relationships and cardinalities, including approval records, access records, and role-permission links.
5. Decide whether many-to-many relationships need association entities at the conceptual level.
6. Generate textual E-R design notes and diagram code.
7. Check that the model covers the complete business lifecycle.

## Outputs
- `docs/05_ER设计.md`
- `docs/ER图.mmd` or `docs/ER图.puml`
- `docs/实体属性说明.md`

## Quality Checklist
- Ensure every entity comes from a real business need.
- Explain each entity and its primary key.
- Make relationship names and cardinalities explicit.
- Keep the E-R diagram suitable for a course report.
- Do not collapse conceptual design into only table DDL.

## Default Rules For CQUPT Visitor Management
Start from these core entities: `visitor`, `campus_user`, `department`, `role`, `permission`, `gate`, `visit_apply`, `approval_record`, `access_record`, `pass_code`, `blacklist`, `system_log`, and `notice`. Add or remove entities only when the requirement context justifies it.

## References
Read `references/er_design_template.md` when producing entity descriptions or Mermaid E-R diagram code.
