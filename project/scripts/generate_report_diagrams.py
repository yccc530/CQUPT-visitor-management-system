from pathlib import Path
from math import atan2, cos, sin, pi
from PIL import Image, ImageDraw, ImageFont
import shutil

ROOT = Path(__file__).resolve().parents[1]
EXPORT_DIR = ROOT / 'diagrams' / 'export'
FIGURE_DIR = ROOT / 'report-latex' / 'figures'

BLUE = '#1f6feb'
DARK = '#1f2937'
MUTED = '#64748b'
LINE = '#334155'
BG = '#f8fbff'
CARD = '#ffffff'
GREEN = '#16a34a'
AMBER = '#d97706'
RED = '#dc2626'
PURPLE = '#7c3aed'
CYAN = '#0891b2'
GRAY = '#e5e7eb'


def _font(size=28, bold=False):
    candidates = [
        r'C:\Windows\Fonts\msyhbd.ttc' if bold else r'C:\Windows\Fonts\msyh.ttc',
        r'C:\Windows\Fonts\simhei.ttf',
        r'C:\Windows\Fonts\simsun.ttc',
        r'C:\Windows\Fonts\arial.ttf',
    ]
    for item in candidates:
        try:
            return ImageFont.truetype(item, size=size)
        except Exception:
            continue
    return ImageFont.load_default()


FONT = _font(28)
FONT_SM = _font(22)
FONT_XS = _font(18)
FONT_BOLD = _font(30, True)
FONT_TITLE = _font(42, True)


def text_size(draw, text, font):
    box = draw.textbbox((0, 0), text, font=font)
    return box[2] - box[0], box[3] - box[1]


def wrap_text(draw, text, font, max_width):
    lines = []
    current = ''
    for ch in text:
        test = current + ch
        if text_size(draw, test, font)[0] <= max_width or not current:
            current = test
        else:
            lines.append(current)
            current = ch
    if current:
        lines.append(current)
    return lines


