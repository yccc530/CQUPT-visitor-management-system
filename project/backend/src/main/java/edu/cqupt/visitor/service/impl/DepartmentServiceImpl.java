package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.Department;
import edu.cqupt.visitor.mapper.DepartmentMapper;
import edu.cqupt.visitor.service.DepartmentService;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
}
