package io.smarthealth.notification.domain.enumeration;

import lombok.Getter;

/**
 *
 * @author Kelsas
 */
@Getter
public enum NotificationType {
    ReorderLevel("Items Below Reorder level"),
    ItemExpiry("Items Near Expiry");
    //   OutOfStock("Items Out of Stock"),
    //  UnfinalizedBills("Unfinalized Bills"),
    //   PatientsQueue("Patients in queue for long");
    private final String description;

    private NotificationType(String description) {
        this.description = description;
    }

}
