package edu.cqupt.visitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cqupt.visitor.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
