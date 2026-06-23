from pathlib import Path

root = Path('project')
db = root / 'database'
docs = root / 'docs'

PASSWORD = '$2b$10$5pEOo2KWkcTaG5.cUGW3gemS2DeLtd5R.jddXUQm/nfnRFqz1RK4m'

def sql_str(value):
    if value is None:
        return 'NULL'
    return "'" + str(value).replace('\\', '\\\\').replace("'", "''") + "'"

def dt_expr(day_offset=0, hour=9, minute=0):
    return f"DATE_ADD(DATE_ADD(CURRENT_DATE(), INTERVAL {day_offset} DAY), INTERVAL '{hour:02d}:{minute:02d}:00' HOUR_SECOND)"

def now_minus(days=0, hours=0):
    total = days * 24 + hours
    return f"DATE_SUB(NOW(), INTERVAL {total} HOUR)"

def now_plus(days=0, hours=0):
    total = days * 24 + hours
    return f"DATE_ADD(NOW(), INTERVAL {total} HOUR)"

def row(values):
    return '(' + ', '.join(values) + ')'

def insert(table, columns, rows, chunk=80):
    if not rows:
        return []
    statements = []
    for i in range(0, len(rows), chunk):
        part = rows[i:i+chunk]
        statements.append(f"INSERT INTO {table} ({', '.join(columns)}) VALUES\n" + ',\n'.join(part) + ';')
    return statements

lines = []
lines.append('-- 重庆邮电大学智慧访客预约与出入校管理系统演示数据')
lines.append('-- 本脚本使用 CURRENT_DATE()/NOW() 生成动态日期，适合最终演示、截图和报告。')
lines.append('USE cqupt_visitor_system;')
lines.append('SET NAMES utf8mb4;')
lines.append('SET FOREIGN_KEY_CHECKS = 0;')
for table in [
    'report_record','screenshot_record','operation_log','notice','blacklist','access_record','pass_code',
    'approval_record','visitor_companion','visit_apply','visitor_vehicle','visitor','sys_role_permission',
    'sys_user_role','sys_user','dict_item','dict_type','campus_gate','sys_permission','sys_role','department'
]:
    lines.append(f'TRUNCATE TABLE {table};')
lines.append('SET FOREIGN_KEY_CHECKS = 1;')
lines.append('')

# Departments
departments = [
    (1, None, 'CQUPT', '重庆邮电大学', None, '023-62460000', 1, 'ENABLED'),
    (2, 1, 'TXGC', '信息与通信工程学院', None, '023-62460101', 2, 'ENABLED'),
    (3, 1, 'JSJ', '计算机科学与技术学院', None, '023-62460102', 3, 'ENABLED'),
    (4, 1, 'ZDH', '自动化学院', None, '023-62460103', 4, 'ENABLED'),
    (5, 1, 'GDGC', '光电工程学院', None, '023-62460104', 5, 'ENABLED'),
    (6, 1, 'WLAQ', '网络空间安全与信息法学院', None, '023-62460105', 6, 'ENABLED'),
    (7, 1, 'JJGL', '经济管理学院', None, '023-62460106', 7, 'ENABLED'),
    (8, 1, 'CMYS', '传媒艺术学院', None, '023-62460107', 8, 'ENABLED'),
    (9, 1, 'RGZN', '人工智能学院', None, '023-62460108', 9, 'ENABLED'),
    (10, 1, 'YJSY', '研究生院', None, '023-62460109', 10, 'ENABLED'),
    (11, 1, 'JWC', '教务处', None, '023-62460110', 11, 'ENABLED'),
    (12, 1, 'BWC', '保卫处', None, '023-62460119', 12, 'ENABLED'),
    (13, 1, 'HQGL', '后勤管理处', None, '023-62460120', 13, 'ENABLED'),
]
lines += insert('department', ['id','parent_id','dept_code','dept_name','leader_user_id','phone','sort_order','status'], [
    row([str(i), 'NULL' if parent is None else str(parent), sql_str(code), sql_str(name), 'NULL', sql_str(phone), str(sort), sql_str(status)])
    for i, parent, code, name, leader, phone, sort, status in departments
])
lines.append('')

roles = [
    (1,'VISITOR','访客','校外访客自助预约与查看预约进度','ENABLED'),
    (2,'HOST','被访人','校内教师或职工确认本人相关预约','ENABLED'),
    (3,'DEPT_APPROVER','部门审批人员','负责本部门预约审批','ENABLED'),
    (4,'GATE_GUARD','门岗安保人员','负责门岗核验和出入校登记','ENABLED'),
    (5,'ADMIN','系统管理员','维护系统基础数据、用户和权限','ENABLED'),
    (6,'SCHOOL_MANAGER','校级管理人员','查看全校访客统计和运行报表','ENABLED'),
]
lines += insert('sys_role', ['id','role_code','role_name','role_desc','status'], [
    row([str(i), sql_str(code), sql_str(name), sql_str(desc), sql_str(status)]) for i, code, name, desc, status in roles
])
lines.append('')

