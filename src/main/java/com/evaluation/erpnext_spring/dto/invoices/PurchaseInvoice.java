package com.evaluation.erpnext_spring.dto.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseInvoice {
    @JsonProperty("name")
    private String name;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("creation")
    private String creation;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("modified_by")
    private String modifiedBy;

    @JsonProperty("docstatus")
    private Integer docStatus;

    @JsonProperty("idx")
    private Integer idx;

    @JsonProperty("title")
    private String title;

    @JsonProperty("naming_series")
    private String namingSeries;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("supplier_name")
    private String supplierName;

    @JsonProperty("company")
    private String company;

    @JsonProperty("posting_date")
    private String postingDate;

    @JsonProperty("posting_time")
    private String postingTime;

    @JsonProperty("due_date")
    private String dueDate;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("conversion_rate")
    private BigDecimal conversionRate;

    @JsonProperty("total_qty")
    private BigDecimal totalQty;

    @JsonProperty("base_total")
    private BigDecimal baseTotal;

    @JsonProperty("base_net_total")
    private BigDecimal baseNetTotal;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("net_total")
    private BigDecimal netTotal;

    @JsonProperty("base_grand_total")
    private BigDecimal baseGrandTotal;

    @JsonProperty("grand_total")
    private BigDecimal grandTotal;

    @JsonProperty("outstanding_amount")
    private BigDecimal outstandingAmount;

    @JsonProperty("paid_amount")
    private BigDecimal paidAmount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("is_paid")
    private Integer isPaid;

    @JsonProperty("is_return")
    private Integer isReturn;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("items")
    private List<PurchaseInvoiceItem> items;

    // @JsonProperty("taxes")
    // private List<PurchaseInvoiceTax> taxes;

    // @JsonProperty("payment_schedule")
    // private List<PaymentSchedule> paymentSchedule;

   
}