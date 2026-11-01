package com.group3.learningcommonsproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
@AllArgsConstructor
public class StaffInfoDto {
    private Long id;
    private String fullName;
    private String email;
    private String position1;
    private String supervisor1;
    private String position2;
    private String supervisor2;
    private Integer CurrentPoints;
}
