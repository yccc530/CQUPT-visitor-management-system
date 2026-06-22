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
@TableName("operation_log")
@Schema(description = "操作日志")
public class OperationLog {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "日志主键")
    private Long id;

    @TableField("operator_user_id")
    @Schema(description = "操作用户")
    private Long operatorUserId;

    @TableField("operator_name")
    @Schema(description = "操作人姓名")
    private String operatorName;

    @TableField("module_name")
    @Schema(description = "模块名称")
    private String moduleName;

    @TableField("operation_type")
    @Schema(description = "操作类型")
    private String operationType;

    @TableField("request_method")
    @Schema(description = "请求方法")
    private String requestMethod;

    @TableField("request_url")
    @Schema(description = "请求地址")
    private String requestUrl;

    @TableField("operation_result")
    @Schema(description = "操作结果")
    private String operationResult;

    @TableField("ip_address")
    @Schema(description = "IP 地址")
    private String ipAddress;

    @TableField("operation_time")
    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @TableField("error_message")
    @Schema(description = "错误信息")
    private String errorMessage;

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
