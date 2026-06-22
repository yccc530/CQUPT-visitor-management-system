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
@TableName("report_record")
@Schema(description = "报告记录")
public class ReportRecord {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "报告记录主键")
    private Long id;

    @TableField("report_code")
    @Schema(description = "报告编号")
    private String reportCode;

    @TableField("report_name")
    @Schema(description = "报告名称")
    private String reportName;

    @TableField("markdown_path")
    @Schema(description = "Markdown 路径")
    private String markdownPath;

    @TableField("word_path")
    @Schema(description = "Word 路径")
    private String wordPath;

    @TableField("generate_status")
    @Schema(description = "生成状态")
    private String generateStatus;

    @TableField("generate_time")
    @Schema(description = "生成时间")
    private LocalDateTime generateTime;

    @TableField("generated_by")
    @Schema(description = "生成人")
    private Long generatedBy;

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
