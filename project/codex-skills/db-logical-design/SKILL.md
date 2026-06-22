---
name: db-logical-design
description: "用于将 E-R 图转换为 MySQL 关系模式和逻辑结构设计，输出表、字段、主外键、约束、默认值、关系说明和范式检查。Use when the user asks “转换关系模式”, “逻辑结构设计”, “设计数据库表”, or “设计表字段”."
---

# Database Logical Design

## Purpose
Convert conceptual E-R design into MySQL-ready relation schemas without writing full DDL unless requested. Emphasize keys, fields, constraints, normalization, and table relationships.

## Inputs
- E-R design, entity list, relationship list, or data dictionary.
- Naming rules and MySQL version, if provided.
- Required output files or report sections.

## Workflow
1. Convert each entity into a table using lower_snake_case names.
2. Convert one-to-many relationships into foreign keys on the many side.
3. Convert many-to-many relationships into association tables when needed.
4. Choose MySQL data types, lengths, nullability, defaults, and unique constraints.
5. Add indexes for common query and workflow conditions.
6. Explain table purpose and relation schema notation.
7. Check 1NF, 2NF, and 3NF issues at course-design depth.
8. Compare the design against the full visitor workflow.

## Outputs
- `docs/06_逻辑结构设计.md`
- `docs/关系模式.md`
- `database/table_design.md`

## Quality Checklist
- Use consistent lower_snake_case field names.
- Keep primary and foreign keys complete.
- Avoid duplicated fields unless there is a clear denormalization reason.
- Ensure the tables support appointment, approval, entry, exit, blacklist, statistics, and logs.
- Make the design suitable for MySQL implementation.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Use `id` or clear business keys consistently. Prefer `created_at`, `updated_at`, and status fields where workflow tracking is required. Keep approval history in `approval_record`, access times in `access_record`, and generated credentials in `pass_code`.

## References
Read `references/database_table_template.md` when writing table design documents and relationship schemas.
