package io.smarthealth.clinical.record.domain;

import io.smarthealth.clinical.record.data.DoctorRequestData;
import io.smarthealth.clinical.record.data.VisitOrderDTO;
import io.smarthealth.clinical.record.data.VisitOrderItemDTO;
import io.smarthealth.clinical.visit.domain.ResultsRepository;
import io.smarthealth.clinical.record.data.DoctorRequestData.RequestType;
import io.smarthealth.clinical.record.data.enums.FullFillerStatusType;
import io.smarthealth.clinical.visit.domain.Visit;
import io.smarthealth.organization.person.patient.domain.Patient;
import io.smarthealth.stock.item.domain.Item;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("select d FROM DoctorRequest d WHERE d.visit=:visit AND  d.fulfillerStatus=:fulfillerStatus AND d.requestType =:requestType")
    List<DoctorRequest> findServiceRequestsByVisit(@Param("visit") final Visit visit, @Param("fulfillerStatus") final FullFillerStatusType fulfillerStatus, @Param("requestType") final RequestType requestType);

    @Query("SELECT new io.smarthealth.clinical.record.data.VisitOrderDTO(d.visit.visitNumber, d.visit.startDatetime, d.patient.patientNumber, d.patient.fullName, d.requestType, COUNT (d.item), d.requestedBy.name) FROM DoctorRequest d WHERE d.visit.paymentMethod = 'Cash' AND d.fulfillerStatus = 'Unfulfilled' AND d.visit.visitType='Outpatient' AND d.billed = false group by d.visit.visitNumber, d.requestType order by d.visit.startDatetime ")
    Page<VisitOrderDTO> findDoctorCashRequests(Pageable pageable);

    @Query("SELECT new io.smarthealth.clinical.record.data.VisitOrderDTO(d.visit.visitNumber, d.visit.startDatetime, d.patient.patientNumber, d.patient.fullName, d.requestType, COUNT (d.item), d.requestedBy.name) FROM DoctorRequest d WHERE d.visit.paymentMethod = 'Cash' AND d.fulfillerStatus = 'Unfulfilled' AND d.visit.visitType='Outpatient' AND d.billed = false AND d.requestType= :requestType group by d.visit.visitNumber, d.requestType order by d.visit.startDatetime ")
    Page<VisitOrderDTO> findDoctorCashRequests(RequestType requestType, Pageable pageable);

    @Query("SELECT new io.smarthealth.clinical.record.data.VisitOrderDTO(d.visit.visitNumber, d.visit.startDatetime, d.patient.patientNumber, d.patient.fullName, d.requestType, COUNT (d.item), d.requestedBy.name) FROM DoctorRequest d WHERE d.visit.paymentMethod = 'Cash' AND d.fulfillerStatus = 'Unfulfilled' AND d.visit.visitType='Outpatient' AND d.billed = false AND (d.visit.startDatetime BETWEEN :startDate AND :endDate) group by d.visit.visitNumber, d.requestType order by d.visit.startDatetime ")
    Page<VisitOrderDTO> findDoctorCashRequests(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("SELECT new io.smarthealth.clinical.record.data.VisitOrderDTO(d.visit.visitNumber, d.visit.startDatetime, d.patient.patientNumber, d.patient.fullName, d.requestType, COUNT (d.item), d.requestedBy.name) FROM DoctorRequest d WHERE d.visit.paymentMethod = 'Cash' AND d.fulfillerStatus = 'Unfulfilled' AND d.visit.visitType='Outpatient' AND d.billed = false AND (d.visit.startDatetime BETWEEN :startDate AND :endDate) AND d.requestType= :requestType group by d.visit.visitNumber, d.requestType order by d.visit.startDatetime ")
    Page<VisitOrderDTO> findDoctorCashRequests(LocalDateTime startDate, LocalDateTime endDate, RequestType requestType, Pageable pageable);

    @Query("SELECT new io.smarthealth.clinical.record.data.VisitOrderItemDTO(d.id, d.item.id, d.item.itemName, d.item.itemCode, d.item.category, 1.0, d.item.rate, d.requestType) FROM DoctorRequest d WHERE d.visit.paymentMethod = 'Cash' AND d.fulfillerStatus = 'Unfulfilled' AND d.visit.visitType='Outpatient' AND d.billed = false AND d.requestType <> 'Pharmacy' AND d.visit.visitNumber = :visitNumber")
    List<VisitOrderItemDTO> findDoctorCashRequestsItems(String visitNumber);

    @Modifying
    @Query("UPDATE DoctorRequest d SET d.billed=true, d.paid=true where d.id = :requestId")
    void updateBilledAndPaidDoctorRequest(@Param("requestId") Long requestId);

}
