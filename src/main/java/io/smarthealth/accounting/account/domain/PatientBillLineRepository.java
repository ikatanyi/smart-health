/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface PatientBillLineRepository extends JpaRepository<PatientBillLine, Long>{
    
}
