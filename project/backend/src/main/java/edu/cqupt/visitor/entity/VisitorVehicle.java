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
@TableName("visitor_vehicle")
@Schema(description = "访客车辆")
public class VisitorVehicle {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "车辆主键")
    private Long id;

    @TableField("visitor_id")
    @Schema(description = "访客编号")
    private Long visitorId;

    @TableField("plate_no")
    @Schema(description = "车牌号")
    private String plateNo;

    @TableField("vehicle_type")
    @Schema(description = "车辆类型")
    private String vehicleType;

    @TableField("color")
    @Schema(description = "颜色")
    private String color;

    @TableField("brand")
    @Schema(description = "品牌")
    private String brand;

    @TableField("status")
    @Schema(description = "车辆状态")
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
