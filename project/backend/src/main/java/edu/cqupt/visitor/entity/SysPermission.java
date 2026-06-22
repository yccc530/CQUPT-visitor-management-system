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
@TableName("sys_permission")
@Schema(description = "权限")
public class SysPermission {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "权限主键")
    private Long id;

    @TableField("permission_code")
    @Schema(description = "权限编码")
    private String permissionCode;

    @TableField("permission_name")
    @Schema(description = "权限名称")
    private String permissionName;

    @TableField("permission_type")
    @Schema(description = "权限类型")
    private String permissionType;

    @TableField("parent_id")
    @Schema(description = "父级权限")
    private Long parentId;

    @TableField("route_path")
    @Schema(description = "前端路由")
    private String routePath;

    @TableField("component_path")
    @Schema(description = "组件路径")
    private String componentPath;

    @TableField("api_path")
    @Schema(description = "接口路径")
    private String apiPath;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @TableField("status")
    @Schema(description = "权限状态")
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
