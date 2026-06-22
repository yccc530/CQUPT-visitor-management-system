package edu.cqupt.visitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cqupt.visitor.entity.Visitor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VisitorMapper extends BaseMapper<Visitor> {
}
