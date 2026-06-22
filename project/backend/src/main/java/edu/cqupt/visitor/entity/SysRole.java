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
@TableName("sys_role")
@Schema(description = "角色")
public class SysRole {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "角色主键")
    private Long id;

    @TableField("role_code")
    @Schema(description = "角色编码")
    private String roleCode;

    @TableField("role_name")
    @Schema(description = "角色名称")
    private String roleName;

    @TableField("role_desc")
    @Schema(description = "角色说明")
    private String roleDesc;

    @TableField("status")
    @Schema(description = "角色状态")
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
