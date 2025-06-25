package com.evaluation.erpnext_spring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "`tabSalary Structure Assignment`")
public class SalaryStructureAssignment {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "creation")
    private LocalDate creation;

    @Column(name = "modified")
    private LocalDate modified;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "owner")
    private String owner;

    @Column(name = "docstatus")
    private Integer docstatus;

    @Column(name = "idx")
    private Integer idx;

    @Column(name = "employee")
    private String employee;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "department")
    private String department;

    @Column(name = "designation")
    private String designation;

    @Column(name = "grade")
    private String grade;

    @Column(name = "salary_structure")
    private String salaryStructure;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "income_tax_slab")
    private String incomeTaxSlab;

    @Column(name = "company")
    private String company;

    @Column(name = "payroll_payable_account")
    private String payrollPayableAccount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "base")
    private String base;

    @Column(name = "variable")
    private BigDecimal variable;

    @Column(name = "amended_from")
    private String amendedFrom;

    @Column(name = "taxable_earnings_till_date")
    private BigDecimal taxableEarningsTillDate;

    @Column(name = "tax_deducted_till_date")
    private BigDecimal taxDeductedTillDate;

    @Column(name = "_user_tags")
    private String userTags;

    @Column(name = "_comments")
    private String comments;

    @Column(name = "_assign")
    private String assign;

    @Column(name = "_liked_by")
    private String likedBy;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "base_salary")
    private BigDecimal baseSalary;

    @Column(name = "payroll_frequency")
    private String payrollFrequency;
}
