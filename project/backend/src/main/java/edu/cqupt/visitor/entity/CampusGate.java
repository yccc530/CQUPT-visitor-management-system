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
@TableName("campus_gate")
@Schema(description = "校门")
public class CampusGate {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "校门主键")
    private Long id;

    @TableField("gate_code")
    @Schema(description = "校门编码")
    private String gateCode;

    @TableField("gate_name")
    @Schema(description = "校门名称")
    private String gateName;

    @TableField("gate_location")
    @Schema(description = "校门位置")
    private String gateLocation;

    @TableField("gate_type")
    @Schema(description = "校门类型")
    private String gateType;

    @TableField("status")
    @Schema(description = "校门状态")
    private String status;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

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
