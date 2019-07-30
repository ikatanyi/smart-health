/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author simon.waweru
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Optional<Organization> findByCode(final String organizationCode);
}
