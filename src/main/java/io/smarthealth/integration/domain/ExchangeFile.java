package io.smarthealth.integration.domain;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Table; 
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
@Entity 
@Table(name = "exchange_files")
public class ExchangeFile extends Identifiable {

    private String globalID;
    private String memberNr;
    private Long admitId;
    private Long progressFlag;
    private String rejectionReason;
    private Long exchangeType;
    private Long inOutType;
    private String locationId;
    private LocalDate smartDate;
    private byte[] smartFile;
    private LocalDate exchangeDate;
    private byte[] exchangeFile;
    private LocalDate resultDate;
    private String resultFile;
}
