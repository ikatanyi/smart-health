package io.smarthealth.organization.facility.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.waweru
 */
public interface FacilityRepository extends JpaRepository<Facility, Long> {

//    Optional<Facility> findByCode(String facilityCode);
}
