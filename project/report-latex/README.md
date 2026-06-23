# LaTeX 报告说明

本目录为“重庆邮电大学智慧访客预约与出入校管理系统”数据库原理课程设计报告的 LaTeX 项目。

## 编译方式

```bash
xelatex main.tex
xelatex main.tex
```

Windows 可执行：

```bat
build.bat
```

Git Bash / Linux / macOS 可执行：

```bash
bash build.sh
```

## 目录说明

- `main.tex`：报告主文件。
- `chapters/`：六个章节，严格对应课程设计基本要求。
- `figures/`：自动截图和后续导出的设计图 PDF/PNG。
- `styles/report-style.tex`：页面、中文、代码、表格和页眉页脚样式。
- `references.bib`：参考文献占位文件。

## 图形导出

如果需要把 Mermaid 或 Graphviz 图导出为 PDF，请参考 `../diagrams/export/README.md`。
