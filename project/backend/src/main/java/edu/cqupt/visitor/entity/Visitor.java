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
@TableName("visitor")
@Schema(description = "访客")
public class Visitor {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "访客主键")
    private Long id;

    @TableField("visitor_name")
    @Schema(description = "访客姓名")
    private String visitorName;

    @TableField("id_type")
    @Schema(description = "证件类型")
    private String idType;

    @TableField("id_number")
    @Schema(description = "证件号码")
    private String idNumber;

    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    @TableField("company")
    @Schema(description = "单位")
    private String company;

    @TableField("gender")
    @Schema(description = "性别")
    private String gender;

    @TableField("visitor_level")
    @Schema(description = "访客等级")
    private String visitorLevel;

    @TableField("status")
    @Schema(description = "访客状态")
    private String status;

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
