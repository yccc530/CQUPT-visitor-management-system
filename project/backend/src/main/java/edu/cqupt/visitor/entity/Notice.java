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
@TableName("notice")
@Schema(description = "通知消息")
public class Notice {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "通知主键")
    private Long id;

    @TableField("receiver_user_id")
    @Schema(description = "接收用户")
    private Long receiverUserId;

    @TableField("receiver_type")
    @Schema(description = "接收对象类型")
    private String receiverType;

    @TableField("title")
    @Schema(description = "标题")
    private String title;

    @TableField("content")
    @Schema(description = "内容")
    private String content;

    @TableField("business_type")
    @Schema(description = "业务类型")
    private String businessType;

    @TableField("business_id")
    @Schema(description = "业务编号")
    private Long businessId;

    @TableField("read_status")
    @Schema(description = "阅读状态")
    private String readStatus;

    @TableField("read_time")
    @Schema(description = "阅读时间")
    private LocalDateTime readTime;

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
