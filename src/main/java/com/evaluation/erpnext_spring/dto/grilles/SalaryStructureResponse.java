package com.evaluation.erpnext_spring.dto.grilles;

import java.util.List;

import lombok.Data;

@Data
public class SalaryStructureResponse {
    public List<SalaryStructureDto> data;
}
