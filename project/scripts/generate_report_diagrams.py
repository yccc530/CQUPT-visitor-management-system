from pathlib import Path
from math import atan2, cos, sin, pi
from PIL import Image, ImageDraw, ImageFont
import shutil

ROOT = Path(__file__).resolve().parents[1]
EXPORT_DIR = ROOT / 'diagrams' / 'export'
FIGURE_DIR = ROOT / 'report-latex' / 'figures'

BG = '#f7fbff'
INK = '#172033'
MUTED = '#64748b'
LINE = '#334155'
BLUE = '#2563eb'
CYAN = '#0891b2'
GREEN = '#16a34a'
AMBER = '#d97706'
RED = '#dc2626'
PURPLE = '#7c3aed'
BORDER = '#b8c7dc'
WHITE = '#ffffff'
STORE = '#f8fafc'


def _font(size=26, bold=False):
    choices = [
        r'C:\Windows\Fonts\msyhbd.ttc' if bold else r'C:\Windows\Fonts\msyh.ttc',
        r'C:\Windows\Fonts\simhei.ttf',
        r'C:\Windows\Fonts\simsun.ttc',
        r'C:\Windows\Fonts\arial.ttf',
    ]
    for c in choices:
        try:
            return ImageFont.truetype(c, size=size)
        except Exception:
            pass
    return ImageFont.load_default()

FONT_XS = _font(18)
FONT_SM = _font(22)
FONT = _font(26)
FONT_BOLD = _font(28, True)
FONT_TITLE = _font(42, True)


def size(draw, text, font):
    box = draw.textbbox((0, 0), text, font=font)
    return box[2] - box[0], box[3] - box[1]


def wrap(draw, text, font, max_width):
    result = []
    for raw in str(text).split('\n'):
        line = ''
        for ch in raw:
            trial = line + ch
            if size(draw, trial, font)[0] <= max_width or not line:
                line = trial
            else:
                result.append(line)
                line = ch
        result.append(line)
    return result


