package io.smarthealth.administration.app.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author simon.waweru
 */
public interface ContactRepository extends JpaRepository<Contact, Long> {
     
}
