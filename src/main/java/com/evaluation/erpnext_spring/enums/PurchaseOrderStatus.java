package com.evaluation.erpnext_spring.enums;

public enum PurchaseOrderStatus {
    // DRAFT("Draft", "Brouillon"),
    // ON_HOLD("On Hold", "En attente"),
    // TO_RECEIVE_AND_BILL("To Receive and Bill", "À recevoir et facturer"),
    // TO_BILL("To Bill", "À facturer"),
    // TO_RECEIVE("To Receive", "À recevoir"),
    // COMPLETED("Completed", "Terminé"),
    // CANCELLED("Cancelled", "Annulé"),
    // CLOSED("Closed", "Clôturé"),
    // DELIVERED("Delivered", "Livré");


    RECEIVED("Received","Recu"),
    PAID("Paid","Paye");

    

    private final String key;
    private final String label;

    PurchaseOrderStatus(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public static PurchaseOrderStatus fromKey(String key) {
        for (PurchaseOrderStatus status : values()) {
            if (status.key.equalsIgnoreCase(key)) {
                return status;
            }
        }
        return null;
    }
}
