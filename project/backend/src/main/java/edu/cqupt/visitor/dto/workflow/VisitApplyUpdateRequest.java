package edu.cqupt.visitor.dto.workflow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class VisitApplyUpdateRequest {
    private String visitReason;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    private String remark;
    private String vehiclePlateNo;
    private String vehicleType;
    private String vehicleColor;
    private String vehicleBrand;
    private List<CompanionRequest> companions = new ArrayList<>();
}