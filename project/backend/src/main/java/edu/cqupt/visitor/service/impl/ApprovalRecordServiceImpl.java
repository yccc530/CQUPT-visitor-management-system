package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.ApprovalRecord;
import edu.cqupt.visitor.mapper.ApprovalRecordMapper;
import edu.cqupt.visitor.service.ApprovalRecordService;
import org.springframework.stereotype.Service;

@Service
public class ApprovalRecordServiceImpl extends ServiceImpl<ApprovalRecordMapper, ApprovalRecord> implements ApprovalRecordService {
}