permissions = [
    (1,'dashboard:view','首页驾驶舱','MENU',None,'/dashboard','views/Dashboard.vue',None,1),
    (2,'visit:apply','访客预约申请','MENU',None,'/visit/apply','views/visit/Apply.vue',None,2),
    (3,'visit:mine','我的预约','MENU',None,'/visit/mine','views/visit/MyApply.vue',None,3),
    (4,'host:confirm','待确认预约','MENU',None,'/approval/host','views/approval/HostConfirm.vue',None,4),
    (5,'dept:approve','部门审批','MENU',None,'/approval/department','views/approval/DepartmentApproval.vue',None,5),
    (6,'gate:verify','门岗核验','MENU',None,'/gate/verify','views/gate/Verify.vue',None,6),
    (7,'access:entry','入校登记','MENU',None,'/access/entry','views/access/Entry.vue',None,7),
    (8,'access:exit','离校登记','MENU',None,'/access/leave','views/access/Leave.vue',None,8),
    (9,'access:current','当前在校访客','MENU',None,'/access/current','views/access/CurrentVisitors.vue',None,9),
    (10,'access:overtime','超时未离校访客','MENU',None,'/access/overtime','views/access/OvertimeVisitors.vue',None,10),
    (11,'blacklist:manage','黑名单管理','MENU',None,'/blacklist','views/blacklist/Blacklist.vue',None,11),
    (12,'visit:record','访客记录查询','MENU',None,'/records','views/records/VisitRecords.vue',None,12),
    (13,'statistics:view','统计报表','MENU',None,'/reports','views/reports/Statistics.vue',None,13),
    (14,'user:manage','用户管理','MENU',None,'/system/users','views/system/UserManage.vue',None,14),
    (15,'role:permission','角色权限管理','MENU',None,'/system/roles','views/system/RolePermission.vue',None,15),
    (16,'dept:manage','部门管理','MENU',None,'/system/departments','views/system/DepartmentManage.vue',None,16),
    (17,'gate:manage','校门管理','MENU',None,'/system/gates','views/system/GateManage.vue',None,17),
    (18,'log:view','系统日志','MENU',None,'/system/logs','views/system/SystemLog.vue',None,18),
    (19,'auth:login','登录接口','API',None,None,None,'/api/auth/login',19),
    (20,'workflow:operate','访客流程接口','API',None,None,None,'/api/workflow/**',20),
    (21,'statistics:api','统计报表接口','API',None,None,None,'/api/statistics/**',21),
]
lines += insert('sys_permission', ['id','permission_code','permission_name','permission_type','parent_id','route_path','component_path','api_path','sort_order','status'], [
    row([str(i), sql_str(code), sql_str(name), sql_str(ptype), 'NULL' if parent is None else str(parent), sql_str(route), sql_str(component), sql_str(api), str(sort), sql_str('ENABLED')])
    for i, code, name, ptype, parent, route, component, api, sort in permissions
])
lines.append('')

# Dicts
dict_types = [
    (1,'apply_status','预约状态','访客预约审批状态'),
    (2,'access_status','访问状态','访客出入校状态'),
    (3,'pass_status','通行凭证状态','通行码生命周期状态'),
    (4,'approval_result','审批结果','审批动作结果'),
    (5,'visitor_type','访客类型','演示数据访客来源类型'),
    (6,'blacklist_level','黑名单等级','风险等级'),
]
lines += insert('dict_type', ['id','type_code','type_name','remark','status'], [row([str(i),sql_str(c),sql_str(n),sql_str(d),sql_str('ENABLED')]) for i,c,n,d in dict_types])
items = []
idc=1
for type_id, values in [
    (1,[('PENDING_HOST','待被访人确认'),('HOST_CONFIRMED','被访人已确认'),('PENDING_DEPT','待部门审批'),('APPROVED','审批通过'),('REJECTED','审批拒绝'),('HOST_REJECTED','被访人已拒绝'),('CANCELED','已取消'),('REJECTED_BLACKLIST','黑名单拦截')]),
    (2,[('NOT_ENTERED','未入校'),('ENTERED','已入校'),('EXITED','已离校'),('OVERTIME','超时未离校'),('ABNORMAL','异常处理')]),
    (3,[('VALID','有效'),('USED','已使用'),('EXPIRED','已过期'),('DISABLED','已作废')]),
    (4,[('APPROVED','同意'),('REJECTED','拒绝'),('PENDING','待处理'),('RETURNED','退回修改')]),
    (5,[('PARENT','学生家长'),('ENTERPRISE','企业合作人员'),('MAINTENANCE','后勤维修人员'),('ACADEMIC','学术交流人员'),('INTERVIEW','面试人员'),('DELIVERY','快递或配送人员'),('ALUMNI','校友'),('EXPERT','外聘专家')]),
    (6,[('WARNING','提醒关注'),('LIMITED','限制通行'),('FORBIDDEN','禁止通行')]),
]:
    for sort, (value,label) in enumerate(values,1):
        items.append((idc,type_id,value,label,sort)); idc += 1
lines += insert('dict_item', ['id','type_id','item_code','item_name','item_value','sort_order','status','remark'], [row([str(i),str(t),sql_str(v),sql_str(l),sql_str(v),str(s),sql_str('ENABLED'),sql_str('演示字典项')]) for i,t,v,l,s in items])
lines.append('')

