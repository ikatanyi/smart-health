package io.smarthealth.notification.domain.enumeration;

import lombok.Getter;

/**
 *
 * @author Kelsas
 */
@Getter 
public enum NotificationType {
    ReorderLevel("Items Below Reorder level"),
    OutOfStock("Items Out of Stock"),
    ItemExpiry("Items Near Expiry"),
    UnfinalizedBills("Unfinalized Bills"),
    PatientsQueue("Patients in queue for long");
    private final String description;

    private NotificationType(String description) {
        this.description = description;
    }

}
