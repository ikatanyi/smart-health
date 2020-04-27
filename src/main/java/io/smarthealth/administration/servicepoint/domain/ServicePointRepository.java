package io.smarthealth.administration.servicepoint.domain;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ServicePointRepository extends JpaRepository<ServicePoint, Long>, JpaSpecificationExecutor<ServicePoint> {

//    Optional<ServicePoint> findByServicePointType(final ServicePointType servicePointType);

    List<ServicePoint> findByServicePointType(final ServicePointType servicePointType);

    Page<ServicePoint> findByPointType(String type, Pageable page);

}
