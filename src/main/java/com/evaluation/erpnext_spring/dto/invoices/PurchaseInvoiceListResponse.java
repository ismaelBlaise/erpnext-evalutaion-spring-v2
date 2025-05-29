package com.evaluation.erpnext_spring.dto.invoices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PurchaseInvoiceListResponse {

    @JsonProperty("data")
    private List<PurchaseInvoiceDTO> data;
}
