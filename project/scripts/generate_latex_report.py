from pathlib import Path
import json
import re
import shutil

root = Path(__file__).resolve().parents[1]
latex = root / 'report-latex'
chapters = latex / 'chapters'
figures = latex / 'figures'
tables_dir = latex / 'tables'
listings = latex / 'listings'
styles = latex / 'styles'
for d in [latex, chapters, figures, tables_dir, listings, styles]:
    d.mkdir(parents=True, exist_ok=True)

try:
    from generate_report_diagrams import generate_all as generate_report_diagram_images
except Exception:
    generate_report_diagram_images = None


SPECIAL = {
    '&': r'\&', '%': r'\%', '$': r'\$', '#': r'\#', '_': r'\_',
    '{': r'\{', '}': r'\}', '~': r'\textasciitilde{}', '^': r'\textasciicircum{}',
    '\\': r'\textbackslash{}'
}

TABLE_ORDER = [
    'sys_user', 'sys_role', 'sys_permission', 'sys_user_role', 'sys_role_permission',
    'department', 'campus_gate', 'visitor', 'visitor_vehicle', 'visitor_companion',
    'visit_apply', 'approval_record', 'pass_code', 'access_record', 'blacklist',
    'notice', 'operation_log', 'dict_type', 'dict_item', 'screenshot_record', 'report_record'
]

SQL_KEEP = {'1','2','3','4','5','6','7','8','9','10','11','13','14','15','18','19','20','21','22'}

SQL_NOTES = {
    '1': '用于访客记录查询页，按手机号回溯访客所有历史预约，关联展示被访部门、被访人、预约状态和访问状态。',
    '2': '用于被访人端待确认列表，保证被访人只处理与本人账号相关的待确认预约。',
    '3': '用于部门审批页，筛选本部门已由被访人确认或进入部门审批阶段的预约申请。',
    '4': '用于门岗和统计页面，动态查询当天已经完成入校登记的访客。',
    '5': '用于当前在校访客页，查询当天已入校但尚未离校的访客记录。',
    '6': '用于超时未离校页面，结合访问状态与计划离校时间识别风险访客。',
    '7': '用于部门访客排行图表，按本月访问申请统计各部门接待量。',
    '8': '用于校门通行统计图表，分别统计本月各校门入校、离校和总通行次数。',
    '9': '用于黑名单管理和预约风险检查，展示仍处于生效状态的限制入校人员。',
    '10': '用于预约详情页，按审批顺序展示某个预约的完整审批链路。',
    '11': '用于预约详情页，联合展示车辆和随行人员信息。',
    '13': '用于首页驾驶舱，汇总今日访客、当前在校、超时未离校、待审批和黑名单风险。',
    '14': '用于近七天访客趋势图，使用递归日期表保证没有访问的日期也能显示为 0。',
    '15': '用于审批通过率统计，计算部门审批通过、拒绝/退回、待处理数量和通过率。',
    '18': '用于访问事由分布图，通过关键词归类展示家长、企业合作、后勤维修、学术交流等访问目的。',
    '19': '用于审批状态分布图，展示预约在待确认、审批通过、审批拒绝、取消等状态上的数量。',
    '20': '用于入校离校状态分布图，统计未入校、已入校、已离校、超时未离校等访问状态。',
    '21': '用于黑名单风险分析，按风险级别和状态统计风险数量。',
    '22': '用于系统日志页，展示最近关键操作记录，支撑系统审计。'
}

def write(path: Path, text: str):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(text.strip() + '\n', encoding='utf-8')

def read(rel: str, default: str = '') -> str:
    p = root / rel
    return p.read_text(encoding='utf-8') if p.exists() else default

def tex_escape(text) -> str:
    if text is None:
        return ''
    return ''.join(SPECIAL.get(ch, ch) for ch in str(text))

def table(title, headers, rows, widths=None):
    col_count = len(headers)
    if widths is None:
        widths = [round(0.94 / col_count, 3)] * col_count
    spec = ''.join([r'>{\raggedright\arraybackslash}p{' + f'{w:.3f}' + r'\textwidth}' for w in widths])
    out = [r'\begin{longtable}{' + spec + '}', rf'\caption{{{tex_escape(title)}}}\\', r'\toprule']
    out.append(' & '.join(tex_escape(h) for h in headers) + r' \\')
    out.append(r'\midrule')
    for row in rows:
        padded = list(row) + [''] * (col_count - len(row))
        out.append(' & '.join(tex_escape(c) for c in padded[:col_count]) + r' \\')
    out.append(r'\bottomrule')
    out.append(r'\end{longtable}')
    return '\n'.join(out)

def itemize(items):
    return '\n'.join([r'\item ' + tex_escape(i) for i in items]).join([r'\begin{itemize}', r'\end{itemize}'])