# Gates
gates = [
    (1,'TFM','腾飞门','南岸校区腾飞广场入口','PEDESTRIAN'),
    (2,'BM','北门','南岸校区北侧主入口','VEHICLE'),
    (3,'CWM','崇文门','崇文路校园入口','PEDESTRIAN'),
    (4,'XM','西门','西区道路入口','VEHICLE'),
    (5,'XNM','西南门','西南侧教学区入口','VEHICLE'),
    (6,'TSK','图书馆访客入口','图书馆一楼访客通道','PEDESTRIAN'),
    (7,'CYY','创新创业园门岗','创新创业园区入口','VEHICLE'),
]
lines += insert('campus_gate', ['id','gate_code','gate_name','gate_location','gate_type','status','remark'], [
    row([str(i),sql_str(c),sql_str(n),sql_str(loc),sql_str(t),sql_str('ENABLED'),sql_str('演示数据校门')]) for i,c,n,loc,t in gates
])
lines.append('')

# Users
teacher_names = ['张启航','李静怡','王明远','赵雨晴','陈思远','刘若琳','杨嘉宁','黄子涵','周安琪','吴泽宇','徐欣然','孙博文','何佳怡','高宇航','郭晨曦']
approver_names = ['冯立国','唐雅雯','蒋明辉','袁晓峰','程丽娜','邓志强']
guard_names = ['罗建军','谢鹏飞','马国强','韩志勇','彭涛']
manager_names = ['许文博','秦晓岚']
users = [
    (1,'admin','系统管理员','13800000001','admin@cqupt.edu.cn',12,'ADMIN'),
    (2,'admin02','平台管理员','13800000002','admin02@cqupt.edu.cn',12,'ADMIN'),
    (3,'visitor01','访客演示账号','13800000003','visitor01@example.com',None,'VISITOR'),
]
uid = 4
teacher_ids = []
for idx,name in enumerate(teacher_names,1):
    dept_id = 2 + ((idx-1) % 10)
    teacher_ids.append(uid)
    users.append((uid,f'teacher{idx:02d}',name,f'1381000{idx:04d}',f'teacher{idx:02d}@cqupt.edu.cn',dept_id,'HOST'))
    uid += 1
approver_ids=[]
for idx,name in enumerate(approver_names,1):
    dept_id = 2 + ((idx-1) % 6)
    approver_ids.append(uid)
    users.append((uid,f'approver{idx:02d}',name,f'1382000{idx:04d}',f'approver{idx:02d}@cqupt.edu.cn',dept_id,'DEPT_APPROVER'))
    uid += 1
guard_ids=[]
for idx,name in enumerate(guard_names,1):
    guard_ids.append(uid)
    users.append((uid,f'guard{idx:02d}',name,f'1383000{idx:04d}',f'guard{idx:02d}@cqupt.edu.cn',12,'GATE_GUARD'))
    uid += 1
manager_ids=[]
for idx,name in enumerate(manager_names,1):
    manager_ids.append(uid)
    users.append((uid,f'manager{idx:02d}',name,f'1384000{idx:04d}',f'manager{idx:02d}@cqupt.edu.cn',1,'SCHOOL_MANAGER'))
    uid += 1
lines += insert('sys_user', ['id','username','password_hash','real_name','phone','email','department_id','user_type','status','last_login_time'], [
    row([str(i),sql_str(username),sql_str(PASSWORD),sql_str(real),sql_str(phone),sql_str(email),'NULL' if dept is None else str(dept),sql_str(user_type),sql_str('ENABLED'),now_minus(days=(i%10),hours=i%7)])
    for i,username,real,phone,email,dept,user_type in users
])
lines.append('')

# Department leaders
leader_map = {2:4,3:5,4:6,5:7,6:8,7:9,8:10,9:11,10:12,11:13,12:25,13:14}
for dept_id, leader_id in leader_map.items():
    lines.append(f'UPDATE department SET leader_user_id = {leader_id} WHERE id = {dept_id};')
lines.append('')

role_by_user_type = {'VISITOR':1,'HOST':2,'DEPT_APPROVER':3,'GATE_GUARD':4,'ADMIN':5,'SCHOOL_MANAGER':6}
user_roles=[]
for i,username,real,phone,email,dept,user_type in users:
    user_roles.append(row([str(i),str(role_by_user_type[user_type])]))
lines += insert('sys_user_role', ['user_id','role_id'], user_roles)
role_permissions = []
role_perm_map = {
    1:[1,2,3,19,20],
    2:[1,3,4,12,19,20],
    3:[1,5,12,19,20],
    4:[1,6,7,8,9,10,19,20],
    5:list(range(1,22)),
    6:[1,9,10,12,13,18,19,21],
}
for rid, perms in role_perm_map.items():
    for pid in perms:
        role_permissions.append(row([str(rid),str(pid)]))
lines += insert('sys_role_permission', ['role_id','permission_id'], role_permissions)
lines.append('')

# Visitors
family_names = ['陈','李','王','张','刘','杨','赵','黄','周','吴','徐','孙','胡','朱','高','林','何','郭','马','罗','梁','宋','郑','谢','韩','唐','冯','于','董','萧','程','曹','袁','邓','许','傅','沈','曾','彭','吕']
given_names = ['明轩','子涵','思源','雨桐','浩然','雅雯','宇航','佳怡','俊杰','欣然','泽宇','嘉宁','梓萱','博文','若琳','承志','诗涵','一鸣','晨曦','安琪','锦程','文博','沐阳','昊天','静怡','启航','睿哲','可欣','立诚','清扬']
visitors=[]
for i in range(1,141):
    name = family_names[(i-1)%len(family_names)] + given_names[(i*3)%len(given_names)]
    phone = f'139{(50000000+i):08d}'
    year = 1975 + (i % 28)
    month = (i % 12) + 1
    day = (i % 28) + 1
    idno = f'500106{year:04d}{month:02d}{day:02d}{i%10000:04d}'
    company = ['重庆移动南岸分公司','重庆邮电大学学生家长','重庆数智科技有限公司','重庆市南岸区维修服务中心','西部科学城智能网联企业','重庆日报传媒集团','重庆邮电大学校友会','重庆市软件园'][i%8]
    gender = 'MALE' if i % 2 else 'FEMALE'
    level = ['NORMAL','IMPORTANT','TEMPORARY'][i%3]
    status = 'NORMAL'
    visitors.append((i,name,'ID_CARD',idno,phone,company,gender,level,status))
