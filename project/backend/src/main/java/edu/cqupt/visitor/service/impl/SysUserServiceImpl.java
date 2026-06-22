package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.SysUser;
import edu.cqupt.visitor.mapper.SysUserMapper;
import edu.cqupt.visitor.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