def split_sql_examples(sql_text):
    parts = []
    matches = list(re.finditer(r'^--\s*(\d+)\.\s*(.+?)。?\s*$', sql_text, flags=re.M))
    for idx, match in enumerate(matches):
        start = match.end()
        end = matches[idx + 1].start() if idx + 1 < len(matches) else len(sql_text)
        body = sql_text[start:end].strip()
        if body:
            parts.append((match.group(1), match.group(2).strip(), body))
    return parts

def involved_tables(sql):
    names = re.findall(r'\b(?:FROM|JOIN|UPDATE|INTO)\s+`?([a-zA-Z_][a-zA-Z0-9_]*)`?', sql, flags=re.I)
    seen = []
    for name in names:
        if name.lower() not in {'select'} and name not in seen:
            seen.append(name)
    return ', '.join(seen) if seen else '由 SQL 语句中的 FROM/JOIN 子句确定'

def parse_schema_tables(schema):
    found = re.findall(r'CREATE TABLE\s+`?([a-zA-Z_][a-zA-Z0-9_]*)`?', schema, flags=re.I)
    ordered = [name for name in TABLE_ORDER if name in found]
    ordered.extend([name for name in found if name not in ordered])
    return ordered

def field_list(schema, table_name, limit=8):
    pattern = re.compile(r'CREATE TABLE\s+`?' + re.escape(table_name) + r'`?\s*\((.*?)\)\s*ENGINE', re.I | re.S)
    m = pattern.search(schema)
    if not m:
        return ''
    fields = []
    for line in m.group(1).splitlines():
        line = line.strip().rstrip(',')
        fm = re.match(r'`?([a-zA-Z_][a-zA-Z0-9_]*)`?\s+([A-Z]+(?:\([^)]*\))?)', line, re.I)
        if fm:
            fields.append(f'{fm.group(1)} {fm.group(2).upper()}')
    return '；'.join(fields[:limit])

def sync_figures():
    for png in (root / 'screenshots').glob('*.png'):
        dst = figures / png.name
        try:
            if dst.exists():
                dst.unlink()
            shutil.copy2(png, dst)
        except PermissionError:
            pass
    manifest = root / 'screenshots/manifest.json'
    if manifest.exists():
        shutil.copy2(manifest, figures / 'screenshot_manifest.json')


def diagram_block(title, source, description, export_cmd=None):
    stem = Path(source).stem
    candidates = [
        figures / f'{stem}.pdf',
        figures / f'{stem}.png',
        root / 'diagrams/export' / f'{stem}.pdf',
        root / 'diagrams/export' / f'{stem}.png',
    ]
    image = next((p for p in candidates if p.exists()), None)
    out = [rf'\subsubsection{{{tex_escape(title)}}}', tex_escape(description)]
    if image:
        target = figures / image.name
        if image.resolve() != target.resolve():
            shutil.copy2(image, target)
        width = r'0.94\textwidth' if image.suffix.lower() == '.png' else r'0.90\textwidth'
        out.extend([
            r'\begin{figure}[H]',
            r'  \centering',
            rf'  \includegraphics[width={width}]{{figures/{target.name}}}',
            rf'  \caption{{{tex_escape(title)}}}',
            r'\end{figure}'
        ])
    else:
        out.append('该图用于说明本节设计关系。若重新编译报告前未生成图像文件，可先运行图形生成脚本，再进行 XeLaTeX 编译。')
    return '\n\n'.join(out)

if generate_report_diagram_images is not None:
    generate_report_diagram_images(root)

sync_figures()
query_sql = read('database/query_examples.sql')
schema_sql = read('database/schema.sql')
manifest_path = root / 'screenshots/manifest.json'
manifest = json.loads(manifest_path.read_text(encoding='utf-8')) if manifest_path.exists() else []
schema_tables = parse_schema_tables(schema_sql)

