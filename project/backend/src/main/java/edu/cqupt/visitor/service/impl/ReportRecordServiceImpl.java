package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.ReportRecord;
import edu.cqupt.visitor.mapper.ReportRecordMapper;
import edu.cqupt.visitor.service.ReportRecordService;
import org.springframework.stereotype.Service;

@Service
public class ReportRecordServiceImpl extends ServiceImpl<ReportRecordMapper, ReportRecord> implements ReportRecordService {
}
