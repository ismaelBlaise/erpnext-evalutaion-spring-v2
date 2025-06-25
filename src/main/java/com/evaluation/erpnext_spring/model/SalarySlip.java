package com.evaluation.erpnext_spring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "`tabSalary Slip`")
public class SalarySlip {

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

    @Column(name = "company")
    private String company;

    @Column(name = "department")
    private String department;

    @Column(name = "designation")
    private String designation;

    @Column(name = "branch")
    private String branch;

    @Column(name = "posting_date")
    private LocalDate postingDate;

    @Column(name = "letter_head")
    private String letterHead;

    @Column(name = "status")
    private String status;

    @Column(name = "salary_withholding")
    private Integer salaryWithholding;

    @Column(name = "salary_withholding_cycle")
    private String salaryWithholdingCycle;

    @Column(name = "currency")
    private String currency;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "payroll_frequency")
    private String payrollFrequency;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "salary_structure")
    private String salaryStructure;

    @Column(name = "payroll_entry")
    private String payrollEntry;

    @Column(name = "mode_of_payment")
    private String modeOfPayment;

    @Column(name = "salary_slip_based_on_timesheet")
    private Integer salarySlipBasedOnTimesheet;

    @Column(name = "deduct_tax_for_unclaimed_employee_benefits")
    private Integer deductTaxForUnclaimedEmployeeBenefits;

    @Column(name = "deduct_tax_for_unsubmitted_tax_exemption_proof")
    private Integer deductTaxForUnsubmittedTaxExemptionProof;

    @Column(name = "total_working_days")
    private Double totalWorkingDays;

    @Column(name = "unmarked_days")
    private Double unmarkedDays;

    @Column(name = "leave_without_pay")
    private Double leaveWithoutPay;

    @Column(name = "absent_days")
    private Double absentDays;

    @Column(name = "payment_days")
    private Double paymentDays;

    @Column(name = "total_working_hours")
    private Double totalWorkingHours;

    @Column(name = "hour_rate")
    private BigDecimal hourRate;

    @Column(name = "base_hour_rate")
    private BigDecimal baseHourRate;

    @Column(name = "gross_pay")
    private BigDecimal grossPay;

    @Column(name = "base_gross_pay")
    private BigDecimal baseGrossPay;

    @Column(name = "gross_year_to_date")
    private BigDecimal grossYearToDate;

    @Column(name = "base_gross_year_to_date")
    private BigDecimal baseGrossYearToDate;

    @Column(name = "total_deduction")
    private BigDecimal totalDeduction;

    @Column(name = "base_total_deduction")
    private BigDecimal baseTotalDeduction;

    @Column(name = "net_pay")
    private BigDecimal netPay;

    @Column(name = "base_net_pay")
    private BigDecimal baseNetPay;

    @Column(name = "rounded_total")
    private BigDecimal roundedTotal;

    @Column(name = "base_rounded_total")
    private BigDecimal baseRoundedTotal;

    @Column(name = "year_to_date")
    private BigDecimal yearToDate;

    @Column(name = "base_year_to_date")
    private BigDecimal baseYearToDate;

    @Column(name = "month_to_date")
    private BigDecimal monthToDate;

    @Column(name = "base_month_to_date")
    private BigDecimal baseMonthToDate;

    @Column(name = "total_in_words")
    private String totalInWords;

    @Column(name = "base_total_in_words")
    private String baseTotalInWords;

    @Column(name = "ctc")
    private BigDecimal ctc;

    @Column(name = "income_from_other_sources")
    private BigDecimal incomeFromOtherSources;

    @Column(name = "total_earnings")
    private BigDecimal totalEarnings;

    @Column(name = "non_taxable_earnings")
    private BigDecimal nonTaxableEarnings;

    @Column(name = "standard_tax_exemption_amount")
    private BigDecimal standardTaxExemptionAmount;

    @Column(name = "tax_exemption_declaration")
    private BigDecimal taxExemptionDeclaration;

    @Column(name = "deductions_before_tax_calculation")
    private BigDecimal deductionsBeforeTaxCalculation;

    @Column(name = "annual_taxable_amount")
    private BigDecimal annualTaxableAmount;

    @Column(name = "income_tax_deducted_till_date")
    private BigDecimal incomeTaxDeductedTillDate;

    @Column(name = "current_month_income_tax")
    private BigDecimal currentMonthIncomeTax;

    @Column(name = "future_income_tax_deductions")
    private BigDecimal futureIncomeTaxDeductions;

    @Column(name = "total_income_tax")
    private BigDecimal totalIncomeTax;

    @Column(name = "journal_entry")
    private String journalEntry;

    @Column(name = "amended_from")
    private String amendedFrom;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_no")
    private String bankAccountNo;

    @Column(name = "_user_tags")
    private String userTags;

    @Column(name = "_comments")
    private String comments;

    @Column(name = "_assign")
    private String assign;

    @Column(name = "_liked_by")
    private String likedBy;

    // 🔁 Lien vers les lignes de salaire : earnings/deductions
    @OneToMany
    @JoinColumn(name = "parent", referencedColumnName = "name", insertable = false, updatable = false)
    private List<SalaryDetail> salaryDetails;
}
