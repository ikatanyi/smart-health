package io.smarthealth.stock.inventory.domain.enumeration;

/**
 *
 * @author Kelsas
 */
public enum MovementType {
    /**
         * Items received from Suppliers against Purchase Orders.
         */
        Purchase,
        /**
         * Items transferred from one Warehouse to another.
         */
        Stock_Entry,
        /**
         * Items dispensed to patient
         */
        Dispensed,
        /**
         * Items Adjusted after stock retake
         */
        Adjustment;
}
