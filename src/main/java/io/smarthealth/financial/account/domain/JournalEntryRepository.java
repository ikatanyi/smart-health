package io.smarthealth.financial.account.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long>{
    List<JournalEntry> findByEntryDate(LocalDate entryDate);
}