# 01 system definition
chapter01 = r'''
\section{系统定义}
\subsection{本章概述}
系统定义用于回答“系统要做什么”。本系统面向重庆邮电大学校园访客管理场景，以数据库为核心组织访客、预约、审批、通行凭证、出入校记录、黑名单、权限和日志数据，形成从访客申请到访问归档的完整信息化闭环。
'''
chapter01 += table('系统基本信息', ['项目', '内容'], [
    ['中文系统名称', '重庆邮电大学智慧访客预约与出入校管理系统'],
    ['英文系统名称', 'Smart Visitor Reservation and Campus Access Management System of Chongqing University of Posts and Telecommunications'],
    ['课程设计定位', '数据库原理期末大作业，重点展示需求分析、数据流程、E-R 建模、关系模式、SQL 查询和系统实现。'],
    ['技术形态', 'Vue 3 前端、Spring Boot 3 后端、MySQL 8 数据库、Playwright 自动截图、LaTeX 自动报告。']
], [0.24, 0.70])
chapter01 += r'''
\subsection{设计意图}
系统设计意图是将传统线下访客登记、人工审批和纸质留痕转化为可查询、可追踪、可统计的数据库应用流程。系统以预约申请为主线，将访客身份、被访人确认、部门审批、通行码生成、门岗核验、入校登记、离校登记和访问记录归档统一纳入数据库管理，减少人工信息传递造成的遗漏和延迟。

\subsection{目标功能}
系统需要支持访客预约、黑名单检查、被访人确认、部门审批、通行凭证生成、门岗核验、入校离校登记、当前在校查询、超时未离校识别、访客记录查询、统计报表、用户角色权限管理和操作日志审计等功能。上述功能均需要与数据库表结构、后端接口和前端页面保持一致。
'''
chapter01 += table('系统服务对象', ['服务对象', '主要使用目标'], [
    ['访客', '提交预约申请，查看预约状态和通行凭证。'],
    ['被访人', '确认或拒绝与本人相关的访客预约。'],
    ['部门审批人员', '审批本部门访问申请并填写审批意见。'],
    ['门岗安保人员', '核验通行码，办理入校登记、离校登记和超时处理。'],
    ['系统管理员', '维护用户、角色、权限、部门、校门、黑名单、字典和日志。'],
    ['校级管理人员', '查看全校访客记录和统计报表，为管理决策提供依据。']
], [0.24, 0.70])
chapter01 += r'''
\subsection{数据处理目标}
系统的数据处理目标包括：保证访客身份与预约申请可追溯，保证审批记录按流程留痕，保证通行凭证与出入校记录一一关联，保证黑名单风险可在预约和核验阶段被识别，保证统计报表可按日期、部门、校门、状态和访问事由进行汇总分析。

\subsection{权限访问要求}
系统采用登录认证和 RBAC 权限控制。系统管理员可访问全部功能；被访人仅能处理与本人相关的预约；部门审批人员仅能审批本部门申请；门岗安保人员仅能访问核验、出入校登记和在校访客查询功能；校级管理人员主要查看全校记录和统计报表；访客仅能维护本人预约和查看本人凭证。

\subsection{系统价值}
从校园管理角度看，系统提升了访客预约审批效率和出入校安全控制能力。从数据库课程设计角度看，系统覆盖实体识别、联系分析、关系模式转换、主外键约束、索引设计、典型 SQL 查询、系统实现和测试截图，能够较完整地体现数据库应用系统设计过程。
'''
write(chapters / '01_system_definition.tex', chapter01)

