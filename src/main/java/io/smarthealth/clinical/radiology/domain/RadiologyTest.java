/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.radiology.domain;

import io.smarthealth.clinical.lab.data.LabTestTypeData;
import io.smarthealth.infrastructure.domain.Identifiable;
import io.smarthealth.stock.item.domain.Item;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "radiology_tests",uniqueConstraints = {
    @UniqueConstraint(columnNames = {"scanName", "item_id"}, name="unique_scan_name_item_id")})
@Inheritance(strategy = InheritanceType.JOINED)
public class RadiologyTest extends Identifiable{
    private String scanName; 
    private Boolean status; 
    private String notes;
    @OneToOne
    private Item item;
}
