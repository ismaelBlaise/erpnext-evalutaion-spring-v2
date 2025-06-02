package com.lucio.erp_new_app_3.dtos.salary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SalaryEarning {

    private String name;
    private String owner;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime creation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime modified;

    @JsonProperty("modified_by")
    private String modifiedBy;

    private int docstatus;
    private int idx;

    @JsonProperty("salary_component")
    private String salaryComponent;

    private String abbr;
    private double amount;

    @JsonProperty("year_to_date")
    private double yearToDate;

    @JsonProperty("is_recurring_additional_salary")
    private int isRecurringAdditionalSalary;

    @JsonProperty("statistical_component")
    private int statisticalComponent;

    @JsonProperty("depends_on_payment_days")
    private int dependsOnPaymentDays;

    @JsonProperty("exempted_from_income_tax")
    private int exemptedFromIncomeTax;

    @JsonProperty("is_tax_applicable")
    private int isTaxApplicable;

    @JsonProperty("is_flexible_benefit")
    private int isFlexibleBenefit;

    @JsonProperty("variable_based_on_taxable_salary")
    private int variableBasedOnTaxableSalary;

    @JsonProperty("do_not_include_in_total")
    private int doNotIncludeInTotal;

    @JsonProperty("deduct_full_tax_on_selected_payroll_date")
    private int deductFullTaxOnSelectedPayrollDate;

    @JsonProperty("amount_based_on_formula")
    private int amountBasedOnFormula;

    @JsonProperty("default_amount")
    private double defaultAmount;

    @JsonProperty("additional_amount")
    private double additionalAmount;

    @JsonProperty("tax_on_flexible_benefit")
    private double taxOnFlexibleBenefit;

    @JsonProperty("tax_on_additional_salary")
    private double taxOnAdditionalSalary;

    private String parent;
    private String parentfield;
    private String parenttype;
    private String doctype;
}
