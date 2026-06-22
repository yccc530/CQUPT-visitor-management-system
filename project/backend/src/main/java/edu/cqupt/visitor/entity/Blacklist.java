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
@TableName("blacklist")
@Schema(description = "黑名单")
public class Blacklist {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "黑名单主键")
    private Long id;

    @TableField("visitor_id")
    @Schema(description = "访客编号")
    private Long visitorId;

    @TableField("id_number")
    @Schema(description = "证件号码")
    private String idNumber;

    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    @TableField("reason")
    @Schema(description = "加入原因")
    private String reason;

    @TableField("level")
    @Schema(description = "黑名单等级")
    private String level;

    @TableField("start_time")
    @Schema(description = "生效时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "失效时间")
    private LocalDateTime endTime;

    @TableField("status")
    @Schema(description = "状态")
    private String status;

    @TableField("operator_user_id")
    @Schema(description = "操作人")
    private Long operatorUserId;

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
