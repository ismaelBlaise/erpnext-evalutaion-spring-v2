package com.evaluation.erpnext_spring.model;

import jakarta.persistence.*;
import lombok.Data;
 
import java.time.LocalDate;

@Data
@Entity
@Table(name = "`tabSalary Component`")
public class SalaryComponent {

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

    @Column(name = "salary_component_abbr")
    private String salaryComponentAbbr;

    @Column(name = "type")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "depends_on_payment_days")
    private Integer dependsOnPaymentDays;

    @Column(name = "is_tax_applicable")
    private Integer isTaxApplicable;

    @Column(name = "deduct_full_tax_on_selected_payroll_date")
    private Integer deductFullTaxOnSelectedPayrollDate;

    @Column(name = "variable_based_on_taxable_salary")
    private Integer variableBasedOnTaxableSalary;

    @Column(name = "is_income_tax_component")
    private Integer isIncomeTaxComponent;

    @Column(name = "exempted_from_income_tax")
    private Integer exemptedFromIncomeTax;

    @Column(name = "round_to_the_nearest_integer")
    private Integer roundToTheNearestInteger;

    @Column(name = "statistical_component")
    private Integer statisticalComponent;

    @Column(name = "do_not_include_in_total")
    private Integer doNotIncludeInTotal;

    @Column(name = "remove_if_zero_valued")
    private Integer removeIfZeroValued;

    @Column(name = "disabled")
    private Integer disabled;

    @Column(name = "condition")
    private String condition;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "amount_based_on_formula")
    private Integer amountBasedOnFormula;

    @Column(name = "formula")
    private String formula;

    @Column(name = "is_flexible_benefit")
    private Integer isFlexibleBenefit;

    @Column(name = "max_benefit_amount")
    private Double maxBenefitAmount;

    @Column(name = "pay_against_benefit_claim")
    private Integer payAgainstBenefitClaim;

    @Column(name = "only_tax_impact")
    private Integer onlyTaxImpact;

    @Column(name = "create_separate_payment_entry_against_benefit_claim")
    private Integer createSeparatePaymentEntryAgainstBenefitClaim;

    @Column(name = "_user_tags")
    private String userTags;

    @Column(name = "_comments")
    private String comments;

    @Column(name = "_assign")
    private String assign;

    @Column(name = "_liked_by")
    private String likedBy;
}
