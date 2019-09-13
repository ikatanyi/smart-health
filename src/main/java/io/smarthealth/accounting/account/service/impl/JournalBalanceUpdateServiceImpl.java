package io.smarthealth.accounting.account.service.impl;
 
import io.smarthealth.accounting.account.domain.AccountType;
import io.smarthealth.accounting.account.domain.JournalEntry;
import io.smarthealth.accounting.account.domain.JournalEntryRepository;
import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import io.smarthealth.accounting.account.service.JournalBalanceUpdateService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class JournalBalanceUpdateServiceImpl implements JournalBalanceUpdateService {

    private final String balanceSql = "select je.running_balance as runningBalance,je.account_id as accountId from account_journal_entry je\n"
            + "inner join (select max(id) as id from account_journal_entry where entry_date < ? group by account_id,entry_date) je2 \n"
            + "inner join (select max(entry_date) as date from account_journal_entry where entry_date < ? group by account_id) je3 \n"
            + "where je2.id = je.id and je.entry_date = je3.date group by je.id order by je.entry_date DESC limit 0, 10000";

    private final JdbcTemplate jdbcTemplate;
    private final JournalEntryRepository journalEntryRepository;

    public JournalBalanceUpdateServiceImpl(JdbcTemplate jdbcTemplate, JournalEntryRepository journalEntryRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.journalEntryRepository = journalEntryRepository;
    }

    @Override
    public void updateRunningBalance() {
        LocalDate entityDate = this.jdbcTemplate.queryForObject("select MIN(je.entry_date) as entityDate from account_journal_entry  je  where je.is_balance_calculated=0", LocalDate.class);
        log.info("running balance for date .. {0} ", entityDate);
        runBalanceAsAt(entityDate);

    }

    private void runBalanceAsAt(LocalDate entityDate) {
        log.info("Running Journal Balances as at {0} ", entityDate);
        Map<Long, BigDecimal> runningBalanceMap = new HashMap<>(5);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(balanceSql, entityDate, entityDate);

        list.forEach((entries) -> {
            Long accountId = Long.parseLong(entries.get("accountId").toString()); //Drizzle is returning Big Integer where as MySQL returns Long.
            if (!runningBalanceMap.containsKey(accountId)) {
                runningBalanceMap.put(accountId, (BigDecimal) entries.get("runningBalance"));
            }
        });

        List<JournalEntry> entryDatas = journalEntryRepository.findByEntryDate(entityDate);
        entryDatas.stream().map((entryData) -> {
            BigDecimal runningBalance = calculateRunningBalance(entryData, runningBalanceMap);
            entryData.setRunningBalance(runningBalance);
            entryData.setBalanceCalculated(true);
            return entryData;
        }).forEachOrdered((entryData) -> {
            journalEntryRepository.save(entryData);
        });
    }

    private BigDecimal calculateRunningBalance(JournalEntry entry, Map<Long, BigDecimal> runningBalanceMap) {
        BigDecimal runningBalance = BigDecimal.ZERO;
        if (runningBalanceMap.containsKey(entry.getAccount().getId())) {
            runningBalance = runningBalanceMap.get(entry.getAccount().getId());
        }

        AccountCategory accountType = entry.getAccount().getAccountType().getGlAccountType();

        boolean isIncrease = false;

        switch (accountType) {
            case ASSET:
                if (entry.isDebit()) { //debit
                    isIncrease = true;
                }
                break;
            case EQUITY:
                if (!entry.isDebit()) { //credit
                    isIncrease = true;
                }
                break;
            case EXPENSE:
                if (entry.isDebit()) { //debit
                    isIncrease = true;
                }
                break;
            case REVENUE:
                if (!entry.isDebit()) { //credit
                    isIncrease = true;
                }
                break;
            case LIABILITY:
                if (!entry.isDebit()) {//credit
                    isIncrease = true;
                }
                break;
        }
        if (isIncrease) {
            runningBalance = runningBalance.add(entry.getAmount());
        } else {
            runningBalance = runningBalance.subtract(entry.getAmount());
        }
        runningBalanceMap.put(entry.getAccount().getId(), runningBalance);
        return runningBalance;
    }
}
