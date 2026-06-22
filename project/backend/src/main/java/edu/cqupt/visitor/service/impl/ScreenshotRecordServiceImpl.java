package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.ScreenshotRecord;
import edu.cqupt.visitor.mapper.ScreenshotRecordMapper;
import edu.cqupt.visitor.service.ScreenshotRecordService;
import org.springframework.stereotype.Service;

@Service
public class ScreenshotRecordServiceImpl extends ServiceImpl<ScreenshotRecordMapper, ScreenshotRecord> implements ScreenshotRecordService {
}
