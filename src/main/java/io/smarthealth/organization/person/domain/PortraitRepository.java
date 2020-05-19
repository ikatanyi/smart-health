/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface PortraitRepository extends JpaRepository<Portrait, Long> {

    void deleteByPerson(final Person person);

    Optional<Portrait> findByPerson(final Person person);
}
