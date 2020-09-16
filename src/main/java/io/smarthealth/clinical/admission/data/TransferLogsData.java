package io.smarthealth.clinical.admission.data;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class TransferLogsData {
    private String fromBed;
    private String toBed;
    private String fromWard;
    private String toWard;
    private String fromRoom;
    private String toRoom;
    private LocalDate transferDate;
}
