# LaTeX 课程设计报告

本目录是“重庆邮电大学智慧访客预约与出入校管理系统”数据库原理课程设计报告的 LaTeX 项目。报告使用 `ctexart` 和 XeLaTeX 编译，支持中文、目录、长表格、SQL 代码块、规范化设计图图片和代表性运行截图。

## 编译命令

```bash
cd project/report-latex
xelatex main.tex
xelatex main.tex
```

也可以执行：

```bash
bash build.sh
```

Windows 下可执行：

```bat
build.bat
```

## 内容重新生成

如果修改了需求文档、数据库脚本、截图或设计图，请先在 `project/` 目录执行：

```bash
python scripts/generate_latex_report.py
```

该脚本会重新生成章节内容，生成并同步报告用设计图 PNG，同时同步运行截图。

## 当前状态

- `main.pdf` 已生成。
- 数据流程图采用 DFD 规范表达，区分外部实体、处理过程、数据存储和数据流。
- E-R 图采用实体、属性、联系和数量约束分离的表达方式，标明主键、联系动词和参与约束。
- 系统功能模块图、业务流程图、系统架构图和数据库表关系图均已作为图片插入正文。
- 代表性运行截图已插入第五章。
