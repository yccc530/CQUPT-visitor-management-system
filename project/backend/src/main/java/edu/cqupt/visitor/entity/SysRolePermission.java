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
@TableName("sys_role_permission")
@Schema(description = "角色权限关联")
public class SysRolePermission {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "关联主键")
    private Long id;

    @TableField("role_id")
    @Schema(description = "角色编号")
    private Long roleId;

    @TableField("permission_id")
    @Schema(description = "权限编号")
    private Long permissionId;

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
