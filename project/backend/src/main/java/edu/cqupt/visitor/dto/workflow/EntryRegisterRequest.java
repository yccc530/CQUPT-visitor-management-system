package edu.cqupt.visitor.dto.workflow;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EntryRegisterRequest {
    private Long applyId;
    private String applyNo;
    private String passCode;

    @NotNull(message = "入校校门不能为空")
    private Long gateId;
}