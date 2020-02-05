/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.record.domain.SickOffNote;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class SickOffNoteData {

    private String visitNo;

    private String sickOffNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate reviewDate;

    public static SickOffNoteData map(SickOffNote s) {
        SickOffNoteData data = new SickOffNoteData();
        data.setEndDate(s.getEndDate());
        data.setStartDate(s.getStartDate());
        data.setReviewDate(s.getReviewDate());
        data.setSickOffNumber(s.getSickOffNumber());
        return data;
    }

    public static SickOffNote map(SickOffNoteData data) {
        SickOffNote s = new SickOffNote();
        s.setEndDate(data.getEndDate());
        s.setStartDate(data.getStartDate());
        s.setReviewDate(data.getReviewDate());
        s.setSickOffNumber(data.getSickOffNumber());
        return s;
    }
}
