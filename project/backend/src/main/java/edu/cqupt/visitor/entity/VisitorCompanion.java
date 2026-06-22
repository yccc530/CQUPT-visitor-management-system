package edu.cqupt.visitor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("visitor_companion")
@Schema(description = "随行人员")
public class VisitorCompanion {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "随行人员主键")
    private Long id;

    @TableField("apply_id")
    @Schema(description = "预约编号")
    private Long applyId;

    @TableField("companion_name")
    @Schema(description = "随行人员姓名")
    private String companionName;

    @TableField("id_type")
    @Schema(description = "证件类型")
    private String idType;

    @TableField("id_number")
    @Schema(description = "证件号码")
    private String idNumber;

    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    @TableField("relation_remark")
    @Schema(description = "关系说明")
    private String relationRemark;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("update_time")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "软删除标记")
    private Integer deleted;
}
