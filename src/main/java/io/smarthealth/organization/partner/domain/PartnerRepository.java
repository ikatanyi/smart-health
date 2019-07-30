/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.partner.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface PartnerRepository extends JpaRepository<Partner, String> {

    Page<Partner> findAll(final boolean pageable);

    Optional<Partner> findByCode(final String partnerCode);
}
