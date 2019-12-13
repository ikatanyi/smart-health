/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.domain;

import io.smarthealth.infrastructure.domain.Auditable;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 *
 * @author Simon.Waweru
 */
@Entity
@Data
public class RoomType extends Auditable {

    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String typeCode;
    @Column(nullable = false)
    private double chargesPerNight;
    private String specialComments;

}
