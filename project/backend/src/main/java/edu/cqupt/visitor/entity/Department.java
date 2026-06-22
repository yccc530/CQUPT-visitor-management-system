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
@TableName("department")
@Schema(description = "部门")
public class Department {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "部门主键")
    private Long id;

    @TableField("parent_id")
    @Schema(description = "上级部门")
    private Long parentId;

    @TableField("dept_code")
    @Schema(description = "部门编码")
    private String deptCode;

    @TableField("dept_name")
    @Schema(description = "部门名称")
    private String deptName;

    @TableField("leader_user_id")
    @Schema(description = "部门负责人")
    private Long leaderUserId;

    @TableField("phone")
    @Schema(description = "联系电话")
    private String phone;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

    @TableField("status")
    @Schema(description = "部门状态")
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
