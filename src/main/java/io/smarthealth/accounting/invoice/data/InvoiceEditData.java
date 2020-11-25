package io.smarthealth.accounting.invoice.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.accounting.invoice.domain.InvoiceStatus;
import io.smarthealth.infrastructure.lang.Constants;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class InvoiceEditData {

    private Long id;
    private Long payerId;
    private Long schemeId;
    private String scheme;
    private String memberNumber;
    private String memberName;
    @Enumerated(EnumType.STRING)
    private String idNumber;
    private String notes;
}
