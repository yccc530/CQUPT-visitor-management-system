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
@TableName("access_record")
@Schema(description = "出入校记录")
public class AccessRecord {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "出入校记录主键")
    private Long id;

    @TableField("apply_id")
    @Schema(description = "预约编号")
    private Long applyId;

    @TableField("visitor_id")
    @Schema(description = "访客编号")
    private Long visitorId;

    @TableField("pass_code_id")
    @Schema(description = "通行凭证编号")
    private Long passCodeId;

    @TableField("entry_gate_id")
    @Schema(description = "入校校门")
    private Long entryGateId;

    @TableField("exit_gate_id")
    @Schema(description = "离校校门")
    private Long exitGateId;

    @TableField("entry_guard_id")
    @Schema(description = "入校经办人")
    private Long entryGuardId;

    @TableField("exit_guard_id")
    @Schema(description = "离校经办人")
    private Long exitGuardId;

    @TableField("entry_time")
    @Schema(description = "入校时间")
    private LocalDateTime entryTime;

    @TableField("exit_time")
    @Schema(description = "离校时间")
    private LocalDateTime exitTime;

    @TableField("access_status")
    @Schema(description = "访问状态")
    private String accessStatus;

    @TableField("overtime_flag")
    @Schema(description = "超时标记")
    private Integer overtimeFlag;

    @TableField("abnormal_reason")
    @Schema(description = "异常原因")
    private String abnormalReason;

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

    @TableField(exist = false)
    @Schema(description = "预约编号")
    private String applyNo;

    @TableField(exist = false)
    @Schema(description = "访客姓名")
    private String visitorName;

    @TableField(exist = false)
    @Schema(description = "访客手机号")
    private String phone;

    @TableField(exist = false)
    @Schema(description = "入校校门名称")
    private String entryGateName;

    @TableField(exist = false)
    @Schema(description = "离校校门名称")
    private String exitGateName;

    @TableField(exist = false)
    @Schema(description = "入校安保人员姓名")
    private String entryGuardName;

    @TableField(exist = false)
    @Schema(description = "离校安保人员姓名")
    private String exitGuardName;

    @TableField(exist = false)
    @Schema(description = "通行码")
    private String passCode;
}
