package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.moh.data.*;
import java.time.LocalDate;

/**
 *
 * @author Kent
 */
public interface TotalTest {

    public String getTestName();

    public Integer getCount();

    public Double getTotalPrice();
    
}
