package com.evaluation.erpnext_spring.dto.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyDetailDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("default_bank_account")
    private String defaultBankAccount;

    @JsonProperty("default_payable_account")
    private String defaultPayableAccount;
}
