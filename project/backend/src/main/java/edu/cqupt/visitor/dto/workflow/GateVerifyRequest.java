package edu.cqupt.visitor.dto.workflow;

import lombok.Data;

@Data
public class GateVerifyRequest {
    private Long applyId;
    private String applyNo;
    private String phone;
    private String idNumber;
    private String passCode;
    private Long gateId;
}