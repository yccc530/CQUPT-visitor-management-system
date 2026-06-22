package edu.cqupt.visitor.dto.workflow;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class GateVerifyResponse {
    private Boolean allowed;
    private String message;
    private Long applyId;
    private String applyNo;
    private Long visitorId;
    private String visitorName;
    private String phone;
    private String idNumber;
    private String applyStatus;
    private String accessStatus;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    private Long passCodeId;
    private String passCode;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
}