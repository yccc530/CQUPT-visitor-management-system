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
@TableName("dict_item")
@Schema(description = "字典项")
public class DictItem {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "字典项主键")
    private Long id;

    @TableField("type_id")
    @Schema(description = "字典类型编号")
    private Long typeId;

    @TableField("item_code")
    @Schema(description = "字典项编码")
    private String itemCode;

    @TableField("item_name")
    @Schema(description = "字典项名称")
    private String itemName;

    @TableField("item_value")
    @Schema(description = "字典项值")
    private String itemValue;

    @TableField("sort_order")
    @Schema(description = "排序号")
    private Integer sortOrder;

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
