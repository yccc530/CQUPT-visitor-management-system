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
@TableName("visit_apply")
@Schema(description = "访客预约申请")
public class VisitApply {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "预约主键")
    private Long id;

    @TableField("apply_no")
    @Schema(description = "预约编号")
    private String applyNo;

    @TableField("visitor_id")
    @Schema(description = "访客编号")
    private Long visitorId;

    @TableField("host_user_id")
    @Schema(description = "被访人编号")
    private Long hostUserId;

    @TableField("department_id")
    @Schema(description = "部门编号")
    private Long departmentId;

    @TableField("vehicle_id")
    @Schema(description = "车辆编号")
    private Long vehicleId;

    @TableField("visit_reason")
    @Schema(description = "来访事由")
    private String visitReason;

    @TableField("plan_start_time")
    @Schema(description = "计划开始时间")
    private LocalDateTime planStartTime;

    @TableField("plan_end_time")
    @Schema(description = "计划结束时间")
    private LocalDateTime planEndTime;

    @TableField("apply_status")
    @Schema(description = "预约状态")
    private String applyStatus;

    @TableField("access_status")
    @Schema(description = "通行状态")
    private String accessStatus;

    @TableField("companion_count")
    @Schema(description = "随行人数")
    private Integer companionCount;

    @TableField("submit_time")
    @Schema(description = "提交时间")
    private LocalDateTime submitTime;

    @TableField("cancel_time")
    @Schema(description = "取消时间")
    private LocalDateTime cancelTime;

    @TableField("cancel_reason")
    @Schema(description = "取消原因")
    private String cancelReason;

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