lines += insert('visitor', ['id','visitor_name','id_type','id_number','phone','company','gender','visitor_level','status'], [
    row([str(i),sql_str(n),sql_str(idt),sql_str(idno),sql_str(phone),sql_str(company),sql_str(gender),sql_str(level),sql_str(status)])
    for i,n,idt,idno,phone,company,gender,level,status in visitors
], chunk=100)
lines.append('')

# Vehicles
letters='ABCDEFGHJKLMNPQRSTUVWXYZ'
vehicles=[]
for i in range(1,71):
    visitor_id = i
    plate = '渝' + letters[i % len(letters)] + f'{(i*73)%100000:05d}'
    vtype = ['轿车','SUV','商务车','货车'][i%4]
    color = ['白色','黑色','银色','蓝色','灰色'][i%5]
    brand = ['长安','比亚迪','大众','丰田','特斯拉','吉利','五菱'][i%7]
    vehicles.append((i,visitor_id,plate,vtype,color,brand,'NORMAL'))
lines += insert('visitor_vehicle', ['id','visitor_id','plate_no','vehicle_type','color','brand','status'], [
    row([str(i),str(vid),sql_str(plate),sql_str(vtype),sql_str(color),sql_str(brand),sql_str(status)]) for i,vid,plate,vtype,color,brand,status in vehicles
])
lines.append('')

# Visit applications
reasons = [
    '学生家长来校办理学籍与生活沟通',
    '企业合作项目洽谈与实验室参观',
    '后勤维修人员进行设备检修',
    '学术交流报告与课题研讨',
    '参加学院实习与就业面试',
    '快递或配送人员进行物资交付',
    '校友返校参加学院交流活动',
    '外聘专家参加项目评审与讲座'
]
applications=[]
for i in range(1,221):
    if i <= 42:
        day_offset = -30 + i
    else:
        day_offset = -29 + ((i-43) % 37)
    if i <= 18:
        day_offset = 0
    visitor_id = ((i-1) % 140) + 1
    host_id = teacher_ids[(i-1) % len(teacher_ids)]
    dept_id = 2 + ((i-1) % 12)
    if dept_id > 13:
        dept_id = 2
    if dept_id == 12:
        dept_id = 13
    vehicle_id = visitor_id if visitor_id <= 70 and i % 3 != 1 else None
    status = 'APPROVED'
    if i > 18:
        if i % 17 == 0:
            status = 'PENDING_HOST'
        elif i % 19 == 0:
            status = 'HOST_CONFIRMED'
        elif i % 23 == 0:
            status = 'PENDING_DEPT'
        elif i % 29 == 0:
            status = 'REJECTED'
        elif i % 31 == 0:
            status = 'HOST_REJECTED'
        elif i % 37 == 0:
            status = 'CANCELED'
        elif i % 41 == 0:
            status = 'REJECTED_BLACKLIST'
    if i <= 18:
        status = ['APPROVED','APPROVED','APPROVED','APPROVED','PENDING_HOST','HOST_CONFIRMED','PENDING_DEPT','PENDING_DEPT','REJECTED','HOST_REJECTED','APPROVED','APPROVED','APPROVED','APPROVED','APPROVED','CANCELED','REJECTED_BLACKLIST','APPROVED'][i-1]
    access_status = 'NOT_ENTERED'
    if status == 'APPROVED':
        if day_offset < 0:
            access_status = ['EXITED','EXITED','OVERTIME','ENTERED','ABNORMAL'][i % 5]
        elif day_offset == 0:
            access_status = ['ENTERED','EXITED','OVERTIME','NOT_ENTERED'][i % 4]
        else:
            access_status = 'NOT_ENTERED'
    applications.append((i, visitor_id, host_id, dept_id, vehicle_id, reasons[i%len(reasons)], day_offset, 8 + (i % 8), status, access_status, i % 3, 1 + (i % 6)))

apply_rows=[]
for i, visitor_id, host_id, dept_id, vehicle_id, reason, day_offset, hour, status, access_status, comp_count, dur in applications:
    apply_no = f"CONCAT('VA', DATE_FORMAT({dt_expr(day_offset,0,0)}, '%Y%m%d'), '{i:04d}')"
    cancel_time = now_minus(days=max(0, -day_offset), hours=3) if status == 'CANCELED' else 'NULL'
    cancel_reason = sql_str('访客行程变化，主动取消') if status == 'CANCELED' else 'NULL'
    apply_rows.append(row([
        str(i), apply_no, str(visitor_id), str(host_id), str(dept_id), 'NULL' if vehicle_id is None else str(vehicle_id), sql_str(reason), dt_expr(day_offset,hour,0), dt_expr(day_offset,hour+dur,0),
        sql_str(status), sql_str(access_status), str(comp_count), now_minus(days=max(0,-day_offset)+1,hours=i%12), cancel_time, cancel_reason, sql_str('演示数据：覆盖预约、审批、核验和统计报表场景')
    ]))
