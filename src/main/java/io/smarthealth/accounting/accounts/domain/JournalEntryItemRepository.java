package io.smarthealth.accounting.accounts.domain;

import java.util.List;
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
public interface JournalEntryItemRepository extends JpaRepository<JournalEntryItem, Long>, JpaSpecificationExecutor<JournalEntryItem>, AccountlBalanceRepository {

    Page<JournalEntryItem> findByAccount(Account account, Pageable page);

    List<JournalEntryItem> findByAccount(Account account);
}
