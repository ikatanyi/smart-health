package io.smarthealth.integration.data;


import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class FileData  {
    private String memberNumber;
    private Long admitId;
    private Long progressFlag;
    private String rejectionReason;
}
