/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.payment.domain;

import io.smarthealth.infrastructure.utility.ContentPage;
import io.smarthealth.organization.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface PaymentTermsRepository extends JpaRepository<PaymentTerms, Long> {

    Page<PaymentTerms> findByOrganization(final Organization organization, final Pageable pageable);

}
