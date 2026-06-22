package edu.cqupt.visitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.cqupt.visitor.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecord> {
}
