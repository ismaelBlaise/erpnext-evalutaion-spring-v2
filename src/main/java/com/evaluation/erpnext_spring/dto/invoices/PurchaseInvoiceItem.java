package com.evaluation.erpnext_spring.dto.invoices;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PurchaseInvoiceItem {
    @JsonProperty("name")
    private String name;

    @JsonProperty("item_code")
    private String itemCode;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("item_group")
    private String itemGroup;

    @JsonProperty("image")
    private String image;

    @JsonProperty("qty")
    private BigDecimal qty;

    @JsonProperty("uom")
    private String uom;

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("base_rate")
    private BigDecimal baseRate;

    @JsonProperty("base_amount")
    private BigDecimal baseAmount;

    @JsonProperty("expense_account")
    private String expenseAccount;

    @JsonProperty("cost_center")
    private String costCenter;

    @JsonProperty("creation")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private Date creation;

    @JsonProperty("modified")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private Date modified;

    // Getters and setters
}