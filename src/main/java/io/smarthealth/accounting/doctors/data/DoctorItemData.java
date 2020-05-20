package io.smarthealth.accounting.doctors.data;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class DoctorItemData {
    private Long id;
    @NotNull(message = "Doctor is Required")
    private Long doctorId;
    private String doctorName;
    @NotNull(message = "Service Item is Required")
    private Long serviceId;
    private String serviceName;
    private String serviceCode;
    private BigDecimal amount;
    private Boolean isPercentage;
    private Boolean active;
    private BigDecimal  commisionValue;

}
