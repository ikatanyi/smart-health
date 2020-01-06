/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.infrastructure.image;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface ClinicalImageRepository extends JpaRepository<ClinicalImage, Long> {

//    void deleteByScan(final Person person);
//
//    ClinicalImage findByScan(final Person person);
}
