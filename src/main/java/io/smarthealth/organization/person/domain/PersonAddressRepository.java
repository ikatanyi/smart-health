/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author Simon.waweru
 */
@Repository
public interface PersonAddressRepository extends JpaRepository<PersonAddress, Long> {

    Page<PersonAddress> findByPerson(final Person person, final Pageable pageable);

    List<PersonAddress> findByPerson(final Person person);

}
