/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain;

import java.util.List;

/**
 *
 * @author Kelsas
 */
public interface CustomizedPatientRepository {

    List<Patient> search(String terms, int limit, int offset);
}
