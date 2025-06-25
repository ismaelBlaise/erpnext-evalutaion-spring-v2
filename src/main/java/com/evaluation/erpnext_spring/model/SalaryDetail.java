package com.evaluation.erpnext_spring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "`tabSalary Detail`")
public class SalaryDetail {

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

    @Column(name = "salary_component")
    private String salaryComponent;

    @Column(name = "abbr")
    private String abbr;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "year_to_date")
    private BigDecimal yearToDate;

    @Column(name = "additional_salary")
    private BigDecimal additionalSalary;

    @Column(name = "is_recurring_additional_salary")
    private Integer isRecurringAdditionalSalary;

    @Column(name = "statistical_component")
    private Integer statisticalComponent;

    @Column(name = "depends_on_payment_days")
    private Integer dependsOnPaymentDays;

    @Column(name = "exempted_from_income_tax")
    private Integer exemptedFromIncomeTax;

    @Column(name = "is_tax_applicable")
    private Integer isTaxApplicable;

    @Column(name = "is_flexible_benefit")
    private Integer isFlexibleBenefit;

    @Column(name = "variable_based_on_taxable_salary")
    private Integer variableBasedOnTaxableSalary;

    @Column(name = "do_not_include_in_total")
    private Integer doNotIncludeInTotal;

    @Column(name = "deduct_full_tax_on_selected_payroll_date")
    private Integer deductFullTaxOnSelectedPayrollDate;

    @Column(name = "condition")
    private String condition;

    @Column(name = "amount_based_on_formula")
    private Integer amountBasedOnFormula;

    @Column(name = "formula")
    private String formula;

    @Column(name = "default_amount")
    private BigDecimal defaultAmount;

    @Column(name = "additional_amount")
    private BigDecimal additionalAmount;

    @Column(name = "tax_on_flexible_benefit")
    private BigDecimal taxOnFlexibleBenefit;

    @Column(name = "tax_on_additional_salary")
    private BigDecimal taxOnAdditionalSalary;

    @Column(name = "parent")
    private String parent;

    @Column(name = "parentfield")
    private String parentField;

    @Column(name = "parenttype")
    private String parentType;
}
