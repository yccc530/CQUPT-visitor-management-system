package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.CampusGate;
import edu.cqupt.visitor.mapper.CampusGateMapper;
import edu.cqupt.visitor.service.CampusGateService;
import org.springframework.stereotype.Service;

@Service
public class CampusGateServiceImpl extends ServiceImpl<CampusGateMapper, CampusGate> implements CampusGateService {
}
