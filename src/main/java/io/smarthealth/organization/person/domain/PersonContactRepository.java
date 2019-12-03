/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface PersonContactRepository extends JpaRepository<PersonContact, Long> {

    Page<PersonContact> findByPerson(final Person person, final Pageable pageable);

    List<PersonContact> findByPerson(final Person person);

    Optional<PersonContact> findByPersonAndIsPrimary(final Person person, final boolean isPrimary);
}
