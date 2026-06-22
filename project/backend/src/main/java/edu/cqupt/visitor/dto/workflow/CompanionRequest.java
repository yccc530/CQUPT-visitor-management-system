package edu.cqupt.visitor.dto.workflow;

import lombok.Data;

@Data
public class CompanionRequest {
    private String companionName;
    private String idType;
    private String idNumber;
    private String phone;
    private String relationRemark;
}