package com.edutech.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDTO {
    private Long id;
    private String leaveType;
    private Integer maxDays;
    private String rules;
    private Boolean isActive;
}
