package edu.cqupt.visitor.dto.workflow;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class VisitApplySubmitRequest {

    @NotBlank(message = "访客姓名不能为空")
    private String visitorName;

    @NotBlank(message = "证件类型不能为空")
    private String idType;

    @NotBlank(message = "证件号码不能为空")
    private String idNumber;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String company;
    private String gender;

    @NotNull(message = "被访人不能为空")
    private Long hostUserId;

    @NotNull(message = "访问部门不能为空")
    private Long departmentId;

    @NotBlank(message = "来访事由不能为空")
    private String visitReason;

    @NotNull(message = "计划开始时间不能为空")
    private LocalDateTime planStartTime;

    @NotNull(message = "计划结束时间不能为空")
    private LocalDateTime planEndTime;

    private String remark;
    private String vehiclePlateNo;
    private String vehicleType;
    private String vehicleColor;
    private String vehicleBrand;
    private List<CompanionRequest> companions = new ArrayList<>();
}