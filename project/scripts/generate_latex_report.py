from pathlib import Path
import json
import re
import shutil

root = Path('project')
latex = root / 'report-latex'
chapters = latex / 'chapters'
figures = latex / 'figures'
tables = latex / 'tables'
listings = latex / 'listings'
styles = latex / 'styles'
for d in [latex, chapters, figures, tables, listings, styles]:
    d.mkdir(parents=True, exist_ok=True)

# Copy current screenshots as initial LaTeX figures. The capture script will refresh them later.
# On Windows, image files may be temporarily locked by a viewer or sync client; keep report generation resilient.
for png in (root / 'screenshots').glob('*.png'):
    dst = figures / png.name
    try:
        if dst.exists():
            try:
                dst.unlink()
            except PermissionError:
                continue
        shutil.copy2(png, dst)
    except PermissionError:
        continue

SPECIAL = {
    '&': r'\&', '%': r'\%', '$': r'\$', '#': r'\#', '_': r'\_',
    '{': r'\{', '}': r'\}', '~': r'\textasciitilde{}', '^': r'\textasciicircum{}',
    '\\': r'\textbackslash{}'
}

def tex_escape(text):
    if text is None:
        return ''
    return ''.join(SPECIAL.get(ch, ch) for ch in str(text))

def read(rel, default=''):
    p = root / rel
    return p.read_text(encoding='utf-8') if p.exists() else default

def strip_md_heading(text):
    return re.sub(r'^# .*?\n+', '', text.strip(), count=1, flags=re.S)

def md_to_tex(md, level_shift=0, max_lines=None):
    lines = md.splitlines()
    if max_lines:
        lines = lines[:max_lines]
    out = []
    in_code = False
    code_lang = ''
    table_buf = []

    def flush_table():
        nonlocal table_buf
        if not table_buf:
            return
        rows = []
        for raw in table_buf:
            cells = [c.strip() for c in raw.strip().strip('|').split('|')]
            if cells and all(re.fullmatch(r':?-{3,}:?', c or '') for c in cells):
                continue
            rows.append(cells)
        table_buf = []
        if not rows:
            return
        col_count = max(len(r) for r in rows)
        col_width = 0.94 / col_count
        col_spec = ''.join([f'p{{{col_width:.2f}\\textwidth}}' for _ in range(col_count)])
        out.append(r'\begin{longtable}{' + col_spec + '}')
        out.append(r'\toprule')
        for idx, row in enumerate(rows):
            row = row + [''] * (col_count - len(row))
            out.append(' & '.join(tex_escape(c) for c in row) + r' \\')
            if idx == 0:
                out.append(r'\midrule')
        out.append(r'\bottomrule')
        out.append(r'\end{longtable}')
        out.append('')

    for line in lines:
        if line.strip().startswith('```'):
            flush_table()
            if not in_code:
                code_lang = line.strip()[3:].strip().lower()
                style = 'sqlstyle' if 'sql' in code_lang else 'codestyle'
                out.append(r'\begin{lstlisting}[style=' + style + ']')
                in_code = True
            else:
                out.append(r'\end{lstlisting}')
                in_code = False
            continue
        if in_code:
            out.append(line)
            continue
        if line.strip().startswith('|') and line.strip().endswith('|'):
            table_buf.append(line)
            continue
        flush_table()
        m = re.match(r'^(#{1,6})\s+(.*)$', line)
        if m:
            depth = len(m.group(1)) + level_shift
            title = tex_escape(m.group(2).strip())
            cmd = ['section', 'subsection', 'subsubsection', 'paragraph', 'subparagraph'][min(depth-1, 4)]
            out.append(f'\\{cmd}{{{title}}}')
            continue
        if not line.strip():
            out.append('')
            continue
        numbered = re.match(r'^\d+\.\s+(.*)$', line)
        bullet = re.match(r'^[-*]\s+(.*)$', line)
        if numbered:
            out.append(tex_escape(numbered.group(1)) + r'\\')
        elif bullet:
            out.append(r'\noindent $\bullet$ ' + tex_escape(bullet.group(1)) + r'\\')
        else:
            out.append(tex_escape(line))
    flush_table()
    if in_code:
        out.append(r'\end{lstlisting}')
    return '\n'.join(out)

def diagram_todo(title, source, export_cmd):
    return f'''\\subsubsection{{{tex_escape(title)}}}
\\noindent 图源文件：\\texttt{{{tex_escape(source)}}}。当前环境未检测到 Graphviz 或 Mermaid CLI 导出的 PDF 文件，LaTeX 报告中先保留导出说明。生成 PDF 后可替换为 \\verb|\\includegraphics| 引用。\\par
\\begin{{lstlisting}}[style=codestyle]
{export_cmd}
\\end{{lstlisting}}
'''