lines += insert('visit_apply', ['id','apply_no','visitor_id','host_user_id','department_id','vehicle_id','visit_reason','plan_start_time','plan_end_time','apply_status','access_status','companion_count','submit_time','cancel_time','cancel_reason','remark'], apply_rows, chunk=60)
lines.append('')

# Companions
companion_rows=[]
cid=1
for app in applications:
    i, visitor_id, host_id, dept_id, vehicle_id, reason, day_offset, hour, status, access_status, comp_count, dur = app
    if cid > 90: break
    if comp_count > 0 and i % 2 == 0:
        for j in range(min(comp_count,2)):
            if cid > 90: break
            name = family_names[(cid+5)%len(family_names)] + given_names[(cid*5)%len(given_names)]
            idno = f'500108{1980+(cid%30):04d}{(cid%12)+1:02d}{(cid%28)+1:02d}{(cid+3000)%10000:04d}'
            phone = f'137{(60000000+cid):08d}'
            companion_rows.append(row([str(cid),str(i),sql_str(name),sql_str('ID_CARD'),sql_str(idno),sql_str(phone),sql_str(['家属','同事','项目成员','驾驶员'][cid%4])]))
            cid += 1
lines += insert('visitor_companion', ['id','apply_id','companion_name','id_type','id_number','phone','relation_remark'], companion_rows)
lines.append('')

# Approval records
approval_rows=[]
arid=1
for app in applications:
    i, visitor_id, host_id, dept_id, vehicle_id, reason, day_offset, hour, status, access_status, comp_count, dur = app
    approver = approver_ids[(dept_id-2) % len(approver_ids)]
    if status == 'PENDING_HOST':
        approval_rows.append(row([str(arid),str(i),sql_str('HOST_CONFIRM'),str(host_id),sql_str('PENDING'),sql_str('等待被访人确认'),dt_expr(day_offset, max(7,hour-2), 10),str(1)])); arid+=1
    elif status == 'HOST_REJECTED':
        approval_rows.append(row([str(arid),str(i),sql_str('HOST_CONFIRM'),str(host_id),sql_str('REJECTED'),sql_str('来访时间与教学安排冲突'),dt_expr(day_offset, max(7,hour-2), 20),str(1)])); arid+=1
    elif status == 'HOST_CONFIRMED':
        approval_rows.append(row([str(arid),str(i),sql_str('HOST_CONFIRM'),str(host_id),sql_str('APPROVED'),sql_str('被访人确认接待'),dt_expr(day_offset, max(7,hour-2), 15),str(1)])); arid+=1
        approval_rows.append(row([str(arid),str(i),sql_str('DEPT_APPROVAL'),str(approver),sql_str('PENDING'),sql_str('等待部门审批'),dt_expr(day_offset, max(7,hour-1), 5),str(2)])); arid+=1
    elif status == 'PENDING_DEPT':
        approval_rows.append(row([str(arid),str(i),sql_str('HOST_CONFIRM'),str(host_id),sql_str('APPROVED'),sql_str('被访人确认接待'),dt_expr(day_offset, max(7,hour-2), 15),str(1)])); arid+=1
        approval_rows.append(row([str(arid),str(i),sql_str('DEPT_APPROVAL'),str(approver),sql_str('PENDING'),sql_str('等待部门审批'),dt_expr(day_offset, max(7,hour-1), 5),str(2)])); arid+=1
    elif status == 'REJECTED':
        approval_rows.append(row([str(arid),str(i),sql_str('HOST_CONFIRM'),str(host_id),sql_str('APPROVED'),sql_str('被访人确认接待'),dt_expr(day_offset, max(7,hour-2), 15),str(1)])); arid+=1
        approval_rows.append(row([str(arid),str(i),sql_str('DEPT_APPROVAL'),str(approver),sql_str('REJECTED' if i%3 else 'RETURNED'),sql_str('材料不完整或来访事由不充分'),dt_expr(day_offset, max(7,hour-1), 30),str(2)])); arid+=1
    elif status == 'CANCELED':
        approval_rows.append(row([str(arid),str(i),sql_str('HOST_CONFIRM'),str(host_id),sql_str('PENDING'),sql_str('申请已由访客取消'),dt_expr(day_offset, max(7,hour-2), 10),str(1)])); arid+=1
    elif status == 'REJECTED_BLACKLIST':
        approval_rows.append(row([str(arid),str(i),sql_str('SYSTEM_BLACKLIST_CHECK'),str(1),sql_str('REJECTED'),sql_str('命中黑名单风险，系统拦截'),dt_expr(day_offset, max(7,hour-2), 0),str(0)])); arid+=1
    else:
        approval_rows.append(row([str(arid),str(i),sql_str('HOST_CONFIRM'),str(host_id),sql_str('APPROVED'),sql_str('被访人确认接待'),dt_expr(day_offset, max(7,hour-2), 15),str(1)])); arid+=1
        approval_rows.append(row([str(arid),str(i),sql_str('DEPT_APPROVAL'),str(approver),sql_str('APPROVED'),sql_str('部门审批通过，同意按预约时间入校'),dt_expr(day_offset, max(7,hour-1), 30),str(2)])); arid+=1
        if i % 4 == 0:
            approval_rows.append(row([str(arid),str(i),sql_str('SECURITY_CHECK'),str(guard_ids[i%len(guard_ids)]),sql_str('APPROVED'),sql_str('安保复核通过'),dt_expr(day_offset, max(7,hour-1), 45),str(3)])); arid+=1
