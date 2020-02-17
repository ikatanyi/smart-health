/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.data;

import io.smarthealth.organization.person.domain.Walking;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class WalkingData {

    private String firstName;
    private String secondName;
    private String surname;
    private String idNumber;
    private String phoneNo;
    @ApiModelProperty(hidden = true, required = false)
    private String walkingIdentitificationNo;
    private String specialComments;
    @ApiModelProperty(hidden = true, required = false)
    private String fullName;
    private int age;

    public static WalkingData convertToWalkingData(Walking w) {
        WalkingData d = new WalkingData();
        d.setAge(w.getAge());
        d.setFirstName(w.getFirstName());
        d.setFullName(w.getFullName());
        d.setIdNumber(w.getIdNumber());
        d.setPhoneNo(w.getPhoneNo());
        d.setSecondName(w.getSecondName());
        d.setSpecialComments(w.getSpecialComments());
        d.setSurname(w.getSurname());
        d.setWalkingIdentitificationNo(w.getWalkingIdentitificationNo());
        return d;
    }

    public static Walking convertToWalkingEntity(WalkingData w) {
        Walking e = new Walking();
        e.setAge(w.getAge());
        e.setFirstName(w.getFirstName());
        e.setFullName(w.getFullName());
        e.setIdNumber(w.getIdNumber());
        e.setPhoneNo(w.getPhoneNo());
        e.setSecondName(w.getSecondName());
        e.setSpecialComments(w.getSpecialComments());
        e.setSurname(w.getSurname());
        e.setWalkingIdentitificationNo(w.getWalkingIdentitificationNo());
        return e;
    }

}