def split_sql_examples(sql_text):
    parts = []
    matches = list(re.finditer(r'^--\s*(\d+)\.\s*(.+?)。?\s*$', sql_text, flags=re.M))
    for idx, m in enumerate(matches):
        start = m.end()
        end = matches[idx+1].start() if idx + 1 < len(matches) else len(sql_text)
        body = sql_text[start:end].strip()
        if body:
            parts.append((m.group(1), m.group(2).strip(), body))
    return parts

def involved_tables(sql):
    names = re.findall(r'\b(?:FROM|JOIN|UPDATE|INTO)\s+([a-zA-Z_][a-zA-Z0-9_]*)', sql, flags=re.I)
    seen = []
    for name in names:
        if name.lower() not in {'select'} and name not in seen:
            seen.append(name)
    return ', '.join(seen) if seen else '由 SQL 语句中的 FROM/JOIN 子句确定'

system_definition = read('docs/report_parts/01_系统定义.md')
requirement = read('docs/report_parts/02_需求分析.md')
data_flow = read('docs/report_parts/03_数据流程图.md')
data_dict = read('docs/report_parts/04_数据字典.md')
concept = read('docs/report_parts/05_概念结构设计.md')
er_doc = read('docs/report_parts/06_ER图.md')
logical = read('docs/report_parts/07_逻辑结构设计.md')
relation = read('docs/关系模式.md')
table_design = read('database/table_design.md')
api_doc = read('docs/API接口说明.md')
test_doc = read('docs/系统测试说明.md')
test_cases = read('docs/测试用例.md')
query_sql = read('database/query_examples.sql')
schema_sql = read('database/schema.sql')

# 01
(chapters / '01_system_definition.tex').write_text(r'''
\section{系统定义}
''' + md_to_tex(strip_md_heading(system_definition), level_shift=1) + r'''

\subsection{本章小结}
本系统围绕高校访客预约和出入校管理构建统一数据库应用，系统名称、业务范围、服务对象和建设意义已经明确，为后续需求分析、概念结构设计和系统实现提供边界。
''', encoding='utf-8')

# 02
(chapters / '02_requirement_analysis.tex').write_text(r'''
\section{需求分析}
\subsection{综合需求}
''' + md_to_tex(requirement, level_shift=2) + r'''

\subsection{数据流程图}
本系统的数据流程图分为顶层数据流程图和二层数据流程图。源文件位于 \texttt{diagrams/data\_flow\_level0.mmd} 和 \texttt{diagrams/data\_flow\_level1.mmd}。当前 LaTeX 项目未直接嵌入 Mermaid 图，导出 PDF 后可替换为图片引用。
\begin{lstlisting}[style=codestyle]
mmdc -i diagrams/data_flow_level0.mmd -o report-latex/figures/data_flow_level0.pdf
mmdc -i diagrams/data_flow_level1.mmd -o report-latex/figures/data_flow_level1.pdf
\end{lstlisting}
''' + md_to_tex(strip_md_heading(data_flow), level_shift=2, max_lines=120) + r'''

\subsection{数据字典}
''' + md_to_tex(strip_md_heading(data_dict), level_shift=2) + '\n', encoding='utf-8')

# 03
system_design_parts = [r'\section{系统设计}', r'\subsection{概念结构设计}', md_to_tex(strip_md_heading(concept), level_shift=2), r'\subsection{E-R 图}',
    '本项目将 E-R 图拆分为总体简化图、核心业务图、用户权限图和系统支撑图。这样可以避免一张图中实体过多、连线交叉严重的问题。报告正文建议使用总体简化 E-R 图和核心业务 E-R 图，完整主外键关系图放入附录。',
    diagram_todo('总体简化 E-R 图', 'diagrams/er_overview.mmd', 'dot -Tpdf diagrams/er_overview.dot -o report-latex/figures/er_overview.pdf'),
    diagram_todo('核心业务 E-R 图', 'diagrams/er_core_business.mmd', 'dot -Tpdf diagrams/er_core_business.dot -o report-latex/figures/er_core_business.pdf'),
    diagram_todo('用户权限 E-R 图', 'diagrams/er_user_permission.mmd', 'dot -Tpdf diagrams/er_user_permission.dot -o report-latex/figures/er_user_permission.pdf'),
    r'\subsection{逻辑结构设计}', md_to_tex(strip_md_heading(logical), level_shift=2),
    r'\subsection{关系模式}', md_to_tex(strip_md_heading(relation), level_shift=2),
    r'\subsection{系统功能模块图}', diagram_todo('系统功能模块图', 'diagrams/system_module.mmd', 'mmdc -i diagrams/system_module.mmd -o report-latex/figures/system_module.pdf'),
    r'\subsection{其它设计图}',
    diagram_todo('访客预约审批流程图', 'diagrams/visitor_workflow.mmd', 'mmdc -i diagrams/visitor_workflow.mmd -o report-latex/figures/visitor_workflow.pdf'),
    diagram_todo('门岗核验流程图', 'diagrams/gate_check_workflow.mmd', 'mmdc -i diagrams/gate_check_workflow.mmd -o report-latex/figures/gate_check_workflow.pdf'),
    diagram_todo('系统架构图', 'diagrams/system_architecture.mmd', 'mmdc -i diagrams/system_architecture.mmd -o report-latex/figures/system_architecture.pdf'),
    diagram_todo('数据库表关系图', 'diagrams/table_relation.mmd', 'dot -Tpdf diagrams/table_relation.dot -o report-latex/figures/table_relation.pdf'),
    r'\subsection{表结构摘要}', md_to_tex(strip_md_heading(table_design), level_shift=2, max_lines=220)
]
(chapters / '03_system_design.tex').write_text('\n\n'.join(system_design_parts), encoding='utf-8')