class Canvas:
    def __init__(self, w, h, title):
        self.w = w
        self.h = h
        self.img = Image.new('RGB', (w, h), BG)
        self.draw = ImageDraw.Draw(self.img)
        self.draw.rounded_rectangle((40, 30, w - 40, 105), radius=20, fill='#eaf3ff', outline='#bfdbfe', width=2)
        tw, th = size(self.draw, title, FONT_TITLE)
        self.draw.text(((w - tw) / 2, 48), title, fill='#0f172a', font=FONT_TITLE)

    def centered_text(self, box, text, font=FONT, fill=INK, line_gap=8):
        x1, y1, x2, y2 = box
        lines = wrap(self.draw, text, font, x2 - x1 - 28)
        heights = [size(self.draw, line, font)[1] for line in lines]
        total = sum(heights) + max(0, len(lines) - 1) * line_gap
        y = y1 + (y2 - y1 - total) / 2
        for line, lh in zip(lines, heights):
            tw, _ = size(self.draw, line, font)
            self.draw.text((x1 + (x2 - x1 - tw) / 2, y), line, fill=fill, font=font)
            y += lh + line_gap

    def external(self, box, text):
        self.draw.rectangle(box, fill=WHITE, outline=CYAN, width=3)
        self.centered_text(box, text, FONT_BOLD)

    def process(self, box, text, pid=None):
        self.draw.rounded_rectangle(box, radius=24, fill='#eef6ff', outline=BLUE, width=3)
        if pid:
            self.draw.ellipse((box[0] + 14, box[1] + 12, box[0] + 52, box[1] + 50), fill=BLUE, outline=BLUE)
            self.centered_text((box[0] + 14, box[1] + 12, box[0] + 52, box[1] + 50), pid, FONT_XS, WHITE)
        self.centered_text(box, text, FONT_BOLD)

    def datastore(self, box, text):
        x1, y1, x2, y2 = box
        self.draw.rectangle(box, fill=STORE, outline=BORDER, width=3)
        self.draw.line((x1 + 18, y1, x1 + 18, y2), fill=BORDER, width=3)
        self.centered_text((x1 + 20, y1, x2, y2), text, FONT_SM)

    def entity(self, box, title, attrs, color=BLUE):
        x1, y1, x2, y2 = box
        self.draw.rounded_rectangle(box, radius=10, fill=WHITE, outline=color, width=3)
        self.draw.rectangle((x1, y1, x2, y1 + 48), fill=color, outline=color)
        self.centered_text((x1, y1, x2, y1 + 48), title, FONT_BOLD, WHITE)
        y = y1 + 62
        for a in attrs:
            font = FONT_XS if len(a) > 15 else FONT_SM
            fill = '#0f172a'
            if a.startswith('PK'):
                fill = BLUE
            self.draw.text((x1 + 18, y), a, fill=fill, font=font)
            if a.startswith('PK'):
                tw, th = size(self.draw, a, font)
                self.draw.line((x1 + 18, y + th + 2, x1 + 18 + tw, y + th + 2), fill=BLUE, width=2)
            y += 30

    def relation(self, center, w, h, text, color=AMBER):
        cx, cy = center
        points = [(cx, cy - h/2), (cx + w/2, cy), (cx, cy + h/2), (cx - w/2, cy)]
        self.draw.polygon(points, fill='#fff7ed', outline=color)
        self.draw.line(points + [points[0]], fill=color, width=3)
        self.centered_text((cx - w/2 + 10, cy - h/2 + 6, cx + w/2 - 10, cy + h/2 - 6), text, FONT_SM)

    def line(self, p1, p2, label=None, label_at=0.5, color=LINE, width=3, arrow=True):
        self.draw.line((p1, p2), fill=color, width=width)
        if arrow:
            self.arrow_head(p1, p2, color)
        if label:
            self.label(p1[0] + (p2[0] - p1[0]) * label_at, p1[1] + (p2[1] - p1[1]) * label_at, label)

    def poly(self, pts, label=None, color=LINE, width=3, arrow=True):
        self.draw.line(pts, fill=color, width=width, joint='curve')
        if arrow:
            self.arrow_head(pts[-2], pts[-1], color)
        if label:
            mid = pts[len(pts)//2]
            self.label(mid[0], mid[1], label)

    def arrow_head(self, p1, p2, color):
        angle = atan2(p2[1] - p1[1], p2[0] - p1[0])
        length = 15
        pts = [
            p2,
            (p2[0] - length * cos(angle - pi / 7), p2[1] - length * sin(angle - pi / 7)),
            (p2[0] - length * cos(angle + pi / 7), p2[1] - length * sin(angle + pi / 7)),
        ]
        self.draw.polygon(pts, fill=color)

    def label(self, x, y, text):
        font = FONT_XS
        tw, th = size(self.draw, text, font)
        pad = 6
        self.draw.rounded_rectangle((x - tw/2 - pad, y - th/2 - pad, x + tw/2 + pad, y + th/2 + pad), radius=8, fill=WHITE, outline='#cbd5e1')
        self.draw.text((x - tw/2, y - th/2 - 1), text, fill=MUTED, font=font)

    def note(self, box, text):
        self.draw.rounded_rectangle(box, radius=14, fill='#fff7ed', outline='#fed7aa', width=2)
        self.centered_text(box, text, FONT_SM, '#7c2d12')

    def save(self, name):
        EXPORT_DIR.mkdir(parents=True, exist_ok=True)
        FIGURE_DIR.mkdir(parents=True, exist_ok=True)
        out = EXPORT_DIR / f'{name}.png'
        fig = FIGURE_DIR / f'{name}.png'
        self.img.save(out)
        shutil.copy2(out, fig)
        return out


def data_flow_level0():
    c = Canvas(2200, 1350, '顶层数据流程图')
    c.process((860, 430, 1340, 620), '智慧访客预约与\n出入校管理系统', 'P0')
    left = [('访客', (120, 250, 360, 340), '预约申请、身份信息'), ('系统管理员', (120, 720, 360, 810), '基础数据维护')]
    right = [('被访人', (1780, 210, 2020, 300), '确认意见'), ('部门审批人员', (1780, 430, 2020, 520), '审批意见'), ('门岗安保人员', (1780, 650, 2020, 740), '核验与登记'), ('校级管理人员', (1780, 870, 2020, 960), '统计查询')]
    for name, box, label in left:
        c.external(box, name)
        c.poly([(box[2], (box[1]+box[3])//2), (640, (box[1]+box[3])//2), (640, 525), (860, 525)], label=label)
    for name, box, label in right:
        c.external(box, name)
        c.poly([(1340, 525), (1560, 525), (1560, (box[1]+box[3])//2), (box[0], (box[1]+box[3])//2)], label=label)
    stores = [
        ('D1 访客与预约数据', (300, 1080, 620, 1160)),
        ('D2 审批与通行数据', (720, 1080, 1040, 1160)),
        ('D3 出入校记录数据', (1160, 1080, 1480, 1160)),
        ('D4 用户权限基础数据', (1580, 1080, 1900, 1160)),
    ]
    for i, (name, box) in enumerate(stores):
        c.datastore(box, name)
        sx = 920 + i * 120
        c.poly([(1100, 620), (1100, 920), (sx, 920), ((box[0]+box[2])//2, box[1])], label='读写')
    c.note((620, 1230, 1580, 1290), '说明：外部实体通过预约、审批、核验和查询等数据流与系统交互；数据存储用于沉淀业务数据和权限基础数据。')
    return c.save('data_flow_level0')


def data_flow_level1():
    c = Canvas(2600, 1550, '二层数据流程图')
    processes = [
        ('P1', '受理预约申请', (130, 420, 410, 530)),
        ('P2', '黑名单检查', (520, 420, 800, 530)),
        ('P3', '被访人确认', (910, 420, 1190, 530)),
        ('P4', '部门审批', (1300, 420, 1580, 530)),
        ('P5', '生成通行凭证', (1690, 420, 1970, 530)),
        ('P6', '门岗核验与\n出入校登记', (2080, 420, 2380, 530)),
        ('P7', '查询统计', (1120, 1050, 1400, 1160)),
    ]
    for pid, name, box in processes:
        c.process(box, name, pid)
    for a, b, label in zip(processes, processes[1:6], ['预约数据', '检查结果', '确认结果', '审批结果', '通行凭证']):
        c.line((a[2][2], 475), (b[2][0], 475), label=label)
    externals = [
        ('访客', (150, 210, 360, 290), (270, 420), '提交预约/查询状态'),
        ('被访人', (945, 210, 1155, 290), (1050, 420), '确认或拒绝'),
        ('部门审批人员', (1325, 210, 1555, 290), (1440, 420), '审批意见'),
        ('门岗安保人员', (2115, 210, 2345, 290), (2230, 420), '核验登记'),
        ('校级管理人员', (1120, 1290, 1400, 1370), (1260, 1160), '统计查询'),
    ]
    for name, box, target, label in externals:
        c.external(box, name)
        c.line(((box[0]+box[2])//2, box[3]), target, label=label)
    stores = [
        ('D1 访客信息', (120, 760, 370, 840), (270, 530)),
        ('D2 预约申请', (460, 760, 710, 840), (650, 530)),
        ('D3 黑名单', (800, 760, 1050, 840), (660, 530)),
        ('D4 审批记录', (1140, 760, 1390, 840), (1440, 530)),
        ('D5 通行凭证', (1480, 760, 1730, 840), (1830, 530)),
        ('D6 出入校记录', (1820, 760, 2070, 840), (2230, 530)),
        ('D7 用户权限数据', (2160, 760, 2440, 840), (2230, 530)),
    ]
    for name, box, src in stores:
        c.datastore(box, name)
        c.poly([src, (src[0], 660), ((box[0]+box[2])//2, 660), ((box[0]+box[2])//2, box[1])], label='读写')
    for box in [(460,760,710,840), (1140,760,1390,840), (1480,760,1730,840), (1820,760,2070,840)]:
        c.poly([((box[0]+box[2])//2, box[3]), ((box[0]+box[2])//2, 980), (1260, 980), (1260, 1050)], label='统计数据')
    c.note((700, 1230, 1900, 1310), '说明：二层图将预约、黑名单检查、确认、审批、凭证、门岗登记和查询统计拆分为独立处理过程，数据流按业务顺序自左向右流动，避免交叉。')
    return c.save('data_flow_level1')


def er_core_business():
    c = Canvas(2800, 1800, '核心业务 E-R 图')
    entities = {
        'visitor': ((120, 580, 430, 760), '访客 visitor', ['PK id', '姓名', '手机号', '证件号', '访客类型'], BLUE),
        'blacklist': ((120, 250, 430, 430), '黑名单 blacklist', ['PK id', '证件号', '风险原因', '风险等级', '有效期'], RED),
        'vehicle': ((120, 1050, 430, 1230), '访客车辆 vehicle', ['PK id', '车牌号', '车辆类型', '所属访客'], CYAN),
        'apply': ((900, 600, 1240, 830), '预约申请 visit_apply', ['PK id', '预约编号', '访问事由', '计划时间', '预约状态', '访问状态'], BLUE),
        'companion': ((900, 1040, 1240, 1220), '随行人员 companion', ['PK id', '姓名', '证件号', '联系电话'], CYAN),
        'user': ((900, 230, 1240, 410), '校内用户 sys_user', ['PK id', '姓名', '账号', '所属部门'], GREEN),
        'dept': ((1460, 230, 1800, 410), '部门 department', ['PK id', '部门名称', '部门编码'], GREEN),
        'approval': ((1460, 1040, 1800, 1250), '审批记录 approval_record', ['PK id', '审批环节', '审批结果', '审批意见', '审批时间'], AMBER),
        'pass': ((2040, 520, 2360, 700), '通行凭证 pass_code', ['PK id', '通行码', '有效期', '凭证状态'], PURPLE),
        'access': ((2040, 920, 2360, 1140), '出入校记录 access_record', ['PK id', '入校时间', '离校时间', '访问状态', '经办安保'], PURPLE),
        'gate': ((2040, 1360, 2360, 1530), '校门 campus_gate', ['PK id', '校门名称', '校门位置'], CYAN),
    }
    for box, title, attrs, color in entities.values():
        c.entity(box, title, attrs, color)
    rels = [
        ('提交', (660, 680), (430, 670), (900, 715), '0..N', '1..1'),
        ('列入', (280, 500), (275, 430), (275, 580), '0..N', '1..1'),
        ('拥有', (520, 1010), (430, 1140), (430, 690), '0..N', '1..1'),
        ('使用车辆', (660, 1090), (430, 1140), (900, 745), '0..N', '0..1'),
        ('携带', (1070, 940), (1070, 830), (1070, 1040), '1..1', '0..N'),
        ('接待', (1070, 500), (1070, 410), (1070, 600), '0..N', '1..1'),
        ('受理', (1360, 520), (1640, 410), (1240, 700), '0..N', '1..1'),
        ('产生审批', (1360, 1050), (1240, 760), (1460, 1130), '1..1', '0..N'),
        ('处理', (1640, 820), (1070, 410), (1640, 1040), '0..N', '1..1'),
        ('生成', (1640, 650), (1240, 700), (2040, 610), '1..1', '0..1'),
        ('核验登记', (2200, 810), (2200, 700), (2200, 920), '1..1', '0..N'),
        ('形成记录', (1640, 980), (1240, 760), (2040, 1010), '1..1', '0..N'),
        ('入校校门', (2200, 1250), (2200, 1140), (2200, 1360), '0..N', '0..1'),
        ('离校校门', (2500, 1250), (2360, 1040), (2360, 1440), '0..N', '0..1'),
    ]
    for name, center, p1, p2, card1, card2 in rels:
        c.relation(center, 150, 88, name)
        c.line(p1, (center[0]-75, center[1]) if p1[0] < center[0] else (center[0], center[1]-44), label=card1, arrow=False)
        end = (center[0]+75, center[1]) if p2[0] > center[0] else (center[0], center[1]+44)
        if p2[1] < center[1]:
            end = (center[0], center[1]-44)
        c.line(end, p2, label=card2, arrow=False)
    c.note((640, 1540, 2160, 1630), '说明：实体用名词表示，联系用动词表示；预约申请是核心业务实体。图中 1..1 表示必须参与，0..N 表示可选多次参与，0..1 表示可选一次参与。')
    return c.save('er_core_business')


def er_overview():
    c = Canvas(2400, 1400, '总体简化 E-R 图')
    entities = [
        ((150, 260, 430, 420), '访客', ['PK id', '身份信息'], BLUE),
        ((760, 260, 1080, 440), '预约申请', ['PK id', '预约编号', '状态'], BLUE),
        ((1380, 220, 1700, 400), '审批记录', ['PK id', '环节', '结果'], AMBER),
        ((1380, 560, 1700, 720), '通行凭证', ['PK id', '通行码'], PURPLE),
        ((1380, 900, 1700, 1080), '出入校记录', ['PK id', '入校/离校'], PURPLE),
        ((1960, 900, 2220, 1060), '校门', ['PK id', '名称'], CYAN),
        ((760, 820, 1080, 1000), '校内用户', ['PK id', '姓名', '角色'], GREEN),
        ((150, 820, 430, 980), '部门', ['PK id', '名称'], GREEN),
        ((150, 560, 430, 720), '黑名单', ['PK id', '风险原因'], RED),
    ]
    for box, title, attrs, color in entities:
        c.entity(box, title, attrs, color)
    rels = [
        ('提交', (600,350), (430,340), (760,350), '0..N', '1..1'),
        ('风险约束', (600,640), (430,640), (760,360), '0..N', '0..N'),
        ('接待', (920,620), (920,820), (920,440), '0..N', '1..1'),
        ('管理范围', (600,910), (430,900), (760,900), '1..N', '1..1'),
        ('产生', (1230,350), (1080,350), (1380,310), '1..1', '0..N'),
        ('生成', (1230,620), (1080,390), (1380,640), '1..1', '0..1'),
        ('登记', (1230,980), (1080,390), (1380,990), '1..1', '0..N'),
        ('发生于', (1840,990), (1700,990), (1960,980), '0..N', '0..1'),
    ]
    for name, center, p1, p2, a, b in rels:
        c.relation(center, 145, 84, name)
        c.line(p1, center, label=a, arrow=False)
        c.line(center, p2, label=b, arrow=False)
    c.note((520, 1180, 1880, 1260), '总体图仅保留主干实体和核心联系，用于说明访客预约、审批、通行、出入校和组织权限之间的整体约束。')
    return c.save('er_overview')


def er_user_permission():
    c = Canvas(2300, 1250, '用户权限 E-R 图')
    entities = {
        'dept': ((120, 500, 430, 690), '部门 department', ['PK id', '部门名称', '部门编码'], GREEN),
        'user': ((720, 500, 1050, 720), '系统用户 sys_user', ['PK id', '账号', '姓名', '用户状态'], BLUE),
        'role': ((1320, 230, 1650, 450), '角色 sys_role', ['PK id', '角色编码', '角色名称'], PURPLE),
        'perm': ((1320, 780, 1650, 1010), '权限 sys_permission', ['PK id', '权限编码', '权限名称', '权限类型'], AMBER),
    }
    for box, title, attrs, color in entities.values():
        c.entity(box, title, attrs, color)
    c.relation((570, 610), 150, 88, '包含')
    c.line((430,610),(495,610),label='1..1',arrow=False)
    c.line((645,610),(720,610),label='0..N',arrow=False)
    c.relation((1180, 445), 150, 88, '分配')
    c.line((1050,610),(1180,489),label='0..N',arrow=False)
    c.line((1180,401),(1320,340),label='0..N',arrow=False)
    c.relation((1180, 820), 150, 88, '授权')
    c.line((1485,450),(1180,776),label='0..N',arrow=False)
    c.line((1180,864),(1320,895),label='0..N',arrow=False)
    c.relation((1860, 610), 150, 88, '父子权限')
    c.line((1650,895),(1785,610),label='0..N 子权限',arrow=False)
    c.line((1935,610),(1650,895),label='0..1 父权限',arrow=False)
    c.note((760, 1060, 1660, 1135), '说明：用户与角色、角色与权限均为 M:N 联系，逻辑结构中分别转换为 sys_user_role 和 sys_role_permission。')
    return c.save('er_user_permission')


def er_system_support():
    c = Canvas(2200, 1250, '系统支撑 E-R 图')
    entities = [
        ((900, 180, 1240, 380), '系统用户 sys_user', ['PK id', '账号', '姓名'], BLUE),
        ((180, 520, 500, 710), '通知 notice', ['PK id', '标题', '阅读状态'], GREEN),
        ((700, 520, 1040, 720), '操作日志 operation_log', ['PK id', '模块', '操作结果', '操作时间'], AMBER),
        ((1220, 520, 1580, 720), '截图记录 screenshot_record', ['PK id', '截图编码', '生成状态'], CYAN),
        ((1680, 520, 2020, 720), '报告记录 report_record', ['PK id', '报告编码', '生成状态'], CYAN),
        ((500, 900, 820, 1080), '字典类型 dict_type', ['PK id', '类型编码', '类型名称'], PURPLE),
        ((1060, 900, 1380, 1080), '字典项 dict_item', ['PK id', '字典编码', '字典值'], PURPLE),
    ]
    for box, title, attrs, color in entities:
        c.entity(box, title, attrs, color)
    for name, center, p1, p2, a, b in [
        ('接收', (610,455), (1070,380), (500,610), '0..N', '0..1'),
        ('产生', (900,455), (1070,380), (870,520), '0..N', '0..1'),
        ('创建截图', (1390,455), (1070,380), (1400,520), '0..N', '0..1'),
        ('生成报告', (1710,455), (1070,380), (1850,520), '0..N', '0..1'),
        ('包含', (940,990), (820,990), (1060,990), '1..1', '0..N'),
    ]:
        c.relation(center, 150, 88, name)
        c.line(p1, center, label=a, arrow=False)
        c.line(center, p2, label=b, arrow=False)
    c.note((560, 1120, 1640, 1190), '说明：支撑实体不参与预约主流程，但用于消息提醒、操作审计、字典维护、截图留痕和报告生成记录。')
    return c.save('er_system_support')


def system_module():
    c = Canvas(2400, 1450, '系统功能模块图')
    c.process((760, 150, 1640, 240), '重庆邮电大学智慧访客预约与出入校管理系统')
    modules = [
        ('访客端', ['访客预约', '状态查询', '通行凭证', '历史记录'], BLUE),
        ('被访人端', ['待确认预约', '确认预约', '拒绝预约', '接待记录'], CYAN),
        ('审批管理', ['部门待审批', '审批通过', '审批拒绝', '审批轨迹'], PURPLE),
        ('门岗管理', ['通行码核验', '入校登记', '离校登记', '当前在校', '超时未离校'], GREEN),
        ('访客记录', ['预约记录查询', '出入校记录', '车辆随行人员', '审批记录'], MUTED),
        ('安全管理', ['黑名单管理', '风险检查', '异常处理', '日志审计'], RED),
        ('统计报表', ['访客概览', '趋势分析', '部门排行', '校门统计', '审批分析'], AMBER),
        ('系统管理', ['用户管理', '角色权限', '部门管理', '校门管理', '字典管理'], '#0f766e'),
    ]
    x0, y0, col_w, gap = 90, 360, 260, 24
    for i, (name, subs, color) in enumerate(modules):
        x = x0 + i * (col_w + gap)
        c.process((x, y0, x + col_w, y0 + 78), name)
        c.line((1200, 240), (x + col_w/2, y0), color=color, arrow=False)
        y = y0 + 120
        for sub in subs:
            c.process((x, y, x + col_w, y + 68), sub)
            c.line((x + col_w/2, y0 + 78), (x + col_w/2, y), color='#94a3b8', arrow=False, width=2)
            y += 82
    return c.save('system_module')


def visitor_workflow():
    c = Canvas(2300, 1120, '访客预约审批流程图')
    steps = [
        ('访客提交预约', (120, 460, 360, 550), BLUE),
        ('黑名单检查', (480, 460, 720, 550), RED),
        ('被访人确认', (840, 460, 1080, 550), CYAN),
        ('部门审批', (1200, 460, 1440, 550), PURPLE),
        ('生成通行码', (1560, 460, 1800, 550), GREEN),
        ('门岗核验', (1920, 460, 2160, 550), AMBER),
    ]
    for text, box, color in steps:
        c.process(box, text)
    for a, b in zip(steps, steps[1:]):
        c.line((a[1][2], 505), (b[1][0], 505), label='通过')
    exceptions = [('黑名单拦截', (480,250,720,330), RED), ('被访人拒绝', (840,250,1080,330), RED), ('审批拒绝/退回', (1200,250,1440,330), RED), ('通行码过期', (1920,250,2160,330), RED)]
    for text, box, color in exceptions:
        c.process(box, text)
        c.line(((box[0]+box[2])//2, 460), ((box[0]+box[2])//2, box[3]), color=color, label='异常')
    bottom = [('入校登记', (1560,760,1800,850), GREEN), ('离校登记', (1200,760,1440,850), GREEN), ('访问记录归档', (840,760,1080,850), BLUE), ('查询统计', (480,760,720,850), BLUE)]
    for text, box, color in bottom:
        c.process(box, text)
    c.line((2040,550),(1680,760),label='允许入校')
    c.line((1560,805),(1440,805))
    c.line((1200,805),(1080,805))
    c.line((840,805),(720,805))
    c.process((1560,940,1860,1020), '超时未离校预警')
    c.line((1680,850),(1710,940),color=RED,label='超时')
    return c.save('visitor_workflow')


def gate_check_workflow():
    c = Canvas(2100, 1050, '门岗核验入校流程图')
    steps = [('输入凭证信息',(120,430,390,520)),('查询预约与凭证',(520,430,790,520)),('检查审批状态',(920,430,1190,520)),('检查有效时间',(1320,430,1590,520)),('黑名单复核',(1720,430,1970,520))]
    for text, box in steps:
        c.process(box, text)
    for a,b in zip(steps,steps[1:]):
        c.line((a[1][2],475),(b[1][0],475))
    c.process((1320,720,1590,810),'入校登记')
    c.process((1720,720,1970,810),'离校登记')
    c.process((920,720,1190,810),'拒绝入校\n记录原因')
    c.line((1845,520),(1455,720),label='通过')
    c.line((1590,765),(1720,765),label='离校')
    for x in [1055,1455,1845]:
        c.line((x,520),(1055,720),color=RED,label='不通过')
    c.note((560,180,1540,270),'门岗核验必须同时满足审批通过、凭证有效、访问时间有效、未命中黑名单等条件。')
    return c.save('gate_check_workflow')


def system_architecture():
    c = Canvas(2100, 1100, '系统架构图')
    c.process((140,240,520,360),'浏览器前端\nVue 3 / Element Plus / ECharts')
    c.process((860,240,1240,360),'Spring Boot 后端\n认证授权 / 业务服务 / REST API')
    c.process((1580,240,1940,360),'MySQL 数据库\n业务表 / 权限表 / 日志表')
    c.process((860,560,1240,680),'运行界面采集\n自动访问核心页面')
    c.process((1580,560,1940,680),'图表与截图资源\n设计图 / 运行截图')
    c.process((860,850,1240,970),'LaTeX 课程报告\n章节 / SQL / 图表 / 截图')
    c.line((520,300),(860,300),label='HTTP API')
    c.line((1240,300),(1580,300),label='SQL')
    c.line((1050,560),(1050,360),label='访问页面')
    c.line((1240,620),(1580,620),label='生成素材')
    c.line((1760,680),(1240,910),label='插入报告')
    return c.save('system_architecture')


def table_relation():
    c = Canvas(2400, 1450, '数据库表关系图')
    tables = [
        ('department', (130,220,420,300)), ('sys_user', (130,360,420,440)), ('sys_role', (130,500,420,580)), ('sys_permission', (130,640,420,720)),
        ('sys_user_role', (520,430,830,510)), ('sys_role_permission', (520,610,860,690)),
        ('visitor', (1040,220,1320,300)), ('visitor_vehicle', (1040,390,1320,470)), ('visit_apply', (1040,570,1320,650)), ('visitor_companion', (1040,750,1320,830)),
        ('approval_record', (1440,570,1760,650)), ('pass_code', (1440,750,1760,830)), ('access_record', (1440,930,1760,1010)), ('campus_gate', (1900,930,2180,1010)), ('blacklist', (1040,930,1320,1010)),
        ('notice', (520,1020,830,1100)), ('operation_log', (520,1160,830,1240)), ('dict_type', (1040,1160,1320,1240)), ('dict_item', (1440,1160,1760,1240)),
    ]
    pos = {}
    for name, box in tables:
        pos[name]=box
        c.process(box, name)
    def mid(name):
        b=pos[name]; return ((b[0]+b[2])//2,(b[1]+b[3])//2)
    links = [('department','sys_user'),('sys_user','sys_user_role'),('sys_role','sys_user_role'),('sys_role','sys_role_permission'),('sys_permission','sys_role_permission'),('visitor','visitor_vehicle'),('visitor','visit_apply'),('visitor_vehicle','visit_apply'),('visit_apply','visitor_companion'),('visit_apply','approval_record'),('visit_apply','pass_code'),('visit_apply','access_record'),('pass_code','access_record'),('visitor','blacklist'),('campus_gate','access_record'),('sys_user','notice'),('sys_user','operation_log'),('dict_type','dict_item')]
    for a,b in links:
        c.line(mid(a), mid(b), arrow=False, width=2, color='#64748b')
    c.note((620,1340,1780,1400),'说明：表关系图属于逻辑结构补充，用于展示主外键依赖；概念 E-R 图以实体和联系为主。')
    return c.save('table_relation')


def automation_report_workflow():
    c = Canvas(1900, 900, '自动截图与报告生成流程图')
    steps = [('初始化演示数据',(100,390,360,480)),('启动后端服务',(470,390,730,480)),('启动前端服务',(840,390,1100,480)),('采集运行界面',(1210,390,1470,480)),('生成课程报告',(1580,390,1810,480))]
    for text, box in steps:
        c.process(box, text)
    for a,b in zip(steps,steps[1:]):
        c.line((a[1][2],435),(b[1][0],435))
    c.note((420,160,1480,250),'自动化流程用于保证演示数据、运行截图、设计图和最终报告之间的一致性。')
    return c.save('automation_report_workflow')


def generate_all(root=None):
    EXPORT_DIR.mkdir(parents=True, exist_ok=True)
    FIGURE_DIR.mkdir(parents=True, exist_ok=True)
    data_flow_level0()
    data_flow_level1()
    er_overview()
    er_core_business()
    er_user_permission()
    er_system_support()
    system_module()
    visitor_workflow()
    gate_check_workflow()
    system_architecture()
    table_relation()
    automation_report_workflow()


if __name__ == '__main__':
    generate_all()
    print('generated improved report diagram images in', EXPORT_DIR)