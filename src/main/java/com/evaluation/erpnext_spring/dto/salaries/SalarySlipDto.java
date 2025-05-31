package com.evaluation.erpnext_spring.dto.salaries;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SalarySlipDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("employee")
    private String employee;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("net_pay")
    private Double netPay;

    @JsonProperty("status")
    private String status;
}
