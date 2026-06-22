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
@TableName("approval_record")
@Schema(description = "审批记录")
public class ApprovalRecord {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "审批记录主键")
    private Long id;

    @TableField("apply_id")
    @Schema(description = "预约编号")
    private Long applyId;

    @TableField("approval_step")
    @Schema(description = "审批环节")
    private String approvalStep;

    @TableField("approver_user_id")
    @Schema(description = "审批人")
    private Long approverUserId;

    @TableField("approval_result")
    @Schema(description = "审批结果")
    private String approvalResult;

    @TableField("approval_comment")
    @Schema(description = "审批意见")
    private String approvalComment;

    @TableField("approval_time")
    @Schema(description = "审批时间")
    private LocalDateTime approvalTime;

    @TableField("sort_order")
    @Schema(description = "审批顺序")
    private Integer sortOrder;

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
