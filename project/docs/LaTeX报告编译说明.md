# LaTeX 报告编译说明

报告目录：`project/report-latex/`  
主文件：`project/report-latex/main.tex`  
输出文件：`project/report-latex/main.pdf`  
推荐编译器：XeLaTeX

## 一、依赖环境

本报告使用中文 LaTeX 模板，建议安装 TeX Live 或 MiKTeX，并确保以下命令可用：

```bash
xelatex --version
```

报告主要使用宏包：`ctex`、`geometry`、`graphicx`、`float`、`booktabs`、`longtable`、`array`、`hyperref`、`xcolor`、`listings`、`caption`、`subcaption`、`enumitem`、`fancyhdr`、`titlesec`。

## 二、重新生成报告内容

如果修改了需求文档、数据库脚本、截图或设计图，建议先在 `project/` 目录执行：

```bash
python scripts/generate_latex_report.py
```

该脚本会完成以下工作：

- 生成报告专用设计图 PNG，包括数据流程图、E-R 图、系统功能模块图、业务流程图、系统架构图和数据库表关系图。
- 将设计图同步到 `report-latex/figures/`。
- 将运行截图同步到 `report-latex/figures/`。
- 重新生成 `report-latex/chapters/*.tex`。
- 将核心 SQL 脚本副本同步到 `report-latex/listings/`。

## 三、编译 PDF

进入 LaTeX 报告目录：

```bash
cd project/report-latex
xelatex main.tex
xelatex main.tex
```

第二次编译用于刷新目录和交叉引用。

也可以执行脚本：

```bash
bash build.sh
```

Windows 下可执行：

```bat
build.bat
```

## 四、截图联动

重新截图流程：

```bash
cd project
bash scripts/run_all.sh
python scripts/generate_latex_report.py
cd report-latex
xelatex main.tex
xelatex main.tex
```

报告第五章会引用代表性运行界面截图，用于展示登录、首页统计、预约申请、审批处理、门岗核验、出入校登记、黑名单管理、统计报表和系统管理等功能。

## 五、设计图联动

报告已插入以下设计图图片：

- 顶层数据流程图。
- 二层数据流程图。
- 总体简化 E-R 图。
- 核心业务 E-R 图。
- 用户权限 E-R 图。
- 系统支撑 E-R 图。
- 系统功能模块图。
- 访客预约审批流程图。
- 门岗核验入校流程图。
- 系统架构图。
- 数据库表关系图。
- 自动截图与报告生成流程图。

图片生成脚本为：

```bash
python scripts/generate_report_diagrams.py
```

该脚本使用 Pillow 生成 PNG，不依赖 Mermaid CLI 或 Graphviz。原始 Mermaid/DOT 文件仍保留在 `diagrams/`，便于后续修改和对照。

## 六、当前编译结果

本次最终检查已完成 XeLaTeX 编译：

- PDF 路径：`project/report-latex/main.pdf`
- 页数：48 页
- 数据流程图、E-R 图、系统功能模块图和其它设计图均已作为图片插入报告。
- PDF 文本抽查未发现开发路径式说明或图源码展示内容。
- 少量 longtable 或字段名引起的 overfull/underfull 提示不影响 PDF 生成。

## 七、常见问题

1. `xelatex` 命令不存在：安装 TeX Live 或 MiKTeX，并将其 `bin` 目录加入 PATH。
2. 中文乱码：请使用 XeLaTeX，不要使用 pdfLaTeX。
3. 图片缺失：先运行 `python scripts/generate_latex_report.py`，再编译 PDF。
4. 目录页码不更新：连续运行两次 `xelatex main.tex`。