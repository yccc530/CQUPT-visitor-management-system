package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.AccessRecord;
import edu.cqupt.visitor.mapper.AccessRecordMapper;
import edu.cqupt.visitor.service.AccessRecordService;
import org.springframework.stereotype.Service;

@Service
public class AccessRecordServiceImpl extends ServiceImpl<AccessRecordMapper, AccessRecord> implements AccessRecordService {
}
