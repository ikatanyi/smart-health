package io.smarthealth.stock.inventory.domain;

import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Repository
public interface RequisitionRepository extends JpaRepository<Requisition, Long> {

    Optional<Requisition> findByRequestionNumber(String reqNo);

    Page<Requisition> findByStatus(RequisitionStatus status, Pageable page);
}
