package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.DictType;
import edu.cqupt.visitor.mapper.DictTypeMapper;
import edu.cqupt.visitor.service.DictTypeService;
import org.springframework.stereotype.Service;

@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {
}
