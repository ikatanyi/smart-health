package io.smarthealth.accounting.pricelist.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Deprecated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceItems {

    private Long id;

    private Long serviceId;
    private String serviceCode;
    private String serviceName;

    private Double rate;
 
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate effectiveDate;

    private Boolean active;

}
