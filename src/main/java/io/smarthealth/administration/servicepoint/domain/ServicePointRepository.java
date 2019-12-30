package io.smarthealth.administration.servicepoint.domain;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import io.smarthealth.organization.facility.domain.Facility;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ServicePointRepository extends JpaRepository<ServicePoint, Long> {

    Optional<ServicePoint> findByFacilityAndServicePointType(final Facility facility, final ServicePointType servicePointType);
}
