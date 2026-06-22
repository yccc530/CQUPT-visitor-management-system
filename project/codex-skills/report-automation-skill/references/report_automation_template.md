# Report Automation Template

## Source Priority
| Report Section | Primary Source |
|---|---|
| 系统定义 | `docs/report_parts/01_系统定义.md` |
| 需求分析 | `docs/report_parts/02_需求分析.md` |
| 数据流程图 | `docs/diagrams.md`, `diagrams/*dfd*` |
| 数据字典 | `docs/report_parts/04_数据字典.md` |
| 概念结构设计 | `docs/report_parts/05_概念结构设计.md` |
| E-R 图 | `diagrams/er.*`, `docs/diagrams.md` |
| 逻辑结构设计 | `docs/report_parts/06_逻辑结构设计.md` |
| 关系模式 | `database/table_design.md`, `docs/report_parts/关系模式.md` |
| SQL 查询 | `database/query_examples.sql` |
| 系统实现与测试 | `docs/API接口说明.md`, `docs/系统测试说明.md` |
| 截图 | `screenshots/manifest.json` |

## Markdown Image Pattern
```markdown
![首页统计驾驶舱](../screenshots/02_dashboard.png)

图 X-X 首页统计驾驶舱展示今日访客、当前在校、待审批和访问趋势等核心指标。
```

## SQL Block Pattern
````markdown
**用途：** 查询当天已入校但未离校访客。

```sql
SELECT ...
```
````

## Export Strategy
1. Generate Markdown deterministically.
2. If `pandoc` exists, export DOCX with embedded images.
3. If Pandoc is unavailable, use `python-docx` for a simpler DOCX or leave Markdown as canonical.
4. Record missing dependencies in the generation log.