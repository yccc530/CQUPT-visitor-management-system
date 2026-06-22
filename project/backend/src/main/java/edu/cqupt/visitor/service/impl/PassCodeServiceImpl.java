package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.PassCode;
import edu.cqupt.visitor.mapper.PassCodeMapper;
import edu.cqupt.visitor.service.PassCodeService;
import org.springframework.stereotype.Service;

@Service
public class PassCodeServiceImpl extends ServiceImpl<PassCodeMapper, PassCode> implements PassCodeService {
}
