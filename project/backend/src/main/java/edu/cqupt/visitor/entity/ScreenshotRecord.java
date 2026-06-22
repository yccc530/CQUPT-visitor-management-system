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
@TableName("screenshot_record")
@Schema(description = "截图记录")
public class ScreenshotRecord {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "截图记录主键")
    private Long id;

    @TableField("screenshot_code")
    @Schema(description = "截图编号")
    private String screenshotCode;

    @TableField("page_name")
    @Schema(description = "页面名称")
    private String pageName;

    @TableField("route_path")
    @Schema(description = "前端路由")
    private String routePath;

    @TableField("role_code")
    @Schema(description = "角色编码")
    private String roleCode;

    @TableField("file_path")
    @Schema(description = "文件路径")
    private String filePath;

    @TableField("description")
    @Schema(description = "说明")
    private String description;

    @TableField("capture_time")
    @Schema(description = "截图时间")
    private LocalDateTime captureTime;

    @TableField("status")
    @Schema(description = "状态")
    private String status;

    @TableField("created_by")
    @Schema(description = "创建用户")
    private Long createdBy;

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
