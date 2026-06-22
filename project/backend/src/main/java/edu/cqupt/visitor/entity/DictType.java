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
@TableName("dict_type")
@Schema(description = "字典类型")
public class DictType {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "字典类型主键")
    private Long id;

    @TableField("type_code")
    @Schema(description = "字典类型编码")
    private String typeCode;

    @TableField("type_name")
    @Schema(description = "字典类型名称")
    private String typeName;

    @TableField("status")
    @Schema(description = "状态")
    private String status;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

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
