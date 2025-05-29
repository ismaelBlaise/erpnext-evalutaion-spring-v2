package com.evaluation.erpnext_spring.dto.purchase_orders;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PcoDto {
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("creation")
    private String creation;
    
    @JsonProperty("modified")
    private String modified;
    
    @JsonProperty("modified_by")
    private String modifiedBy;
    
    @JsonProperty("owner")
    private String owner;
    
    @JsonProperty("docstatus")
    private Integer docStatus;
    
    @JsonProperty("idx")
    private Integer idx;
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("supplier")
    private String supplier;
    
    @JsonProperty("supplier_name")
    private String supplierName;
    
    @JsonProperty("order_confirmation_no")
    private String orderConfirmationNo;
    
    @JsonProperty("order_confirmation_date")
    private String orderConfirmationDate;
    
    @JsonProperty("transaction_date")
    private String transactionDate;
    
    @JsonProperty("schedule_date")
    private String scheduleDate;
    
    @JsonProperty("company")
    private String company;
    
    @JsonProperty("project")
    private String project;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("conversion_rate")
    private BigDecimal conversionRate;
    
    @JsonProperty("total_qty")
    private BigDecimal totalQty;
    
    @JsonProperty("base_total")
    private BigDecimal baseTotal;
    
    @JsonProperty("base_grand_total")
    private BigDecimal baseGrandTotal;
    
    @JsonProperty("grand_total")
    private BigDecimal grandTotal;
    
    @JsonProperty("rounded_total")
    private BigDecimal roundedTotal;
    
    @JsonProperty("in_words")
    private String inWords;
    
    @JsonProperty("tax_category")
    private String taxCategory;
    
    @JsonProperty("taxes_and_charges")
    private String taxesAndCharges;
    
    @JsonProperty("supplier_address")
    private String supplierAddress;
    
    @JsonProperty("contact_person")
    private String contactPerson;
    
    @JsonProperty("contact_email")
    private String contactEmail;
    
    @JsonProperty("contact_mobile")
    private String contactMobile;
    
    @JsonProperty("shipping_address")
    private String shippingAddress;
    
    @JsonProperty("billing_address")
    private String billingAddress;
    
    @JsonProperty("customer")
    private String customer;
    
    @JsonProperty("customer_name")
    private String customerName;
    
    @JsonProperty("status")
    private String status;

}