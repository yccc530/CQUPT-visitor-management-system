package edu.cqupt.visitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cqupt.visitor.entity.AccessRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessRecordMapper extends BaseMapper<AccessRecord> {
}