class Diagram:
    def __init__(self, width, height, title):
        self.img = Image.new('RGB', (width, height), BG)
        self.draw = ImageDraw.Draw(self.img)
        self.width = width
        self.height = height
        self.title = title
        self.draw.rounded_rectangle((24, 24, width - 24, 100), radius=24, fill='#eaf3ff', outline='#b7d4ff', width=2)
        tw, th = text_size(self.draw, title, FONT_TITLE)
        self.draw.text(((width - tw) / 2, 42), title, fill='#0f172a', font=FONT_TITLE)

    def box(self, xy, text, fill=CARD, outline=BLUE, font=None, radius=18, width=3, color=DARK):
        font = font or FONT
        x1, y1, x2, y2 = xy
        self.draw.rounded_rectangle(xy, radius=radius, fill=fill, outline=outline, width=width)
        lines = wrap_text(self.draw, text, font, max(40, x2 - x1 - 28))
        line_h = text_size(self.draw, '测', font)[1] + 8
        total_h = len(lines) * line_h
        y = y1 + (y2 - y1 - total_h) / 2
        for line in lines:
            tw, _ = text_size(self.draw, line, font)
            self.draw.text((x1 + (x2 - x1 - tw) / 2, y), line, fill=color, font=font)
            y += line_h

    def small_label(self, x, y, text, fill=MUTED, font=None):
        self.draw.text((x, y), text, fill=fill, font=font or FONT_SM)

    def line(self, p1, p2, color=LINE, width=3, label=None, label_pos=0.5):
        self.draw.line((p1, p2), fill=color, width=width)
        self._arrow_head(p1, p2, color)
        if label:
            lx = p1[0] + (p2[0] - p1[0]) * label_pos
            ly = p1[1] + (p2[1] - p1[1]) * label_pos
            self.tag(lx, ly, label)

    def polyline(self, points, color=LINE, width=3, label=None):
        self.draw.line(points, fill=color, width=width, joint='curve')
        self._arrow_head(points[-2], points[-1], color)
        if label:
            mid = points[len(points)//2]
            self.tag(mid[0], mid[1], label)

    def _arrow_head(self, p1, p2, color):
        angle = atan2(p2[1] - p1[1], p2[0] - p1[0])
        size = 16
        pts = [
            p2,
            (p2[0] - size * cos(angle - pi / 7), p2[1] - size * sin(angle - pi / 7)),
            (p2[0] - size * cos(angle + pi / 7), p2[1] - size * sin(angle + pi / 7)),
        ]
        self.draw.polygon(pts, fill=color)

    def tag(self, x, y, text):
        font = FONT_XS
        tw, th = text_size(self.draw, text, font)
        pad = 8
        self.draw.rounded_rectangle((x - tw/2 - pad, y - th/2 - pad, x + tw/2 + pad, y + th/2 + pad), radius=8, fill='#ffffff', outline='#cbd5e1')
        self.draw.text((x - tw/2, y - th/2 - 1), text, fill=MUTED, font=font)

    def group(self, xy, title, outline='#cbd5e1'):
        x1, y1, x2, y2 = xy
        self.draw.rounded_rectangle(xy, radius=24, fill='#ffffff', outline=outline, width=2)
        self.draw.text((x1 + 20, y1 + 14), title, fill=outline if outline != '#cbd5e1' else MUTED, font=FONT_SM)

    def save(self, name):
        EXPORT_DIR.mkdir(parents=True, exist_ok=True)
        FIGURE_DIR.mkdir(parents=True, exist_ok=True)
        export = EXPORT_DIR / f'{name}.png'
        figure = FIGURE_DIR / f'{name}.png'
        self.img.save(export)
        shutil.copy2(export, figure)
        return export


def data_flow_level0():
    d = Diagram(1800, 1100, '顶层数据流程图')
    d.box((690, 390, 1110, 570), '智慧访客预约与出入校管理系统', fill='#eaf3ff', outline=BLUE, font=FONT_BOLD)
    entities = [
        ('访客', (90, 180, 310, 270), (690, 430), '预约申请/身份信息'),
        ('被访人', (780, 170, 1020, 260), (900, 390), '确认意见'),
        ('部门审批人员', (1440, 180, 1700, 270), (1110, 430), '审批意见'),
        ('门岗安保人员', (1440, 650, 1700, 740), (1110, 520), '核验与登记'),
        ('校级管理人员', (780, 850, 1040, 940), (930, 570), '统计查询'),
        ('系统管理员', (90, 650, 330, 740), (690, 520), '基础数据维护'),
    ]
    for name, box, target, label in entities:
        d.box(box, name, fill='#ffffff', outline=CYAN, font=FONT_BOLD)
        sx = (box[0] + box[2]) // 2
        sy = (box[1] + box[3]) // 2
        d.line((sx, sy), target, label=label)
    stores = [
        ('D1 访客与预约数据', (160, 950, 430, 1030)),
        ('D2 审批与通行数据', (520, 950, 790, 1030)),
        ('D3 出入校记录数据', (880, 950, 1150, 1030)),
        ('D4 用户权限基础数据', (1240, 950, 1510, 1030)),
    ]
    for name, box in stores:
        d.box(box, name, fill='#f8fafc', outline='#94a3b8', font=FONT_SM, radius=12)
        d.line((900, 570), ((box[0]+box[2])//2, box[1]), color='#64748b')
    return d.save('data_flow_level0')


def data_flow_level1():
    d = Diagram(2100, 1250, '预约审批与出入校数据流程图')
    processes = [
        ('P1 预约受理\n黑名单检查', (170, 390, 450, 520), BLUE),
        ('P2 被访人确认\n部门审批', (630, 390, 930, 520), PURPLE),
        ('P3 通行凭证\n门岗核验', (1110, 390, 1410, 520), GREEN),
        ('P4 出入校登记\n记录归档', (1590, 390, 1890, 520), AMBER),
    ]
    for text, box, color in processes:
        d.box(box, text, fill='#ffffff', outline=color, font=FONT_BOLD)
    d.line((450, 455), (630, 455), label='待确认申请')
    d.line((930, 455), (1110, 455), label='审批通过')
    d.line((1410, 455), (1590, 455), label='核验结果')
    top = [('访客', (170, 190, 450, 280)), ('被访人', (640, 190, 900, 280)), ('审批人员', (1120, 190, 1380, 280)), ('门岗安保', (1600, 190, 1860, 280))]
    for name, box in top:
        d.box(box, name, fill='#eef6ff', outline=CYAN, font=FONT_BOLD)
        d.line(((box[0]+box[2])//2, box[3]), ((box[0]+box[2])//2, 390), label='数据输入')
    stores = [
        ('visitor\n访客信息', (120, 780, 360, 890)),
        ('visit_apply\n预约申请', (440, 780, 680, 890)),
        ('approval_record\n审批记录', (760, 780, 1030, 890)),
        ('pass_code\n通行凭证', (1110, 780, 1350, 890)),
        ('access_record\n出入校记录', (1430, 780, 1700, 890)),
        ('blacklist\n黑名单', (1780, 780, 2000, 890)),
    ]
    for text, box in stores:
        d.box(box, text, fill='#f8fafc', outline='#94a3b8', font=FONT_SM, radius=12)
    for p, s in [((310,520),(240,780)), ((310,520),(560,780)), ((780,520),(895,780)), ((1260,520),(1230,780)), ((1740,520),(1565,780)), ((310,520),(1890,780))]:
        d.line(p, s, color='#64748b')
    d.box((760, 1010, 1340, 1110), '统计报表读取预约、审批、通行、出入校和风险数据，形成趋势、排行与状态分布。', fill='#fff7ed', outline=AMBER, font=FONT_SM)
    d.line((1030, 890), (1050, 1010), color=AMBER)
    d.line((1230, 890), (1130, 1010), color=AMBER)
    d.line((1565, 890), (1230, 1010), color=AMBER)
    return d.save('data_flow_level1')


def er_core_business():
    d = Diagram(2000, 1150, '核心业务 E-R 图')
    boxes = {
        'visitor': (120, 280, 390, 390),
        'visitor_vehicle': (110, 490, 390, 590),
        'visitor_companion': (110, 690, 390, 790),
        'blacklist': (120, 880, 390, 980),
        'visit_apply': (820, 420, 1180, 570),
        'approval_record': (790, 700, 1210, 830),
        'pass_code': (1530, 280, 1840, 390),
        'access_record': (1510, 540, 1860, 670),
        'campus_gate': (1530, 820, 1840, 930),
    }
    labels = {
        'visitor': 'visitor\n访客', 'visitor_vehicle': 'visitor_vehicle\n车辆', 'visitor_companion': 'visitor_companion\n随行人员',
        'blacklist': 'blacklist\n黑名单', 'visit_apply': 'visit_apply\n预约申请', 'approval_record': 'approval_record\n审批记录',
        'pass_code': 'pass_code\n通行凭证', 'access_record': 'access_record\n出入校记录', 'campus_gate': 'campus_gate\n校门'
    }
    for k, box in boxes.items():
        color = BLUE if k == 'visit_apply' else (RED if k == 'blacklist' else CYAN if k in ['pass_code','access_record','campus_gate'] else GREEN if k == 'approval_record' else '#64748b')
        d.box(box, labels[k], fill='#ffffff', outline=color, font=FONT_BOLD if k == 'visit_apply' else FONT_SM)
    d.line((390, 335), (820, 470), label='1:N 提交')
    d.line((390, 540), (820, 500), label='0:1 关联')
    d.line((390, 740), (820, 530), label='1:N 包含')
    d.line((390, 930), (820, 555), color=RED, label='风险检查')
    d.line((1000, 570), (1000, 700), label='1:N 产生')
    d.line((1180, 470), (1530, 335), label='1:1 生成')
    d.line((1180, 540), (1510, 600), label='1:N 登记')
    d.line((1685, 670), (1685, 820), label='N:1 校门')
    d.box((760, 980, 1260, 1050), '预约申请是核心实体，连接访客、审批、通行凭证和出入校记录。', fill='#eaf3ff', outline=BLUE, font=FONT_SM)
    return d.save('er_core_business')


def er_overview():
    d = Diagram(2100, 1200, '总体简化 E-R 图')
    d.group((80, 160, 500, 980), '用户权限域', BLUE)
    d.group((600, 160, 1120, 980), '访客预约域', GREEN)
    d.group((1220, 160, 1640, 980), '通行核验域', CYAN)
    d.group((1740, 160, 2020, 980), '系统支撑域', PURPLE)
    nodes = [
        ('department', (170,250,410,330)), ('sys_user', (170,420,410,500)), ('sys_role', (170,590,410,670)), ('sys_permission', (145,760,435,840)),
        ('visitor', (720,250,1000,330)), ('visit_apply', (700,430,1020,530)), ('approval_record', (680,640,1040,740)), ('blacklist', (735,820,985,900)),
        ('pass_code', (1290,290,1570,370)), ('access_record', (1280,520,1580,620)), ('campus_gate', (1290,760,1570,840)),
        ('notice', (1790,270,1970,340)), ('operation_log', (1770,440,1995,510)), ('dict_item', (1790,610,1970,680)), ('report_record', (1770,780,1995,850)),
    ]
    for name, box in nodes:
        d.box(box, name, fill='#ffffff', outline='#64748b', font=FONT_SM)
    for p1,p2,label in [
        ((290,330),(290,420),'包含用户'), ((290,500),(290,590),'用户角色'), ((290,670),(290,760),'角色权限'),
        ((1000,290),(1290,330),'生成凭证'), ((1020,490),(1280,570),'产生记录'), ((1430,620),(1430,760),'关联校门'),
        ((1000,330),(1000,430),'提交预约'), ((860,530),(860,640),'审批过程'), ((735,860),(700,500),'风险检查'),
        ((410,460),(700,470),'被访/审批'), ((410,460),(1770,470),'记录操作'), ((1020,470),(1790,305),'发送通知')
    ]:
        d.line(p1,p2,label=label)
    return d.save('er_overview')


def er_user_permission():
    d = Diagram(1700, 800, '用户权限 E-R 图')
    nodes = [
        ('department\n部门', (80, 330, 300, 440), BLUE),
        ('sys_user\n系统用户', (430, 330, 670, 440), BLUE),
        ('sys_user_role\n用户角色', (800, 330, 1060, 440), PURPLE),
        ('sys_role\n角色', (1190, 200, 1430, 310), PURPLE),
        ('sys_role_permission\n角色权限', (1160, 480, 1460, 590), AMBER),
        ('sys_permission\n权限', (1510, 480, 1660, 590), AMBER),
    ]
    for text, box, color in nodes:
        d.box(box, text, outline=color, fill='#ffffff', font=FONT_SM)
    d.line((300,385),(430,385), label='1:N')
    d.line((670,385),(800,385), label='1:N')
    d.line((1060,385),(1190,255), label='N:1')
    d.line((1310,310),(1310,480), label='1:N')
    d.line((1460,535),(1510,535), label='N:1')
    d.box((500,650,1220,720), '通过用户角色表和角色权限表实现多对多授权，保证不同岗位只能访问相应功能。', fill='#eef6ff', outline=BLUE, font=FONT_SM)
    return d.save('er_user_permission')


def er_system_support():
    d = Diagram(1700, 900, '系统支撑 E-R 图')
    nodes = [
        ('sys_user\n系统用户', (710, 170, 990, 280), BLUE),
        ('notice\n通知消息', (180, 390, 440, 500), GREEN),
        ('operation_log\n操作日志', (590, 390, 870, 500), AMBER),
        ('screenshot_record\n截图记录', (1030, 390, 1330, 500), CYAN),
        ('report_record\n报告记录', (1340, 620, 1600, 730), CYAN),
        ('dict_type\n字典类型', (250, 650, 520, 760), PURPLE),
        ('dict_item\n字典项', (660, 650, 930, 760), PURPLE),
    ]
    for text, box, color in nodes:
        d.box(box, text, fill='#ffffff', outline=color, font=FONT_SM)
    d.line((850,280),(310,390), label='接收/发布')
    d.line((850,280),(730,390), label='产生')
    d.line((850,280),(1180,390), label='触发')
    d.line((1180,500),(1470,620), label='支撑报告')
    d.line((520,705),(660,705), label='1:N')
    d.box((470, 805, 1240, 860), '系统支撑实体用于消息提醒、操作审计、字典维护、截图记录和报告生成留痕。', fill='#f8fafc', outline='#94a3b8', font=FONT_SM)
    return d.save('er_system_support')


def system_module():
    d = Diagram(2200, 1400, '系统功能模块图')
    d.box((760, 145, 1440, 235), '重庆邮电大学智慧访客预约与出入校管理系统', fill='#eaf3ff', outline=BLUE, font=FONT_BOLD)
    modules = [
        ('访客端', ['访客预约','状态查询','通行凭证','历史记录'], BLUE),
        ('被访人端', ['待确认预约','确认预约','拒绝预约','接待记录'], CYAN),
        ('审批管理', ['部门待审批','审批通过','审批拒绝','审批轨迹'], PURPLE),
        ('门岗管理', ['通行码核验','入校登记','离校登记','当前在校','超时未离校'], GREEN),
        ('访客记录', ['预约记录查询','出入校记录','车辆随行人员','审批记录'], '#64748b'),
        ('安全管理', ['黑名单管理','风险检查','异常处理','日志审计'], RED),
        ('统计报表', ['访客概览','趋势分析','部门排行','校门统计','审批分析'], AMBER),
        ('系统管理', ['用户管理','角色权限','部门管理','校门管理','字典管理'], '#0f766e'),
        ('自动化支撑', ['自动截图','截图清单','报告生成','报告记录'], '#9333ea'),
    ]
    start_x, start_y = 80, 360
    col_w, row_h = 220, 120
    gap_x = 20
    for i, (name, subs, color) in enumerate(modules):
        x = start_x + i * (col_w + gap_x)
        d.box((x, start_y, x + col_w, start_y + 82), name, fill='#ffffff', outline=color, font=FONT_BOLD)
        d.line((1100, 235), (x + col_w // 2, start_y), color=color)
        y = start_y + 130
        for sub in subs:
            d.box((x, y, x + col_w, y + 76), sub, fill='#f8fafc', outline='#cbd5e1', font=FONT_SM, radius=12, width=2)
            d.line((x + col_w // 2, start_y + 82), (x + col_w // 2, y), color='#94a3b8', width=2)
            y += 92
    return d.save('system_module')


def visitor_workflow():
    d = Diagram(2100, 1000, '访客预约审批流程图')
    steps = [
        ('访客提交预约', (90, 430, 310, 520), BLUE),
        ('黑名单检查', (420, 430, 640, 520), RED),
        ('被访人确认', (750, 430, 970, 520), CYAN),
        ('部门审批', (1080, 430, 1300, 520), PURPLE),
        ('生成通行码', (1410, 430, 1630, 520), GREEN),
        ('门岗核验', (1740, 430, 1960, 520), AMBER),
    ]
    for text, box, color in steps:
        d.box(box, text, outline=color, fill='#ffffff', font=FONT_SM)
    for a,b in zip(steps, steps[1:]):
        d.line((a[1][2],475),(b[1][0],475))
    lower = [('入校登记',(1410,700,1630,790),GREEN),('离校登记',(1080,700,1300,790),GREEN),('访问记录归档',(750,700,970,790),BLUE),('查询统计',(420,700,640,790),BLUE)]
    for text, box, color in lower:
        d.box(box,text,outline=color,fill='#ffffff',font=FONT_SM)
    d.line((1850,520),(1520,700),label='允许入校')
    d.line((1410,745),(1300,745))
    d.line((1080,745),(970,745))
    d.line((750,745),(640,745))
    rejects = [('黑名单拦截',(420,220,640,300)),('被访人拒绝',(750,220,970,300)),('审批拒绝/退回',(1080,220,1300,300)),('通行码过期',(1740,220,1960,300)),('超时未离校预警',(1410,850,1710,930))]
    for text, box in rejects:
        d.box(box,text,outline=RED,fill='#fff1f2',font=FONT_SM)
    d.line((530,430),(530,300),color=RED)
    d.line((860,430),(860,300),color=RED)
    d.line((1190,430),(1190,300),color=RED)
    d.line((1850,430),(1850,300),color=RED)
    d.line((1520,790),(1560,850),color=RED)
    return d.save('visitor_workflow')


def gate_check_workflow():
    d = Diagram(1900, 950, '门岗核验与入校流程图')
    steps = [
        ('输入通行码/手机号/证件号', (80,400,360,500), BLUE),
        ('查询预约与凭证', (480,400,730,500), CYAN),
        ('校验审批状态', (850,400,1100,500), PURPLE),
        ('校验访问时间', (1220,400,1470,500), AMBER),
        ('黑名单复核', (1590,400,1820,500), RED),
    ]
    for text,box,color in steps:
        d.box(box,text,outline=color,fill='#ffffff',font=FONT_SM)
    for a,b in zip(steps, steps[1:]):
        d.line((a[1][2],450),(b[1][0],450))
    d.box((1220,680,1470,780),'允许入校\n写入入校记录',outline=GREEN,fill='#f0fdf4',font=FONT_SM)
    d.box((820,680,1100,780),'拒绝入校\n记录异常原因',outline=RED,fill='#fff1f2',font=FONT_SM)
    d.box((1530,680,1810,780),'离校登记\n更新访问状态',outline=GREEN,fill='#f0fdf4',font=FONT_SM)
    d.line((1705,500),(1345,680),label='核验通过')
    d.line((1345,780),(1530,730),label='访问结束')
    for x in [975,1345,1705]:
        d.line((x,400),(960,680),color=RED,label='异常')
    d.box((500, 180, 1380, 280), '门岗核验必须同时满足审批通过、凭证有效、访问时间有效、未命中黑名单等条件。', fill='#eaf3ff', outline=BLUE, font=FONT_SM)
    return d.save('gate_check_workflow')


def system_architecture():
    d = Diagram(1900, 1050, '系统架构图')
    d.box((120,220,460,340),'浏览器前端\nVue 3 / Element Plus / ECharts',outline=BLUE,fill='#ffffff',font=FONT_SM)
    d.box((760,220,1140,340),'Spring Boot 后端\n认证授权 / 业务服务 / REST API',outline=GREEN,fill='#ffffff',font=FONT_SM)
    d.box((1450,220,1780,340),'MySQL 数据库\n业务表 / 权限表 / 日志表',outline=AMBER,fill='#ffffff',font=FONT_SM)
    d.box((760,520,1140,640),'Playwright 自动截图\n登录访问核心页面',outline=CYAN,fill='#ffffff',font=FONT_SM)
    d.box((1450,520,1780,640),'截图与图形资源\nscreenshots / diagrams',outline=PURPLE,fill='#ffffff',font=FONT_SM)
    d.box((760,800,1140,920),'LaTeX 报告生成\n章节 / SQL / 截图 / 图表',outline=RED,fill='#ffffff',font=FONT_SM)
    d.line((460,280),(760,280),label='HTTP / API')
    d.line((1140,280),(1450,280),label='SQL / ORM')
    d.line((950,520),(950,340),label='调用页面')
    d.line((1140,580),(1450,580),label='生成图片')
    d.line((1615,640),(1140,850),label='插入报告')
    d.line((950,640),(950,800),label='截图说明')
    d.box((200,760,520,880),'课程设计交付\n系统代码、数据库脚本、截图、PDF 报告',outline=BLUE,fill='#eaf3ff',font=FONT_SM)
    d.line((760,860),(520,820),label='汇总提交')
    return d.save('system_architecture')


def table_relation():
    d = Diagram(2200, 1300, '数据库表关系图')
    groups = [
        ('权限基础表', (80,160,560,520), BLUE, ['department','sys_user','sys_role','sys_permission','sys_user_role','sys_role_permission']),
        ('访客预约表', (680,160,1220,620), GREEN, ['visitor','visitor_vehicle','visitor_companion','visit_apply','approval_record','blacklist']),
        ('通行记录表', (1340,160,1780,580), CYAN, ['pass_code','access_record','campus_gate']),
        ('系统支撑表', (80,760,720,1120), PURPLE, ['notice','operation_log','dict_type','dict_item','screenshot_record','report_record']),
    ]
    positions = {}
    for title, area, color, tables in groups:
        d.group(area,title,color)
        x1,y1,x2,y2 = area
        col_w = (x2-x1-60)//2
        for i,t in enumerate(tables):
            x = x1+30+(i%2)*(col_w+20)
            y = y1+70+(i//2)*90
            box=(x,y,x+col_w,y+58)
            positions[t]=box
            d.box(box,t,outline='#94a3b8',fill='#ffffff',font=FONT_XS,radius=10,width=2)
    def center(t):
        b=positions[t]; return ((b[0]+b[2])//2,(b[1]+b[3])//2)
    links=[('department','sys_user'),('sys_user','sys_user_role'),('sys_role','sys_user_role'),('sys_role','sys_role_permission'),('sys_permission','sys_role_permission'),('visitor','visit_apply'),('visit_apply','visitor_vehicle'),('visit_apply','visitor_companion'),('visit_apply','approval_record'),('visitor','blacklist'),('visit_apply','pass_code'),('visit_apply','access_record'),('campus_gate','access_record'),('sys_user','operation_log'),('dict_type','dict_item'),('screenshot_record','report_record')]
    for a,b in links:
        d.line(center(a), center(b), color='#64748b', width=2)
    d.box((920, 900, 1660, 1040), '表关系围绕预约申请展开：权限表控制访问边界，访客预约表记录业务过程，通行记录表沉淀出入校事实，系统支撑表完成日志、字典、截图和报告留痕。', fill='#f8fafc', outline='#94a3b8', font=FONT_SM)
    return d.save('table_relation')


def automation_report_workflow():
    d = Diagram(1800, 900, '自动截图与报告生成流程图')
    steps = [('初始化演示数据',(90,390,330,490),BLUE),('启动后端服务',(450,390,690,490),GREEN),('启动前端服务',(810,390,1050,490),CYAN),('Playwright 截图',(1170,390,1410,490),PURPLE),('生成 LaTeX 报告',(1530,390,1740,490),RED)]
    for text,box,color in steps:
        d.box(box,text,outline=color,fill='#ffffff',font=FONT_SM)
    for a,b in zip(steps,steps[1:]):
        d.line((a[1][2],440),(b[1][0],440))
    d.box((1170,650,1410,740),'运行截图\n19 个核心页面',outline=PURPLE,fill='#faf5ff',font=FONT_SM)
    d.box((1530,650,1740,740),'PDF 报告\n图表与截图',outline=RED,fill='#fff1f2',font=FONT_SM)
    d.line((1290,490),(1290,650),label='输出')
    d.line((1410,695),(1530,695),label='插入')
    d.box((430,160,1370,260),'自动化流程保证数据库演示数据、前端页面截图和最终课程设计报告保持一致。',fill='#eaf3ff',outline=BLUE,font=FONT_SM)
    return d.save('automation_report_workflow')


def generate_all(root=None):
    EXPORT_DIR.mkdir(parents=True, exist_ok=True)
    FIGURE_DIR.mkdir(parents=True, exist_ok=True)
    data_flow_level0(); data_flow_level1(); er_core_business(); er_overview(); er_user_permission(); er_system_support()
    system_module(); visitor_workflow(); gate_check_workflow(); system_architecture(); table_relation(); automation_report_workflow()


if __name__ == '__main__':
    generate_all()
    print('generated report diagram images in', EXPORT_DIR)