# 02 requirement analysis
roles_rows = [
    ['访客', '预约申请、状态查询、通行凭证查看', '本人访客信息、本人预约、本人通行凭证'],
    ['被访人', '确认或拒绝预约', '本人作为被访人的预约和审批记录'],
    ['部门审批人员', '审批本部门预约', '本部门预约、审批记录和统计数据'],
    ['门岗安保人员', '通行码核验、入校登记、离校登记、在校访客查询', '通行凭证、出入校记录、校门数据'],
    ['系统管理员', '系统配置、用户权限、黑名单、日志维护', '全量管理数据'],
    ['校级管理人员', '访客记录查询和统计报表', '全校汇总数据和记录查询结果']
]
func_rows = [
    ['FR-01', '访客预约申请', '访客填写身份、事由、被访人、时间、车辆和随行人员信息，系统写入 visitor、visit_apply 等表。'],
    ['FR-02', '黑名单检查', '提交预约和门岗核验时按手机号、证件号检查 blacklist 表，识别风险访客。'],
    ['FR-03', '被访人确认', '被访人处理待确认预约，确认后进入部门审批，拒绝后流程终止。'],
    ['FR-04', '部门审批', '部门审批人员处理本部门预约，审批通过后允许生成通行凭证。'],
    ['FR-05', '通行凭证', '审批通过后生成 pass_code，记录通行码、有效期和使用状态。'],
    ['FR-06', '门岗核验', '按预约编号、手机号、证件号或通行码核验访客是否允许入校。'],
    ['FR-07', '出入校登记', '记录入校校门、离校校门、安保人员、时间和访问状态。'],
    ['FR-08', '超时未离校', '识别计划离校时间已过但未离校的访客，并标记风险状态。'],
    ['FR-09', '统计报表', '展示今日、本周、本月、部门排行、校门通行、趋势、审批通过率等指标。'],
    ['FR-10', '系统管理', '维护用户、角色、权限、部门、校门、字典、日志和自动化记录。']
]
perf_rows = [
    ['响应时间', '普通列表、详情和统计查询在演示数据规模下应在 2 秒内返回。'],
    ['并发能力', '满足课程演示和多角色同时操作场景，核心流程接口保持无明显阻塞。'],
    ['可维护性', '字段命名采用小写下划线，接口返回统一结构，前后端字段与数据库字段保持映射一致。'],
    ['可扩展性', '部门、校门、字典、权限等基础数据可扩展，不影响核心预约流程。']
]
run_rows = [
    ['数据库', 'MySQL 8，InnoDB，utf8mb4 字符集。'],
    ['后端', 'JDK 17、Spring Boot 3、MyBatis Plus、JWT、Swagger/OpenAPI。'],
    ['前端', 'Node.js、Vue 3、Vite、Element Plus、Axios、Vue Router、ECharts。'],
    ['自动化', 'Playwright 负责截图，XeLaTeX 负责编译最终报告。']
]
security_rows = [
    ['认证', '登录成功返回 JWT Token，后续请求携带 Authorization 请求头。'],
    ['密码', '默认账号用于演示，后端密码字段按加密摘要存储。'],
    ['授权', '基于角色控制菜单和接口访问范围，越权访问返回明确错误。'],
    ['审计', '登录、预约、审批、出入校、黑名单维护和报表查询写入 operation_log。'],
    ['风险控制', '黑名单访客在预约或核验阶段被拦截或提示风险。']
]
chapter02 = r'''
\section{需求分析}
\subsection{本章概述}
需求分析用于回答“系统必须做什么”。本系统围绕访客预约与出入校管理的完整流程，明确用户角色、功能边界、数据需求、安全要求和运行要求，并通过数据流程图和数据字典为后续数据库建模提供依据。

\subsection{综合需求}
重庆邮电大学日常教学、科研、合作交流、后勤维修、招聘面试、校友返校等场景中存在大量校外人员入校需求。系统必须支持如下主流程：访客提交预约申请 $\rightarrow$ 系统检查黑名单 $\rightarrow$ 被访人确认 $\rightarrow$ 部门审批 $\rightarrow$ 生成通行凭证 $\rightarrow$ 门岗核验 $\rightarrow$ 入校登记 $\rightarrow$ 离校登记 $\rightarrow$ 访问记录归档 $\rightarrow$ 查询统计。系统还必须处理黑名单拦截、被访人拒绝、部门审批拒绝、通行码过期、重复离校和超时未离校等异常场景。
'''
chapter02 += table('用户角色与数据访问范围', ['角色', '主要职责', '数据访问范围'], roles_rows, [0.18, 0.34, 0.42])
chapter02 += r'\subsection{功能需求}' + '\n' + table('核心功能需求表', ['编号', '功能', '需求说明'], func_rows, [0.12, 0.22, 0.60])
chapter02 += r'\subsection{性能需求}' + '\n' + table('性能需求表', ['类别', '要求'], perf_rows, [0.22, 0.72])
chapter02 += r'\subsection{运行需求}' + '\n' + table('运行环境需求表', ['环境', '要求'], run_rows, [0.20, 0.74])
chapter02 += r'\subsection{安全需求}' + '\n' + table('安全需求表', ['安全项', '要求'], security_rows, [0.20, 0.74])
chapter02 += r'''
\subsection{数据需求}
系统必须保存访客信息、预约申请信息、审批记录、通行凭证、出入校记录、黑名单信息、用户角色权限、部门、校门、通知、操作日志、字典项、截图记录和报告记录等数据。所有关键业务数据都应设置主键，跨表关联通过外键或业务字段约束表达；状态字段应具有明确含义，并能支撑前端状态标签和统计报表。

\subsection{数据流程图}
数据流程图从数据流角度描述外部实体、处理过程和数据存储之间的关系。下列图示分别给出系统边界和核心业务处理过程，为后续概念结构设计和逻辑结构设计提供依据。
'''
chapter02 += diagram_block('顶层数据流程图', 'diagrams/data_flow_level0.mmd', '顶层数据流程图展示访客、校内人员、审批人员、门岗和管理人员与系统之间的主要数据交换。', 'mmdc -i diagrams/data_flow_level0.mmd -o report-latex/figures/data_flow_level0.pdf')
chapter02 += diagram_block('二层数据流程图', 'diagrams/data_flow_level1.mmd', '二层数据流程图进一步展开预约审批、门岗核验、出入校登记和统计查询等处理过程。', 'mmdc -i diagrams/data_flow_level1.mmd -o report-latex/figures/data_flow_level1.pdf')
chapter02 += r'\subsection{数据字典}' + '\n'
chapter02 += table('核心数据字典摘要', ['类别', '数据对象', '说明'], [
    ['数据流', '预约申请信息', '访客提交的身份、来访事由、被访人、时间、车辆和随行人员等信息。'],
    ['数据流', '审批结果信息', '被访人确认结果、部门审批结果、审批意见和审批时间。'],
    ['数据存储', 'visit_apply', '保存预约申请主记录，是核心业务主表。'],
    ['数据存储', 'approval_record', '保存被访人确认、部门审批等审批链路。'],
    ['数据存储', 'access_record', '保存入校、离校、异常和超时记录。'],
    ['外部实体', '访客', '预约申请的发起人和通行凭证使用者。'],
    ['外部实体', '门岗安保人员', '通行核验和出入校登记的执行者。'],
    ['核心数据项', 'apply_status', '预约状态，包含待确认、审批通过、审批拒绝、已取消等。'],
    ['核心数据项', 'access_status', '访问状态，包含未入校、已入校、已离校、超时未离校等。'],
    ['核心数据项', 'pass_code', '通行凭证编码，用于门岗核验。']
], [0.18, 0.24, 0.52])
write(chapters / '02_requirement_analysis.tex', chapter02)

