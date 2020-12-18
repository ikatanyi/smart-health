/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.doctors.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
@Entity 
public class DoctorClinicItems extends Auditable {

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_clinic_service_id"))
    private Item serviceType;

    @Column(nullable = false, unique = true)
    private String clinicName;
    

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean hasReviewCost;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_doctor_clinic_review_service_id"))
    private Item reviewService;

}
