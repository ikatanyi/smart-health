//package io.smarthealth.audit.domain;
//
//import java.time.Instant;
//import java.util.List;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//
///**
// * Spring Data JPA repository for the PersistentAuditEventold entity.
// */
//public interface PersistenceAuditEventRepository extends JpaRepository<PersistentAuditEventold, Long> {
//
//    List<PersistentAuditEventold> findByPrincipal(String principal);
//
//    List<PersistentAuditEventold> findByAuditEventDateAfter(Instant after);
//
//    List<PersistentAuditEventold> findByPrincipalAndAuditEventDateAfter(String principal, Instant after);
//
//    List<PersistentAuditEventold> findByPrincipalAndAuditEventDateAfterAndAuditEventType(String principal, Instant after, String type);
//
//    Page<PersistentAuditEventold> findAllByAuditEventDateBetween(Instant fromDate, Instant toDate, Pageable pageable);
//}
