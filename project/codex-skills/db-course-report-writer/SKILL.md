---
name: db-course-report-writer
description: "用于撰写最终数据库课程设计报告，整理系统定义、需求分析、数据流程图、数据字典、E-R 图、逻辑结构、关系模式、SQL、系统实现、测试和总结。Use when the user asks “写课程设计报告”, “生成最终报告”, “整理系统设计文档”, or “写数据库大作业报告”."
---

# Database Course Report Writer

## Purpose
Assemble a formal database principles course design report from existing design documents, SQL, diagrams, code notes, and test evidence.

## Inputs
- `docs/` design materials.
- `database/` SQL scripts and table design.
- Backend/frontend implementation notes.
- Test documents and screenshots list.
- Teacher-provided report requirements, if available.

## Workflow
1. Inventory available materials and identify missing report sections.
2. Follow the required report structure and course terminology.
3. Merge requirement, conceptual, logical, physical SQL, implementation, and testing content.
4. Ensure table names, field names, entities, modules, diagrams, and SQL examples are consistent.
5. Explain representative SQL queries with business purpose and expected output.
6. Add placeholders or lists for screenshots/figures that must be inserted later.
7. Produce a polished Markdown report ready for conversion to Word.

## Outputs
- `docs/重庆邮电大学智慧访客预约与出入校管理系统设计报告.md`
- `docs/报告素材清单.md`
- `docs/需要插入的图片清单.md`

## Quality Checklist
- Use formal academic/course-report language.
- Emphasize database design instead of only system introduction.
- Include SQL explanations and diagram descriptions.
- Keep diagrams, table structures, relation schemas, functions, and report text consistent.
- Make the report usable as a final-project draft.

## Default Rules For 重庆邮电大学智慧访客预约与出入校管理系统
Required sections: system definition, requirement analysis, data flow diagram, data dictionary, conceptual design, E-R diagram, logical design, relation schema, function module diagram, detailed design, SQL query statements and explanations, system implementation, system testing, and conclusion.

## References
Read `references/report_structure.md` before writing or reorganizing the final report.
