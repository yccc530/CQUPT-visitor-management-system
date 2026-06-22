package edu.cqupt.visitor.dto.workflow;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExitRegisterRequest {
    private Long accessRecordId;
    private Long applyId;
    private String applyNo;
    private String passCode;

    @NotNull(message = "离校校门不能为空")
    private Long gateId;
}