lines += insert('approval_record', ['id','apply_id','approval_step','approver_user_id','approval_result','approval_comment','approval_time','sort_order'], approval_rows, chunk=100)
lines.append('')

# Pass codes and access records
pass_rows=[]
access_rows=[]
pcid=1
arid2=1
for app in applications:
    i, visitor_id, host_id, dept_id, vehicle_id, reason, day_offset, hour, status, access_status, comp_count, dur = app
    if status != 'APPROVED':
        continue
    if pcid > 150:
        break
    if access_status in ['ENTERED','EXITED','OVERTIME','ABNORMAL']:
        pass_status = 'USED'
    elif day_offset < -2:
        pass_status = 'EXPIRED'
    elif i % 17 == 0:
        pass_status = 'DISABLED'
    else:
        pass_status = 'VALID'
    used_time = dt_expr(day_offset,hour,10) if pass_status == 'USED' else 'NULL'
    pass_rows.append(row([str(pcid),str(i),sql_str(f'PC{100000+pcid:06d}'),sql_str(f'QR:CQUPT:PC{100000+pcid:06d}:APPLY:{i}'),dt_expr(day_offset,hour-1,0),dt_expr(day_offset,hour+dur,30),sql_str(pass_status),used_time,str(1 if pass_status=='USED' else 0)]))
    if access_status in ['ENTERED','EXITED','OVERTIME','ABNORMAL']:
        entry_gate = 1 + (i % 7)
        exit_gate = 1 + ((i+2) % 7) if access_status in ['EXITED','ABNORMAL'] else None
        entry_guard = guard_ids[i % len(guard_ids)]
        exit_guard = guard_ids[(i+1) % len(guard_ids)] if exit_gate else None
        entry_time = dt_expr(day_offset,hour,12)
        exit_time = 'NULL'
        overtime_flag = 1 if access_status == 'OVERTIME' else 0
        abnormal_reason = 'NULL'
        if access_status == 'EXITED':
            exit_time = dt_expr(day_offset,hour+dur-1,40)
        elif access_status == 'ABNORMAL':
            exit_time = dt_expr(day_offset,hour+dur,20)
            abnormal_reason = sql_str('证件信息与随行人员登记不一致，已人工复核')
        elif access_status == 'OVERTIME':
            abnormal_reason = sql_str('超过预计离校时间，已通知安保跟进')
        access_rows.append(row([str(arid2),str(i),str(visitor_id),str(pcid),str(entry_gate),'NULL' if exit_gate is None else str(exit_gate),str(entry_guard),'NULL' if exit_guard is None else str(exit_guard),entry_time,exit_time,sql_str(access_status),str(overtime_flag),abnormal_reason]))
        arid2 += 1
    pcid += 1
lines += insert('pass_code', ['id','apply_id','pass_code','qr_content','valid_from','valid_to','pass_status','used_time','verify_count'], pass_rows, chunk=80)
lines += insert('access_record', ['id','apply_id','visitor_id','pass_code_id','entry_gate_id','exit_gate_id','entry_guard_id','exit_guard_id','entry_time','exit_time','access_status','overtime_flag','abnormal_reason'], access_rows, chunk=80)
lines.append('')

# Blacklist
blacklist_ids = [8,16,28,39,57,73,91,114,126,132]
blacklist_reasons = ['身份信息异常','多次超时未离校','虚假预约','安全风险提示','证件核验失败','违反校园通行规定','冒用他人预约','异常配送记录','拒不配合离校登记','历史风险人员']
blacklist_rows=[]
for bid, visitor_id in enumerate(blacklist_ids,1):
    v = visitors[visitor_id-1]
    level = ['WARNING','LIMITED','FORBIDDEN'][bid%3]
    status = 'ACTIVE' if bid <= 8 else ('EXPIRED' if bid == 9 else 'REMOVED')
    end = now_plus(days=90) if status == 'ACTIVE' else now_minus(days=bid)
    blacklist_rows.append(row([str(bid),str(visitor_id),sql_str(v[3]),sql_str(v[4]),sql_str(blacklist_reasons[bid-1]),sql_str(level),now_minus(days=60-bid),end,sql_str(status),str(1 if bid%2 else 2)]))
lines += insert('blacklist', ['id','visitor_id','id_number','phone','reason','level','start_time','end_time','status','operator_user_id'], blacklist_rows)
lines.append('')

# Notices
notice_rows=[]
notice_types = [('预约提醒','VISIT_APPLY'),('审批提醒','APPROVAL'),('超时提醒','OVERTIME'),('风险提醒','RISK')]
for i in range(1,121):
    nt, code = notice_types[i%4]
    receiver = users[(i % len(users))][0]
    title = f'{nt}：访客流程待处理 {i:03d}'
    content = ['请及时确认今日访客预约。','部门审批有新的待处理申请。','存在访客超过预计离校时间，请跟进。','命中黑名单或证件异常，请复核。'][i%4]
    read_status = 'UNREAD' if i%5 in [0,1] else 'READ'
    read_time = 'NULL' if read_status == 'UNREAD' else now_minus(days=i%21,hours=(i%24)-1)
    notice_rows.append(row([str(i),str(receiver),sql_str('USER'),sql_str(title),sql_str(content),sql_str(code),str((i % 220) + 1),sql_str(read_status),read_time,now_minus(days=i%21,hours=i%24)]))
