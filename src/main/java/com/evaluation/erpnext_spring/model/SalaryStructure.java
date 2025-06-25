package com.evaluation.erpnext_spring.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "`tabSalary Structure`")
public class SalaryStructure {

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

    @Column(name = "company")
    private String company;

    @Column(name = "letter_head")
    private String letterHead;

    @Column(name = "is_active")
    private Integer isActive;

    @Column(name = "is_default")
    private Integer isDefault;

    @Column(name = "currency")
    private String currency;

    @Column(name = "amended_from")
    private String amendedFrom;

    @Column(name = "leave_encashment_amount_per_day")
    private BigDecimal leaveEncashmentAmountPerDay;

    @Column(name = "max_benefits")
    private BigDecimal maxBenefits;

    @Column(name = "salary_slip_based_on_timesheet")
    private Integer salarySlipBasedOnTimesheet;

    @Column(name = "payroll_frequency")
    private String payrollFrequency;

    @Column(name = "salary_component")
    private String salaryComponent;

    @Column(name = "hour_rate")
    private BigDecimal hourRate;

    @Column(name = "total_earning")
    private BigDecimal totalEarning;

    @Column(name = "total_deduction")
    private BigDecimal totalDeduction;

    @Column(name = "net_pay")
    private BigDecimal netPay;

    @Column(name = "mode_of_payment")
    private String modeOfPayment;

    @Column(name = "payment_account")
    private String paymentAccount;

    @Column(name = "_user_tags")
    private String userTags;

    @Column(name = "_comments")
    private String comments;

    @Column(name = "_assign")
    private String assign;

    @Column(name = "_liked_by")
    private String likedBy;

    // 🔁 Liste des détails de la structure salariale : earnings/deductions
    @OneToMany
    @JoinColumn(name = "parent", referencedColumnName = "name", insertable = false, updatable = false)
    private List<SalaryDetail> salaryDetails;
}
