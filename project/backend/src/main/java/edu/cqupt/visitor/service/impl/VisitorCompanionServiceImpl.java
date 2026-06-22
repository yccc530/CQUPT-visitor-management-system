package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.VisitorCompanion;
import edu.cqupt.visitor.mapper.VisitorCompanionMapper;
import edu.cqupt.visitor.service.VisitorCompanionService;
import org.springframework.stereotype.Service;

@Service
public class VisitorCompanionServiceImpl extends ServiceImpl<VisitorCompanionMapper, VisitorCompanion> implements VisitorCompanionService {
}