lines += insert('notice', ['id','receiver_user_id','receiver_type','title','content','business_type','business_id','read_status','read_time','create_time'], notice_rows, chunk=80)
lines.append('')
# Operation logs
modules = [
    ('认证中心','登录','POST','/api/auth/login','SUCCESS'),('预约管理','新增预约','POST','/api/workflow/apply','SUCCESS'),('审批管理','审批','POST','/api/workflow/approve','SUCCESS'),
    ('门岗核验','核验','POST','/api/workflow/gate/verify','SUCCESS'),('入校登记','登记入校','POST','/api/workflow/access/entry','SUCCESS'),('离校登记','登记离校','POST','/api/workflow/access/leave','SUCCESS'),
    ('黑名单管理','黑名单维护','POST','/api/blacklist','SUCCESS'),('用户管理','用户维护','PUT','/api/system/users','SUCCESS'),('统计报表','报表查询','GET','/api/statistics/dashboard','SUCCESS'),
]
log_rows=[]
for i in range(1,361):
    mod, opt, method, url, result = modules[i%len(modules)]
    user = users[i % len(users)]
    err = 'NULL'
    if i % 47 == 0:
        result = 'FAIL'
        err = sql_str('演示异常：权限不足或参数校验未通过')
    log_rows.append(row([str(i),str(user[0]),sql_str(user[2]),sql_str(mod),sql_str(opt),sql_str(method),sql_str(url),sql_str(result),sql_str(f'192.168.10.{(i%220)+10}'),now_minus(days=i%30,hours=i%24),err]))
lines += insert('operation_log', ['id','operator_user_id','operator_name','module_name','operation_type','request_method','request_url','operation_result','ip_address','operation_time','error_message'], log_rows, chunk=100)
lines.append('')

# Screenshot and report records
screenshot_pages = [
    ('01_login.png','登录页','/login','系统登录与测试账号提示'),('02_dashboard.png','首页驾驶舱','/dashboard','今日访客、趋势和排行'),
    ('03_visitor_apply.png','访客预约申请页','/visit/apply','访客填写预约信息'),('04_my_apply.png','我的预约页','/visit/mine','查看预约状态和通行凭证'),
    ('05_host_confirm.png','待确认预约页','/approval/host','被访人确认或拒绝'),('06_department_approval.png','部门审批页','/approval/department','部门审批人员处理申请'),
    ('07_gate_check.png','门岗核验页','/gate/verify','按通行码或证件核验'),('08_enter_register.png','入校登记页','/access/entry','核验通过后登记入校'),
    ('09_leave_register.png','离校登记页','/access/leave','访客离校登记'),('10_current_visitor.png','当前在校访客页','/access/current','实时展示在校访客'),
    ('11_overtime_visitor.png','超时未离校访客页','/access/overtime','展示超时风险人员'),('12_blacklist.png','黑名单页','/blacklist','维护风险访客'),
    ('13_visit_record.png','访客记录查询页','/records','按条件查询历史记录'),('14_statistics.png','统计报表页','/reports','趋势、排行和状态分布'),
    ('15_user_manage.png','用户管理页','/system/users','系统用户维护'),('16_role_permission.png','角色权限页','/system/roles','角色权限配置'),
    ('17_department_manage.png','部门管理页','/system/departments','部门基础数据维护'),('18_gate_manage.png','校门管理页','/system/gates','校门基础数据维护'),
    ('19_system_log.png','系统日志页','/system/logs','查看关键操作日志'),
]
screenshot_rows=[]
for i,(file,page,route,desc) in enumerate(screenshot_pages,1):
    screenshot_rows.append(row([str(i),sql_str(f'SHOT{i:02d}'),sql_str(page),sql_str(route),sql_str('ADMIN'),sql_str(f'screenshots/{file}'),sql_str(desc),now_minus(days=0,hours=i%4),sql_str('SUCCESS'),str(1)]))
lines += insert('screenshot_record', ['id','screenshot_code','page_name','route_path','role_code','file_path','description','capture_time','status','created_by'], screenshot_rows)
report_rows = [
    (1,'RPT001','重庆邮电大学访客管理系统设计报告','docs/重庆邮电大学访客管理系统设计报告.md','docs/重庆邮电大学访客管理系统设计报告.docx','SUCCESS'),
    (2,'RPT002','重庆邮电大学访客管理系统截图清单','docs/截图说明.md',None,'SUCCESS'),
    (3,'RPT003','重庆邮电大学访客管理系统演示数据说明','docs/演示数据说明.md',None,'SUCCESS'),
]
lines += insert('report_record', ['id','report_code','report_name','markdown_path','word_path','generate_status','generate_time','generated_by','error_message'], [
    row([str(i),sql_str(code),sql_str(n),sql_str(md),sql_str(word),sql_str(s),now_minus(days=i%2,hours=i),str(1), 'NULL']) for i,code,n,md,word,s in report_rows
])
lines.append('')
lines.append('-- 数据规模静态校验参考：')
for tbl in ['department','campus_gate','sys_user','visitor','visit_apply','approval_record','access_record','pass_code','blacklist','visitor_vehicle','visitor_companion','operation_log','notice']:
    lines.append(f"SELECT '{tbl}' AS table_name, COUNT(*) AS total_count FROM {tbl};")
lines.append('')

