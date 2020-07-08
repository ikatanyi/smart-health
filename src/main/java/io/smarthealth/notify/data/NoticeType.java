package io.smarthealth.notify.data;

/**
 *
 * @author Kelsas
 */
public enum NoticeType {
    LaboratoryResults("Lab Results"),
    RadiologyResults("Radiology Results");
    private final String label;

    private NoticeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
