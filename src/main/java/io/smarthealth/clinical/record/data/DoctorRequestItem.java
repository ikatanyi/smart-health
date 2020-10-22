/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.record.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN_DD_MM_YYYY;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Simon.Waweru
 */
@Data
public class DoctorRequestItem {

    private Long itemId;
    private String itemName;
    private String code;
    private double costRate;
    private double rate;
    private Long requestItemId;
    @ApiModelProperty(hidden = true)
    private PrescriptionData prescriptionData;

    private String orderNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN_DD_MM_YYYY
    )
    private LocalDate orderDate;
    private String requestedByName, requestedByNo, status;

}
