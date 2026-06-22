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
@TableName("sys_user")
@Schema(description = "系统用户")
public class SysUser {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户主键")
    private Long id;

    @TableField("username")
    @Schema(description = "登录账号")
    private String username;

    @TableField("password_hash")
    @Schema(description = "密码摘要")
    private String passwordHash;

    @TableField("real_name")
    @Schema(description = "真实姓名")
    private String realName;

    @TableField("phone")
    @Schema(description = "手机号")
    private String phone;

    @TableField("email")
    @Schema(description = "邮箱")
    private String email;

    @TableField("department_id")
    @Schema(description = "所属部门")
    private Long departmentId;

    @TableField("user_type")
    @Schema(description = "用户类型")
    private String userType;

    @TableField("status")
    @Schema(description = "账号状态")
    private String status;

    @TableField("last_login_time")
    @Schema(description = "最近登录时间")
    private LocalDateTime lastLoginTime;

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
