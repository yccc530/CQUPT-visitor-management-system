package edu.cqupt.visitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cqupt.visitor.entity.Blacklist;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlacklistMapper extends BaseMapper<Blacklist> {
}
