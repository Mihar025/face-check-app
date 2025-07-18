package com.zikpak.facecheck.taxesServices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayStubFileDTO {

    private Integer payrollId;
    private String fileName;
    private String employeeName;
    private String periodStart;
    private String periodEnd;
    private String downloadUrl;
    private Long fileSize;




}
