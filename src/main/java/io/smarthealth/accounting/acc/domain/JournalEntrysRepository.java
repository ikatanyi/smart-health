package io.smarthealth.accounting.acc.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntrysRepository extends JpaRepository<JournalEntryEntity, Long> {

    List<JournalEntryEntity> findByDateBucketBetween(LocalDate start, LocalDate end);

    Optional<JournalEntryEntity> findByTransactionIdentifier(String transactionId);
}