# 03 system design
summary_rows = [[name, field_list(schema_sql, name), '数据库物理表，与关系模式和系统实现对象对应'] for name in schema_tables]
chapter03 = r'''
\section{系统设计}
\subsection{本章概述}
系统设计将需求分析结果转换为可实现的数据模型和系统结构。本章包括概念结构设计、E-R 图、逻辑结构设计、关系模式、系统功能模块图和其它设计图，保证概念模型、逻辑结构、系统接口和运行界面之间保持一致。

\subsection{概念结构设计说明}
概念结构设计阶段从业务流程中抽取实体和联系。核心业务实体包括 visitor、visit\_apply、approval\_record、pass\_code、access\_record、blacklist、campus\_gate 等；权限实体包括 sys\_user、sys\_role、sys\_permission 以及用户角色、角色权限中间表；支撑实体包括 notice、operation\_log、dict\_type、dict\_item、screenshot\_record 和 report\_record。设计时将“访客提交预约”作为主线，确保每个实体都能在核心流程中找到业务来源。
'''
chapter03 += table('主要实体及存在原因', ['实体', '存在原因'], [
    ['visitor', '保存访客身份基础信息，避免同一访客多次预约时重复录入。'],
    ['visit_apply', '保存预约申请主记录，连接访客、被访人、部门、车辆、审批和出入校流程。'],
    ['approval_record', '保存被访人确认、部门审批等多阶段审批过程。'],
    ['pass_code', '保存审批通过后的通行凭证和有效期。'],
    ['access_record', '保存门岗核验后的入校、离校和异常记录。'],
    ['blacklist', '保存限制或风险访客，支撑预约和核验阶段的安全检查。'],
    ['sys_user/sys_role/sys_permission', '支撑系统登录认证、角色授权和菜单接口控制。'],
    ['operation_log', '保存关键业务操作日志，支撑审计和测试说明。']
], [0.30, 0.64])
chapter03 += r'''
\subsection{E-R 图}
为了避免一张图实体过多、线条交叉严重，报告将 E-R 图拆分为总体简化 E-R 图、核心业务 E-R 图、用户权限 E-R 图和系统支撑 E-R 图。正文重点展示总体关系和核心业务关系，权限与支撑实体作为补充说明。
'''
chapter03 += diagram_block('总体简化 E-R 图', 'diagrams/er_overview.mmd', '总体简化 E-R 图只展示主要实体和主流程关系，用于报告正文快速说明系统数据模型全貌。', 'mmdc -i diagrams/er_overview.mmd -o report-latex/figures/er_overview.pdf')
chapter03 += diagram_block('核心业务 E-R 图', 'diagrams/er_core_business.mmd', '核心业务 E-R 图围绕访客预约、审批、通行凭证、出入校记录和黑名单展开，是后续关系模式转换的主要依据。', 'mmdc -i diagrams/er_core_business.mmd -o report-latex/figures/er_core_business.pdf')
chapter03 += diagram_block('用户权限 E-R 图', 'diagrams/er_user_permission.mmd', '用户权限 E-R 图展示系统用户、角色、权限、部门及两个多对多中间表之间的联系。', 'mmdc -i diagrams/er_user_permission.mmd -o report-latex/figures/er_user_permission.pdf')
chapter03 += diagram_block('系统支撑 E-R 图', 'diagrams/er_system_support.mmd', '系统支撑 E-R 图展示通知、日志、字典、截图记录和报告记录等支撑性实体。', 'mmdc -i diagrams/er_system_support.mmd -o report-latex/figures/er_system_support.pdf')
chapter03 += r'''
\subsection{逻辑结构设计说明}
逻辑结构设计将概念实体转换为 MySQL 关系表。实体表统一使用 \texttt{id} 作为主键，时间字段统一使用 \texttt{create\_time}、\texttt{update\_time}，软删除字段统一使用 \texttt{deleted}。多对多关系通过 sys\_user\_role 和 sys\_role\_permission 中间表表达；一对多关系通过外键字段或业务关联字段表达；预约、审批、通行和出入校状态通过状态字段表达。
'''
chapter03 += table('逻辑表结构摘要', ['表名', '关键字段摘要', '说明'], summary_rows, [0.22, 0.52, 0.20])
chapter03 += r'''
\subsection{关系模式说明}
系统关系模式与 MySQL 表结构一一对应。核心关系模式包括 visitor(id, visitor\_name, phone, id\_number, ...)、visit\_apply(id, apply\_no, visitor\_id, host\_user\_id, department\_id, apply\_status, access\_status, ...)、approval\_record(id, apply\_id, approval\_step, approval\_result, ...)、pass\_code(id, apply\_id, code, valid\_from, valid\_to, status, ...)、access\_record(id, apply\_id, visitor\_id, entry\_gate\_id, exit\_gate\_id, access\_status, ...)。这些关系模式既能表达实体属性，也能表达核心业务联系。

\subsection{系统功能模块图}
系统功能模块图按角色和业务域划分为访客端、被访人端、审批管理、门岗管理、访客记录、安全管理、统计报表、系统管理和自动化支撑等模块。
'''
chapter03 += diagram_block('系统功能模块图', 'diagrams/system_module.mmd', '模块图展示系统功能分解及各模块边界，便于说明前端菜单、后端接口和权限控制的对应关系。', 'mmdc -i diagrams/system_module.mmd -o report-latex/figures/system_module.pdf')
chapter03 += r'\subsection{其它设计图}'
chapter03 += diagram_block('访客预约审批流程图', 'diagrams/visitor_workflow.mmd', '访客预约审批流程图展示从预约提交到审批通过或拒绝的状态流转，并体现黑名单拦截、被访人拒绝和部门审批拒绝等异常分支。', 'mmdc -i diagrams/visitor_workflow.mmd -o report-latex/figures/visitor_workflow.pdf')
chapter03 += diagram_block('门岗核验入校流程图', 'diagrams/gate_check_workflow.mmd', '门岗核验流程图展示通行码核验、时间有效性检查、黑名单检查、入校登记、离校登记和超时未离校处理。', 'mmdc -i diagrams/gate_check_workflow.mmd -o report-latex/figures/gate_check_workflow.pdf')
chapter03 += diagram_block('系统架构图', 'diagrams/system_architecture.mmd', '系统架构图展示浏览器前端、Spring Boot 后端、MySQL 数据库、Playwright 截图脚本和 LaTeX 报告生成脚本之间的关系。', 'mmdc -i diagrams/system_architecture.mmd -o report-latex/figures/system_architecture.pdf')
chapter03 += diagram_block('数据库表关系图', 'diagrams/table_relation.mmd', '数据库表关系图适合放入附录，用于展示主外键关系和表间依赖。', 'mmdc -i diagrams/table_relation.mmd -o report-latex/figures/table_relation.pdf')
chapter03 += diagram_block('自动截图与报告生成流程图', 'diagrams/automation_report_workflow.mmd', '自动化支撑流程图展示演示数据、页面截图和报告生成之间的衔接关系，说明系统如何形成可复核的课程设计材料。', 'mmdc -i diagrams/automation_report_workflow.mmd -o report-latex/figures/automation_report_workflow.pdf')
write(chapters / '03_system_design.tex', chapter03)

