/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.clinical.admission.domain.*;
import io.smarthealth.infrastructure.lang.Constants;
import static io.smarthealth.infrastructure.lang.Constants.DATE_TIME_PATTERN;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Data
public class WardTransferData {

    @ApiModelProperty(hidden = true)
    private String patientName;    
    @ApiModelProperty(hidden = true)
    private String patientNumber;
    private Long admissionId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime transferDatetime= LocalDateTime.now();
    @ApiModelProperty(hidden = true)
    private Long wardId;
    @ApiModelProperty(hidden = true)
    private String wardName;
    @ApiModelProperty(hidden = true)
    private String roomName;
    @ApiModelProperty(hidden = true)
    private Long roomId;
    private Long bedId;
    @ApiModelProperty(hidden = true)
    private String bedName;
    private Long bedTypeId; // to bill bed category
    @ApiModelProperty(hidden = true)
    private String bedType;
    private String comment;
    private String methodOfTransfer;

    public WardTransfer map() {
        WardTransfer data = new WardTransfer();
        data.setComment(this.getComment());
        data.setMethodOfTransfer(this.getMethodOfTransfer());
        data.setTransferDatetime(this.getTransferDatetime());

        return data;
    }
}
