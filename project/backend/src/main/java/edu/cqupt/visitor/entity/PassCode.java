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
@TableName("pass_code")
@Schema(description = "通行凭证")
public class PassCode {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "通行凭证主键")
    private Long id;

    @TableField("apply_id")
    @Schema(description = "预约编号")
    private Long applyId;

    @TableField("pass_code")
    @Schema(description = "通行码")
    private String passCode;

    @TableField("qr_content")
    @Schema(description = "二维码内容")
    private String qrContent;

    @TableField("valid_from")
    @Schema(description = "有效开始时间")
    private LocalDateTime validFrom;

    @TableField("valid_to")
    @Schema(description = "有效结束时间")
    private LocalDateTime validTo;

    @TableField("pass_status")
    @Schema(description = "凭证状态")
    private String passStatus;

    @TableField("used_time")
    @Schema(description = "使用时间")
    private LocalDateTime usedTime;

    @TableField("verify_count")
    @Schema(description = "核验次数")
    private Integer verifyCount;

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