# 04 detail design
chapter04 = r'''
\section{详细设计}
\subsection{本章概述}
详细设计重点说明系统如何通过 SQL 支撑核心业务查询和统计分析。以下查询围绕访客预约、审批流转、门岗通行、风险控制和统计报表展开，体现关系数据库在数据关联、聚合统计和状态筛选方面的设计作用。
'''
for no, title, sql in split_sql_examples(query_sql):
    if no not in SQL_KEEP:
        continue
    chapter04 += rf'''
\subsection{{SQL-{int(no):02d} {tex_escape(title)}}}
\textbf{{查询名称：}}{tex_escape(title)}。\par
\textbf{{查询目的：}}{tex_escape(SQL_NOTES.get(no, title))}\par
\textbf{{涉及数据表：}}\texttt{{{tex_escape(involved_tables(sql))}}}。\par
\textbf{{SQL 语句：}}
\begin{{lstlisting}}[style=sqlstyle]
{sql}
\end{{lstlisting}}
\textbf{{查询说明：}}该查询与系统页面、后端统计接口或课程设计报告中的业务说明保持一致，能够直接基于当前数据库脚本执行。
'''
chapter04 += r'''
\subsection{建表 SQL 摘要}
数据库建表设计在主键、外键、索引、状态字段和时间字段上保持统一规范。以下摘要展示核心建表语句的结构特征，用于说明物理实现与逻辑结构设计之间的对应关系。
\begin{lstlisting}[style=sqlstyle]
''' + '\n'.join(schema_sql.splitlines()[:180]) + r'''
\end{lstlisting}
'''
write(chapters / '04_detail_design.tex', chapter04)

