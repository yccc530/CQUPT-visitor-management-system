---
name: mysql-schema-and-query
description: "用于生成 MySQL 数据库创建脚本、建表 SQL、初始化测试数据和课程报告中的典型 SQL 查询语句。Use when the user asks “生成建表SQL”, “生成测试数据”, “写SQL查询语句”, or “写详细设计SQL”."
---

# MySQL Schema And Query

## Purpose
Produce executable MySQL scripts and report-ready query examples from a logical table design.

## Inputs
- Table design document or relation schema.
- Required database name and MySQL version.
- Seed data scale and scenario coverage.
- Required query list or report section.

## Workflow
1. Create `create_database.sql` with charset and database selection.
2. Order `CREATE TABLE` statements by foreign-key dependency.
3. Add primary keys, foreign keys, unique constraints, status checks where MySQL supports them, and practical indexes.
4. Write seed data that covers normal and exceptional visitor workflows.
5. Write query examples with comments explaining purpose and expected result.
6. Verify syntax and dependency order; run MySQL validation when an environment is available.
7. Keep SQL, table documentation, and report text consistent.

## Outputs
- `database/create_database.sql`
- `database/schema.sql`
- `database/seed.sql`
- `database/query_examples.sql`

## Required Query Examples
Include queries for visitor history, host pending confirmations, today's entered visitors, entered-but-not-exited visitors, overtime visitors, monthly department counts, gate passage counts, blacklist visitors, complete approval history, and system logs.

## Quality Checklist
- Use valid MySQL syntax.
- Respect foreign-key creation order.
- Cover appointment, approval, entry, exit, blacklist, and statistics in seed data.
- Add comments that make queries usable in the final report.
- Avoid SQL that contradicts table design documents.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Use database names like `cqupt_visitor_management` unless the user specifies another. Seed data should include at least one approved visit, one rejected visit, one entered visitor, one exited visitor, one overdue visitor, one blacklisted visitor, and users across departments and gates.

## References
Read `references/sql_query_template.md` when producing SQL files and query explanations.
