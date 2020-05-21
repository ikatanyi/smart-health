/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.record.domain.SickOffNote;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

    private String recommendation;
    private String reason;
    @ApiModelProperty(required = false, hidden = true)
    private Long duration;
    @ApiModelProperty(required = false, hidden = true)
    private String createdBy;

    public static SickOffNoteData map(SickOffNote s) {
        SickOffNoteData data = new SickOffNoteData();

        data.setVisitNo(s.getVisit().getVisitNumber());
        data.setEndDate(s.getEndDate());
        data.setStartDate(s.getStartDate());
        data.setReviewDate(s.getReviewDate());
        data.setSickOffNumber(s.getSickOffNumber());
        data.setRecommendation(s.getRecommendation());
        data.setReason(s.getReason());
        data.setDuration(ChronoUnit.DAYS.between(s.getStartDate(), s.getEndDate())-1);
        data.setCreatedBy(s.getCreatedBy());

        return data;
    }

    public static SickOffNote map(SickOffNoteData data) {
        SickOffNote s = new SickOffNote();
        s.setEndDate(data.getEndDate());
        s.setStartDate(data.getStartDate());
        s.setReviewDate(data.getReviewDate());
        s.setSickOffNumber(data.getSickOffNumber());
        s.setRecommendation(data.getRecommendation());
        s.setReason(data.getReason());
        return s;
    }
}
