package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.Blacklist;
import edu.cqupt.visitor.mapper.BlacklistMapper;
import edu.cqupt.visitor.service.BlacklistService;
import org.springframework.stereotype.Service;

@Service
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, Blacklist> implements BlacklistService {
}
