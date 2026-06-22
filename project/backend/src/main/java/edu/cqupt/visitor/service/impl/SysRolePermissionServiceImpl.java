package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.SysRolePermission;
import edu.cqupt.visitor.mapper.SysRolePermissionMapper;
import edu.cqupt.visitor.service.SysRolePermissionService;
import org.springframework.stereotype.Service;

@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements SysRolePermissionService {
}
