# 图形导出说明

当前目录用于保存 LaTeX 报告可直接引用的 PDF 图文件。

## 推荐导出方式：Graphviz DOT

```bash
dot -Tpdf diagrams/er_overview.dot -o diagrams/export/er_overview.pdf
dot -Tpdf diagrams/er_core_business.dot -o diagrams/export/er_core_business.pdf
dot -Tpdf diagrams/er_user_permission.dot -o diagrams/export/er_user_permission.pdf
dot -Tpdf diagrams/er_system_support.dot -o diagrams/export/er_system_support.pdf
dot -Tpdf diagrams/table_relation.dot -o diagrams/export/table_relation.pdf
```

## Mermaid CLI 导出方式

```bash
mmdc -i diagrams/er_overview.mmd -o diagrams/export/er_overview.pdf
mmdc -i diagrams/er_core_business.mmd -o diagrams/export/er_core_business.pdf
mmdc -i diagrams/er_user_permission.mmd -o diagrams/export/er_user_permission.pdf
mmdc -i diagrams/er_system_support.mmd -o diagrams/export/er_system_support.pdf
mmdc -i diagrams/table_relation.mmd -o diagrams/export/table_relation.pdf
```

如果使用 Windows PowerShell，请先进入 `project/` 目录再执行上述命令。