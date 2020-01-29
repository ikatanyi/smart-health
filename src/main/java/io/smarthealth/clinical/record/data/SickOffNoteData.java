/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.visit.domain.Visit;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class SickOffNoteData {

    private Long visitId;

    private String sickOffNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate reviewDate;
}
