package com.evaluation.erpnext_spring.enums;

public enum PurchaseInvoiceStatus {
    DRAFT("Draft", "Brouillon"),
    RETURN("Return", "Retour"),
    DEBIT_NOTE_ISSUED("Debit Note Issued", "Note de débit émise"),
    SUBMITTED("Submitted", "Soumis"),
    PAID("Paid", "Payé"),
    PARTLY_PAID("Partly Paid", "Partiellement payé"),
    UNPAID("Unpaid", "Impayé"),
    OVERDUE("Overdue", "En retard"),
    CANCELLED("Cancelled", "Annulé"),
    INTERNAL_TRANSFER("Internal Transfer", "Transfert interne");

    private final String key;
    private final String label;

    PurchaseInvoiceStatus(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public static PurchaseInvoiceStatus fromKey(String key) {
        for (PurchaseInvoiceStatus status : values()) {
            if (status.key.equalsIgnoreCase(key)) {
                return status;
            }
        }
        return null;
    }
}
