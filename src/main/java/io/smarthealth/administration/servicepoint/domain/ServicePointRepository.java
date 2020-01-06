package io.smarthealth.administration.servicepoint.domain;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ServicePointRepository extends JpaRepository<ServicePoint, Long> {

    Optional<ServicePoint> findByServicePointType(final ServicePointType servicePointType);
}