# 04
sql_sections = [r'\section{详细设计}', r'\subsection{SQL 查询语句和说明}', '本节列出系统课程设计报告中的典型 SQL 查询。SQL 均使用 MySQL 8 语法，并尽量使用动态日期，避免演示数据随时间变化后统计为空。']
for no, title, sql in split_sql_examples(query_sql):
    sql_sections.append(f'\\subsubsection{{SQL-{int(no):02d}：{tex_escape(title)}}}')
    sql_sections.append(f'\\textbf{{查询目的：}}{tex_escape(title)}。\\par')
    sql_sections.append(f'\\textbf{{涉及数据表：}}\\texttt{{{tex_escape(involved_tables(sql))}}}。\\par')
    sql_sections.append(r'\begin{lstlisting}[style=sqlstyle]')
    sql_sections.append(sql)
    sql_sections.append(r'\end{lstlisting}')
    sql_sections.append('查询结果用于支撑页面列表、门岗核验、统计报表或课程设计报告中的数据说明。')
sql_sections.append(r'\subsection{建表 SQL 摘要}')
schema_excerpt = '\n'.join(schema_sql.splitlines()[:220])
sql_sections.append(r'\begin{lstlisting}[style=sqlstyle]')
sql_sections.append(schema_excerpt)
sql_sections.append(r'\end{lstlisting}')
(chapters / '04_detail_design.tex').write_text('\n\n'.join(sql_sections), encoding='utf-8')

# screenshots manifest read current or fallback
manifest_path = root / 'screenshots/manifest.json'
manifest = []
if manifest_path.exists():
    manifest = json.loads(manifest_path.read_text(encoding='utf-8'))

impl = [r'\section{系统实现与测试}', r'\subsection{开发平台和工具选择}', md_to_tex(strip_md_heading(test_doc), level_shift=2, max_lines=90), r'\subsection{系统测试}', md_to_tex(strip_md_heading(test_cases), level_shift=2), r'\subsection{代表性运行界面}', r'以下截图由 Playwright 自动访问系统核心页面后生成，并同步到本 LaTeX 项目的 \texttt{figures/} 目录。每张图对应截图清单中的页面说明。']
for item in manifest:
    title = item.get('title') or item.get('pageName') or item.get('screenshotCode')
    desc = item.get('description') or ''
    file_name = Path(item.get('latexFile') or item.get('filePath') or '').name
    if not file_name:
        continue
    impl.append(tex_escape(desc))
    impl.append(r'\begin{figure}[H]')
    impl.append(r'  \centering')
    impl.append(rf'  \includegraphics[width=0.92\textwidth]{{figures/{file_name}}}')
    impl.append(rf'  \caption{{{tex_escape(title)}}}')
    impl.append(r'\end{figure}')
(chapters / '05_implementation_testing.tex').write_text('\n\n'.join(impl), encoding='utf-8')

summary = r'''
\section{总结}

本课程设计围绕“重庆邮电大学智慧访客预约与出入校管理系统”完成了从需求分析、概念结构设计、逻辑结构设计、MySQL 建库建表、后端接口、前端页面、权限控制、核心业务流程、统计报表、自动截图到报告生成的完整实践。系统以访客预约为主线，将访客信息、预约申请、审批记录、通行凭证、出入校记录、黑名单、用户权限和系统日志统一纳入数据库管理，能够较好地体现数据库系统在校园安全管理场景中的建模、约束、查询和应用价值。

在数据库设计方面，系统明确了实体、关系、主键、外键、唯一约束、索引和状态字段，核心表结构能够覆盖从预约提交到访问归档的完整流程。在系统实现方面，后端采用 Spring Boot 3、MyBatis Plus、MySQL 和 JWT，前端采用 Vue 3、Element Plus、Axios、Vue Router 和 ECharts，页面能够展示预约、审批、核验、出入校、黑名单、统计报表和系统管理等核心功能。在自动化方面，项目提供 Playwright 截图脚本和 LaTeX 报告项目，便于将运行界面、SQL、图表和测试说明整合为最终课程设计报告。

本项目仍有继续优化空间，例如进一步补充自动化单元测试、增强接口文档的示例响应、将 Mermaid 和 Graphviz 图自动导出为 PDF，并在报告生成脚本中实现从数据库实时读取表结构和统计结果。但从数据库原理课程设计角度看，当前系统已经形成了完整的数据模型、可运行应用、演示数据、截图素材和报告结构，能够支撑课程提交与答辩展示。
'''
(chapters / '06_summary.tex').write_text(summary, encoding='utf-8')