# 05 implementation and testing
test_rows = [
    ['TC-01', '数据库初始化', '执行 scripts/init_database.sh', '成功创建 cqupt_visitor_system 并导入演示数据'],
    ['TC-02', '后端启动', '执行 scripts/run_backend.sh', 'Swagger 可访问，核心接口返回统一结果'],
    ['TC-03', '前端启动', '执行 scripts/run_frontend.sh', '登录页可访问，页面资源加载正常'],
    ['TC-04', '登录认证', '使用 admin/123456 登录', '返回 Token 并进入首页'],
    ['TC-05', '角色菜单', '切换 teacher01、approver01、guard01、manager01', '菜单与角色权限匹配'],
    ['TC-06', '预约申请', '访客提交预约', '生成 visit_apply 记录并进入待确认状态'],
    ['TC-07', '黑名单检查', '使用黑名单手机号或证件号预约/核验', '系统拦截或提示风险'],
    ['TC-08', '被访人确认', 'teacher01 处理待确认预约', '写入 approval_record 并更新状态'],
    ['TC-09', '部门审批', 'approver01 审批本部门预约', '审批通过生成通行凭证，拒绝则终止流程'],
    ['TC-10', '门岗核验', 'guard01 输入通行码或预约编号', '返回允许/拒绝入校结果'],
    ['TC-11', '入校登记', '核验通过后登记入校', '生成或更新 access_record，状态为已入校'],
    ['TC-12', '离校登记', '对已入校访客办理离校', '记录离校时间，禁止重复离校'],
    ['TC-13', '统计报表', '访问首页和统计报表页', '趋势、排行、状态分布等图表均有数据'],
    ['TC-14', '运行界面采集', '执行自动化演示流程', '形成 19 张代表性运行界面截图'],
    ['TC-15', 'LaTeX 报告', '执行 xelatex main.tex 两次', '生成 report-latex/main.pdf']
]
platform_rows = [
    ['数据库', 'MySQL 8', '支持事务、外键、索引、utf8mb4 字符集，适合课程设计展示关系模型。'],
    ['后端', 'Spring Boot 3 + MyBatis Plus + JWT', '便于快速构建 REST API、权限认证、统一返回和数据访问层。'],
    ['前端', 'Vue 3 + Vite + Element Plus + ECharts', '适合构建课程答辩演示页面、表格表单和统计图表。'],
    ['截图', 'Playwright / 系统 Chrome', '可自动登录系统并截图，保证报告运行界面一致。'],
    ['报告', 'LaTeX + XeLaTeX', '中文排版稳定，适合插入长表格、SQL 代码和运行截图。']
]
chapter05 = r'''
\section{系统实现与测试}
\subsection{本章概述}
本章说明系统的开发平台、后端实现、前端实现、数据库实现、自动截图方案和测试结果，并插入代表性运行界面截图。截图由脚本自动生成，能够直接服务于课程设计报告和答辩展示。

\subsection{开发平台和工具选择}
'''
chapter05 += table('开发平台和工具选择', ['层次', '技术', '选择原因'], platform_rows, [0.16, 0.28, 0.50])
chapter05 += r'''
\subsection{后端实现说明}
后端采用 Spring Boot 3 分层结构，包名为 \texttt{edu.cqupt.visitor}，主要包含 Entity、Mapper、Service、ServiceImpl、Controller、DTO、配置类和安全认证模块。接口统一以 \texttt{/api} 为前缀，返回统一结果结构，并通过 JWT 和角色权限控制保护核心接口。

\subsection{前端实现说明}
前端采用 Vue 3 和 Element Plus，包含登录页、首页统计驾驶舱、访客预约、我的预约、待确认、部门审批、门岗核验、出入校登记、当前在校、超时未离校、黑名单、访客记录、统计报表、用户管理、角色权限、部门管理、校门管理和系统日志等页面。页面字段与后端接口和数据库状态值保持一致。

\subsection{数据库实现说明}
数据库采用 MySQL 8 作为持久化平台，围绕用户权限、访客预约、审批流转、通行凭证、出入校记录、黑名单、通知消息和操作日志等对象建立关系表。核心表均设置主键、必要外键、业务唯一约束、索引、状态字段和时间字段，以保证数据完整性、查询效率和业务流转可追踪性。

\subsection{自动截图说明}
系统提供自动化截图能力，用于在统一演示数据基础上记录登录、首页驾驶舱、预约申请、审批处理、门岗核验、出入校登记、黑名单管理、统计报表和系统管理等代表性界面。该过程保证报告中的运行截图来自同一套业务数据，便于审查系统功能的完整性和一致性。

\subsection{测试方法}
测试采用数据库初始化测试、接口启动测试、前端页面测试、角色权限测试、核心流程测试、统计报表测试、自动截图测试和 LaTeX 报告编译测试相结合的方式。重点验证数据结构、业务状态、接口响应、页面展示和报告材料之间是否保持一致。
'''
chapter05 += table('系统测试用例摘要', ['编号', '测试名称', '操作/输入', '预期结果'], test_rows, [0.12, 0.20, 0.34, 0.28])
chapter05 += r'''
\subsection{代表性运行界面}
以下运行界面选取系统中最具代表性的功能页面，展示登录认证、首页统计、访客预约、审批处理、门岗核验、出入校登记、风险管理、统计分析和系统维护等功能的实现效果。
'''
for item in manifest:
    title = item.get('title') or item.get('pageName') or item.get('screenshotCode')
    desc = item.get('description') or ''
    file_name = Path(item.get('latexFile') or item.get('file') or item.get('filePath') or '').name
    if not file_name:
        continue
    chapter05 += f'\n{tex_escape(desc)}\n\n'
    chapter05 += r'\begin{figure}[H]' + '\n'
    chapter05 += r'  \centering' + '\n'
    chapter05 += rf'  \includegraphics[width=0.86\textwidth]{{figures/{file_name}}}' + '\n'
    chapter05 += rf'  \caption{{{tex_escape(title)}}}' + '\n'
    chapter05 += r'\end{figure}' + '\n'