content = '\n'.join(lines) + '\n'
(db / 'demo_seed.sql').write_text(content, encoding='utf-8')
(db / 'seed.sql').write_text(content, encoding='utf-8')

accounts_doc = '''# 测试账号说明\n\n本系统演示数据保留固定测试账号，所有账号初始密码均为 `123456`。\n\n| 角色 | 用户名 | 密码 | 真实姓名 | 适用页面 |\n| --- | --- | --- | --- | --- |\n| 系统管理员 | admin | 123456 | 系统管理员 | 所有功能、系统配置、用户与权限管理 |\n| 系统管理员 | admin02 | 123456 | 平台管理员 | 所有功能、演示备用管理员账号 |\n| 访客 | visitor01 | 123456 | 访客演示账号 | 预约申请、我的预约、通行凭证查看 |\n| 被访人 | teacher01 | 123456 | 张启航 | 待确认预约、本人相关预约查询 |\n| 部门审批人员 | approver01 | 123456 | 冯立国 | 部门审批、部门相关访客记录 |\n| 门岗安保人员 | guard01 | 123456 | 罗建军 | 门岗核验、入校登记、离校登记、当前在校、超时未离校 |\n| 校级管理人员 | manager01 | 123456 | 许文博 | 统计报表、全校访客记录、运行日志查看 |\n\n## 演示建议\n\n1. 使用 `admin / 123456` 进行自动截图和完整功能演示。\n2. 使用 `teacher01 / 123456` 演示被访人确认流程。\n3. 使用 `approver01 / 123456` 演示部门审批流程。\n4. 使用 `guard01 / 123456` 演示门岗核验、入校和离校登记。\n5. 使用 `manager01 / 123456` 演示统计报表和校级管理视角。\n\n演示数据中的用户、访客、预约、审批、通行码、出入校记录、黑名单、通知和操作日志均由 `database/demo_seed.sql` 生成。\n'''
(docs / '测试账号说明.md').write_text(accounts_doc, encoding='utf-8')

demo_doc = '''# 演示数据说明\n\n`database/demo_seed.sql` 是最终演示、自动截图和课程设计报告使用的增强数据脚本。该脚本不修改数据库表结构，使用 `CURRENT_DATE()` 和 `NOW()` 动态生成日期，保证今日、本周、本月、近七天和未来七天均有可展示数据。\n\n## 数据规模\n\n| 数据对象 | 规模 | 覆盖说明 |\n| --- | ---: | --- |\n| 部门 | 13 个 | 覆盖学院、研究生院、教务处、保卫处、后勤管理处 |\n| 校门 | 7 个 | 覆盖腾飞门、北门、崇文门、西门、西南门、图书馆访客入口、创新创业园门岗 |\n| 系统用户 | 31 个 | 含 2 个管理员、15 个被访教师、6 个审批人员、5 个安保、2 个校级管理人员、1 个访客账号 |\n| 访客 | 140 条 | 覆盖学生家长、企业合作、后勤维修、学术交流、面试、配送、校友、外聘专家 |\n| 预约申请 | 220 条 | 覆盖最近 30 天、未来 7 天、不同部门、不同校门和多种访问事由 |\n| 审批记录 | 450 条 | 覆盖被访人确认、部门审批、安保复核、同意、拒绝、退回修改、待处理 |\n| 通行凭证 | 150 条 | 覆盖有效、已使用、已过期、已作废 |\n| 出入校记录 | 125 条 | 覆盖已入校未离校、已入校已离校、超时未离校、异常处理 |\n| 黑名单 | 10 条 | 覆盖身份异常、多次超时、虚假预约、安全风险等场景 |\n| 车辆 | 70 条 | 车牌号按重庆本地格式生成，并关联部分预约 |\n| 随行人员 | 90 条 | 关联部分预约申请 |\n| 操作日志 | 360 条 | 覆盖登录、预约、审批、入校、离校、黑名单、用户、报表查询 |\n| 通知消息 | 120 条 | 覆盖预约提醒、审批提醒、超时提醒、风险提醒 |\n\n## 业务场景覆盖\n\n- 今日存在多个待确认、待审批、已入校、已离校、超时未离校和黑名单拦截预约。\n- 近七天访客趋势、部门访客排行、校门通行统计、访问事由分布、审批状态分布、入校离校状态分布均有数据。\n- 黑名单访客使用 `ACTIVE` 状态和不同风险等级，预约或门岗核验时可触发风险提示。\n- 门岗核验页面可使用有效通行码 `PC100001`、`PC100002` 等进行查询。\n- 系统日志页面提供 300 条以上数据，能够支撑分页和查询展示。\n\n## 初始化方式\n\n```bash\nbash scripts/init_database.sh\n```\n\n也可以手动执行：\n\n```bash\nmysql -uroot -p < database/create_database.sql\nmysql -uroot -p cqupt_visitor_system < database/schema.sql\nmysql -uroot -p cqupt_visitor_system < database/demo_seed.sql\n```\n\n如果只运行普通初始化脚本，`database/seed.sql` 已同步为增强演示数据。\n'''
(docs / '演示数据说明.md').write_text(demo_doc, encoding='utf-8')
print('generated demo seed and docs')
print(f'departments={len(departments)} gates={len(gates)} users={len(users)} visitors={len(visitors)} applications={len(applications)} approvals={len(approval_rows)} pass_codes={len(pass_rows)} access_records={len(access_rows)}')





