package com.evaluation.erpnext_spring.dto.salaries;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
public class SalarySlipDto {
    
    @JsonProperty("name") private String name;
    @JsonProperty("owner") private String owner;
    @JsonProperty("creation") private String creation;
    @JsonProperty("modified") private String modified;
    @JsonProperty("modified_by") private String modifiedBy;
    @JsonProperty("docstatus") private int docStatus;
    @JsonProperty("idx") private int idx;
    
    
    @JsonProperty("employee") private String employee;
    @JsonProperty("employee_name") private String employeeName;
    @JsonProperty("company") private String company;
    @JsonProperty("department") private String department;
    @JsonProperty("designation") private String designation;
    
    
    @JsonProperty("posting_date") private String postingDate;
    @JsonProperty("start_date") private String startDate;
    @JsonProperty("end_date") private String endDate;
    
    @JsonProperty("month_to_date") private String monthToDate;
    
    
    @JsonProperty("status") private String status;
    @JsonProperty("currency") private String currency;
    @JsonProperty("exchange_rate") private double exchangeRate;
    @JsonProperty("payroll_frequency") private String payrollFrequency;
    @JsonProperty("salary_structure") private String salaryStructure;
    @JsonProperty("mode_of_payment") private String modeOfPayment;
    
    
    @JsonProperty("total_working_days") private double totalWorkingDays;
    @JsonProperty("unmarked_days") private double unmarkedDays;
    @JsonProperty("leave_without_pay") private double leaveWithoutPay;
    @JsonProperty("absent_days") private double absentDays;
    @JsonProperty("payment_days") private double paymentDays;
    @JsonProperty("total_working_hours") private double totalWorkingHours;
    
    
    @JsonProperty("hour_rate") private double hourRate;
    @JsonProperty("base_hour_rate") private double baseHourRate;
    
    
    @JsonProperty("gross_pay") private double grossPay;
    @JsonProperty("base_gross_pay") private double baseGrossPay;
    @JsonProperty("total_deduction") private double totalDeduction;
    @JsonProperty("base_total_deduction") private double baseTotalDeduction;
    @JsonProperty("net_pay") private double netPay;
    @JsonProperty("base_net_pay") private double baseNetPay;
    
    
    @JsonProperty("gross_year_to_date") private double grossYearToDate;
    @JsonProperty("base_gross_year_to_date") private double baseGrossYearToDate;
    @JsonProperty("year_to_date") private double yearToDate;
    @JsonProperty("base_year_to_date") private double baseYearToDate;
    
    
    @JsonProperty("total_in_words") private String totalInWords;
    @JsonProperty("base_total_in_words") private String baseTotalInWords;
    @JsonProperty("ctc") private double ctc;
    @JsonProperty("income_from_other_sources") private double incomeFromOtherSources;
    
    
    @JsonProperty("standard_tax_exemption_amount") private double standardTaxExemptionAmount;
    @JsonProperty("total_income_tax") private double totalIncomeTax;
    
    
    @JsonProperty("timesheets") private List<Object> timesheets;
    @JsonProperty("earnings") private List<Object> earnings;
    @JsonProperty("deductions") private List<Object> deductions;
    @JsonProperty("leave_details") private List<Object> leaveDetails;
    @JsonProperty("non_taxable_earnings") private double nonTaxableEarnings;
    @JsonProperty("tax_exemption_declaration") private double taxExemptionDeclaration;
    @JsonProperty("future_income_tax_deductions") private double futureIncomeTaxDeductions;
    @JsonProperty("annual_taxable_amount") private double annualTaxableAmount;
    
    
    
    
    @JsonProperty("salary_slip_based_on_timesheet") private int salarySlipBasedOnTimesheet;
    @JsonProperty("deduct_tax_for_unclaimed_employee_benefits") private int deductTaxForUnclaimedEmployeeBenefits;
    @JsonProperty("deduct_tax_for_unsubmitted_tax_exemption_proof") private int deductTaxForUnsubmittedTaxExemptionProof;
    
    @JsonProperty("doctype") private String docType;
}