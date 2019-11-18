package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 *
 * @author Simon.waweru
 */
public interface DoctorsRequestRepository extends JpaRepository<DoctorRequest, Long>, JpaSpecificationExecutor<DoctorRequest> {

    Page<DoctorRequest> findByVisitAndRequestType(final Visit visit, final String requestType,final Pageable pageable);
    Page<DoctorRequest> findByOrderNumberAndRequestType(final String orderNumber, final String requestType,final Pageable pageable);
    Page<DoctorRequest> findByVisit(final Visit visit,final Pageable pageable);
}
