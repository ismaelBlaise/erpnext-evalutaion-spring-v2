package com.evaluation.erpnext_spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tabSalary Slip")
public class SalarySlip {
    @Id
    @Column
    private String name;

}