write(chapters / '05_implementation_testing.tex', chapter05)

chapter06 = r'''
\section{总结}
本课程设计围绕“重庆邮电大学智慧访客预约与出入校管理系统”完成了从需求分析、概念结构设计、逻辑结构设计、MySQL 建库建表、后端接口、前端页面、权限控制、核心业务流程、统计报表、自动截图到 LaTeX 报告生成的完整实践。系统以访客预约为主线，将访客信息、预约申请、审批记录、通行凭证、出入校记录、黑名单、用户权限和系统日志统一纳入数据库管理，能够较好地体现数据库系统在校园安全管理场景中的建模、约束、查询和应用价值。

在数据库设计方面，系统明确了实体、关系、主键、外键、唯一约束、索引和状态字段，核心表结构能够覆盖从预约提交到访问归档的完整流程。在系统实现方面，后端采用 Spring Boot 3、MyBatis Plus、MySQL 和 JWT，前端采用 Vue 3、Element Plus、Axios、Vue Router 和 ECharts，页面能够展示预约、审批、核验、出入校、黑名单、统计报表和系统管理等核心功能。在自动化方面，项目提供 Playwright 截图脚本和 LaTeX 报告项目，便于将运行界面、SQL、图表和测试说明整合为最终课程设计报告。

总体来看，本系统从数据库课程设计要求出发，完成了系统定义、需求分析、数据流程分析、概念结构设计、逻辑结构设计、SQL 详细设计、系统实现和测试验证等环节。设计结果能够覆盖校园访客预约与出入校管理的主要业务场景，报告中的图表、关系模式、SQL 查询和运行界面能够共同说明系统的数据组织方式与实际运行效果。
'''
write(chapters / '06_summary.tex', chapter06)

style = r'''
\usepackage[a4paper, left=2.5cm, right=2.3cm, top=2.5cm, bottom=2.5cm]{geometry}
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
\linespread{1.25}
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
\setlength{\LTpre}{0.4em}
\setlength{\LTpost}{0.6em}
\setlength{\tabcolsep}{3pt}
\sloppy
\emergencystretch=2em
\lstdefinestyle{sqlstyle}{
  language=SQL,
  basicstyle=\ttfamily\footnotesize,
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
  basicstyle=\ttfamily\footnotesize,
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
write(styles / 'report-style.tex', style)

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
write(latex / 'main.tex', main)
write(latex / 'references.bib', '% 本课程设计主要引用项目内部文档、数据库脚本和系统运行截图，暂无外部参考文献。')
write(latex / 'README.md', '''# LaTeX 课程设计报告

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
''')
write(latex / 'build.sh', '''#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
xelatex -interaction=nonstopmode main.tex
xelatex -interaction=nonstopmode main.tex
''')
write(latex / 'build.bat', '''@echo off
cd /d %~dp0
xelatex -interaction=nonstopmode main.tex
xelatex -interaction=nonstopmode main.tex
''')
if (root / 'database/query_examples.sql').exists():
    shutil.copy2(root / 'database/query_examples.sql', listings / 'query_examples.sql')
if (root / 'database/schema.sql').exists():
    shutil.copy2(root / 'database/schema.sql', listings / 'schema.sql')
print('LaTeX report project generated at', latex)