/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.doctors.data;

import io.smarthealth.accounting.doctors.domain.DoctorClinicItems;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class DoctorClinicData {

    private Long clinicId;
    private String clinicName;
    @NotNull(message = "Service Item is Required")
    private Long serviceId;
    private String serviceName;
    private String serviceCode;

    private Boolean hasReviewCost;
    private Long reviewServiceId;
    private String reviewServiceName;
    private String reviewServiceCode;

    public static DoctorClinicData map(DoctorClinicItems clinicItems) {
        DoctorClinicData data = new DoctorClinicData();
        data.setClinicId(clinicItems.getId());
        data.setClinicName(clinicItems.getClinicName());
        data.setServiceId(clinicItems.getServiceType().getId());
        data.setServiceCode(clinicItems.getServiceType().getItemCode());
        data.setServiceName(clinicItems.getServiceType().getItemName());
        if (clinicItems.getHasReviewCost()) {
            data.setHasReviewCost(Boolean.TRUE);
            data.setReviewServiceCode(clinicItems.getServiceType().getItemCode());
            data.setReviewServiceName(clinicItems.getServiceType().getItemName());
            data.setReviewServiceId(clinicItems.getServiceType().getId());
        }
        return data;
    }

}
