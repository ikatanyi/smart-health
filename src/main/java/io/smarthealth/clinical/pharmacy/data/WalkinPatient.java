package io.smarthealth.clinical.pharmacy.data;

import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class WalkinPatient {

    private Long id;
    private String patientName;
    private String walkinNumber;
    private String contact;
}
