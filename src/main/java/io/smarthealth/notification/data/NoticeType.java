package io.smarthealth.notification.data;

/**
 *
 * @author Kelsas
 */
public enum NoticeType {
    LaboratoryResults("Lab Results"),
    SupplierInvoiceDue("Supplier Invoice Payment"),
    RadiologyResults("Radiology Results");
    private final String label;

    private NoticeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