style = r'''
\usepackage[a4paper, left=2.6cm, right=2.4cm, top=2.6cm, bottom=2.6cm]{geometry}
\usepackage{graphicx}
\usepackage{float}
\usepackage{booktabs}
\usepackage{longtable}
\usepackage{array}
\usepackage{xcolor}
\usepackage{listings}
\usepackage{caption}
\usepackage{subcaption}
\usepackage{enumitem}
\usepackage{fancyhdr}
\usepackage{titlesec}
\usepackage{hyperref}
\usepackage{amsmath}
\usepackage{amssymb}
\hypersetup{colorlinks=true, linkcolor=blue, urlcolor=blue, citecolor=blue}
\setlength{\parindent}{2em}
\setlength{\parskip}{0.35em}
\linespread{1.28}
\pagestyle{fancy}
\setlength{\headheight}{15pt}
\fancyhf{}
\fancyhead[L]{重庆邮电大学智慧访客预约与出入校管理系统}
\fancyhead[R]{数据库原理课程设计}
\fancyfoot[C]{\thepage}
\ctexset{
  section={format=\Large\bfseries, name={,、}, number=\chinese{section}},
  subsection={format=\large\bfseries},
  subsubsection={format=\normalsize\bfseries}
}
\lstdefinestyle{sqlstyle}{
  language=SQL,
  basicstyle=\ttfamily\small,
  keywordstyle=\color{blue}\bfseries,
  commentstyle=\color{gray},
  stringstyle=\color{teal},
  breaklines=true,
  frame=single,
  numbers=left,
  numberstyle=\tiny\color{gray},
  columns=fullflexible,
  keepspaces=true,
  showstringspaces=false
}
\lstdefinestyle{codestyle}{
  basicstyle=\ttfamily\small,
  keywordstyle=\color{blue}\bfseries,
  commentstyle=\color{gray},
  stringstyle=\color{teal},
  breaklines=true,
  frame=single,
  numbers=left,
  numberstyle=\tiny\color{gray},
  columns=fullflexible,
  keepspaces=true,
  showstringspaces=false
}
\captionsetup{font=small, labelfont=bf}
\setlist{nosep}
'''
(styles / 'report-style.tex').write_text(style, encoding='utf-8')

main = r'''
\documentclass[UTF8,zihao=-4]{ctexart}
\input{styles/report-style.tex}

\title{重庆邮电大学智慧访客预约与出入校管理系统\\数据库原理课程设计报告}
\author{数据库原理期末大作业}
\date{\today}

\begin{document}
\maketitle
\thispagestyle{empty}
\newpage
\tableofcontents
\newpage

\input{chapters/01_system_definition.tex}
\input{chapters/02_requirement_analysis.tex}
\input{chapters/03_system_design.tex}
\input{chapters/04_detail_design.tex}
\input{chapters/05_implementation_testing.tex}
\input{chapters/06_summary.tex}

\bibliographystyle{plain}
\bibliography{references}
\end{document}
'''
(latex / 'main.tex').write_text(main, encoding='utf-8')
(latex / 'references.bib').write_text('% 本课程设计主要引用项目内部文档、数据库脚本和系统运行截图，暂无外部参考文献。\n', encoding='utf-8')
(latex / 'README.md').write_text('''# LaTeX 报告说明

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
''', encoding='utf-8')
(latex / 'build.sh').write_text('''#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
xelatex -interaction=nonstopmode main.tex
xelatex -interaction=nonstopmode main.tex
''', encoding='utf-8')
(latex / 'build.bat').write_text('''@echo off
cd /d %~dp0
xelatex -interaction=nonstopmode main.tex
xelatex -interaction=nonstopmode main.tex
''', encoding='utf-8')

# Keep a copy of SQL files in listings for traceability.
(root / 'database/query_examples.sql').exists() and shutil.copy2(root / 'database/query_examples.sql', listings / 'query_examples.sql')
(root / 'database/schema.sql').exists() and shutil.copy2(root / 'database/schema.sql', listings / 'schema.sql')
print('LaTeX report project generated at', latex)

