package io.smarthealth.administration.servicepoint.domain;

import io.smarthealth.administration.servicepoint.data.ServicePointType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ServicePointRepository extends JpaRepository<ServicePointsss, Long>, JpaSpecificationExecutor<ServicePointsss> {

//    Optional<ServicePoint> findByServicePointType(final ServicePointType servicePointType);
    @Query("SELECT sp FROM ServicePoint sp WHERE sp.servicePointType=:servicePointType")
    Optional<ServicePointsss> findServicePointByServicePointType(final ServicePointType servicePointType);

    List<ServicePointsss> findByServicePointType(final ServicePointType servicePointType);

    Page<ServicePointsss> findByPointType(String type, Pageable page);

}
