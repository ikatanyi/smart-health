package io.smarthealth.security.service;
   
import io.smarthealth.security.config.audit.AuditEventConverter;
import io.smarthealth.security.domain.repositories.PersistenceAuditEventRepository;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * Service for managing audit events.
 * <p>
 * This is the default implementation to support SpringBoot Actuator
 * {@code AuditEventRepository}.
 */
@Slf4j
@Service
@Transactional
public class AuditEventService {

    @Value("${auditEvents.retentionPeriod:30}")
    private long auditRetentionPeriod;

    private final PersistenceAuditEventRepository persistenceAuditEventRepository;

    private final AuditEventConverter auditEventConverter;

    public AuditEventService(PersistenceAuditEventRepository persistenceAuditEventRepository, AuditEventConverter auditEventConverter) {

        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
        this.auditEventConverter = auditEventConverter;
    }

    /**
     * Old audit events should be automatically deleted after 30 days.
     *
     * This is scheduled to get fired at 12:00 (am).
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void removeOldAuditEvents() {
        persistenceAuditEventRepository
                .findByAuditEventDateBefore(Instant.now().minus(auditRetentionPeriod, ChronoUnit.DAYS))
                .forEach(auditEvent -> {
                    log.debug("Deleting audit data {}", auditEvent);
                    persistenceAuditEventRepository.delete(auditEvent);
                });
    }

    public Page<AuditEvent> findAll(Pageable pageable) {
        return persistenceAuditEventRepository.findAll(pageable)
                .map(auditEventConverter::convertToAuditEvent);
    }

    public Page<AuditEvent> findByDates(Instant fromDate, Instant toDate, Pageable pageable) {
        return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable)
                .map(auditEventConverter::convertToAuditEvent);
    }

    public Optional<AuditEvent> find(Long id) {
        return persistenceAuditEventRepository.findById(id)
                .map(auditEventConverter::convertToAuditEvent);
    }
}
