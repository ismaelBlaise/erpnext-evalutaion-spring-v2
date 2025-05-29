package com.evaluation.erpnext_spring.dto.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseInvoiceDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("title")
    private String title;

    @JsonProperty("supplier_id")
    private String supplier;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("supplier_address")
    private String supplierAddress;

    @JsonProperty("contact_person")
    private String contactPerson;

    @JsonProperty("contact_email")
    private String contactEmail;

    @JsonProperty("contact_mobile")
    private String contactMobile;

    @JsonProperty("company")
    private String company;

    @JsonProperty("posting_date")
    private String postingDate;

    @JsonProperty("due_date")
    private String dueDate;

    @JsonProperty("bill_date")
    private String billDate;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("grand_total")
    private BigDecimal grandTotal;

    @JsonProperty("base_grand_total")
    private BigDecimal baseGrandTotal;

    @JsonProperty("net_total")
    private BigDecimal netTotal;

    @JsonProperty("base_total")
    private BigDecimal baseTotal;

    @JsonProperty("base_total_taxes_and_charges")
    private BigDecimal baseTotalTaxesAndCharges;

    @JsonProperty("total_taxes_and_charges")
    private BigDecimal totalTaxesAndCharges;

    @JsonProperty("paid_amount")
    private BigDecimal paidAmount;

    @JsonProperty("outstanding_amount")
    private BigDecimal outstandingAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("is_paid")
    private Boolean isPaid;

    @JsonProperty("is_return")
    private Boolean isReturn;

    @JsonProperty("return_against")
    private String returnAgainst;

    @JsonProperty("amended_from")
    private String amendedFrom;

    @JsonProperty("tax_id")
    private String taxId;

    @JsonProperty("tax_category")
    private String taxCategory;

    @JsonProperty("tax_withholding_category")
    private String taxWithholdingCategory;

    @JsonProperty("mode_of_payment")
    private String modeOfPayment;

    @JsonProperty("payment_terms_template")
    private String paymentTermsTemplate;

    @JsonProperty("cost_center")
    private String costCenter;

    @JsonProperty("project")
    private String project;

    @JsonProperty("update_stock")
    private Boolean updateStock;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("dispatch_address")
    private String dispatchAddress;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("terms")
    private String terms;
}
