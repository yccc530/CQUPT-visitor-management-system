package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.VisitorVehicle;
import edu.cqupt.visitor.mapper.VisitorVehicleMapper;
import edu.cqupt.visitor.service.VisitorVehicleService;
import org.springframework.stereotype.Service;

@Service
public class VisitorVehicleServiceImpl extends ServiceImpl<VisitorVehicleMapper, VisitorVehicle> implements VisitorVehicleService {
}
