package edu.cqupt.visitor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.cqupt.visitor.entity.DictItem;
import edu.cqupt.visitor.mapper.DictItemMapper;
import edu.cqupt.visitor.service.DictItemService;
import org.springframework.stereotype.Service;

@Service
public class DictItemServiceImpl extends ServiceImpl<DictItemMapper, DictItem> implements DictItemService {
}
