package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.VisitApply;
import edu.cqupt.visitor.mapper.VisitApplyMapper;
import edu.cqupt.visitor.service.VisitApplyService;
import org.springframework.stereotype.Service;

@Service
public class VisitApplyServiceImpl extends ServiceImpl<VisitApplyMapper, VisitApply> implements VisitApplyService {
}
