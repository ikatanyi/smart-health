package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.visit.domain.ResultsRepository;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Simon.waweru
 */
public interface DoctorsRequestRepository extends JpaRepository<DoctorRequest, Long>, JpaSpecificationExecutor<DoctorRequest> {

    Page<DoctorRequest> findByVisitAndRequestType(final Visit visit, final RequestType requestType, final Pageable pageable);

    List<DoctorRequest> findByVisitAndItem(final Visit visit, final Item item);

    Page<DoctorRequest> findByOrderNumberAndRequestType(final String orderNumber, final String requestType, final Pageable pageable);

    Page<DoctorRequest> findByVisit(final Visit visit, final Pageable pageable);

    @Query("select d FROM DoctorRequest d WHERE d.patient=:patient AND  d.fulfillerStatus=:fulfillerStatus AND requestType=:requestType")
    List<DoctorRequest> findServiceRequestsByPatient(@Param("patient") final Patient patient, @Param("fulfillerStatus") final FullFillerStatusType fulfillerStatus, @Param("requestType") final RequestType requestType);

}
