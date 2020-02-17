/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface WalkingRepository extends JpaRepository<Walking, Long>, JpaSpecificationExecutor<Walking> {
    
    Optional<Walking> findByWalkingIdentitificationNo(final String walkingIdentitificationNo);
}
