package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.data.v1.JournalEntry;
import io.smarthealth.administration.servicepoint.service.ServicePointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Component
public class JournalReceiver {

    private FinancialActivityAccountService financialActivityAccountService;
    private JournalEntryService journalService;
    private ServicePointService pointsService;

    @JmsListener(destination = "journalQueue", containerFactory = "connectionFactory")
    public void receiveJournal(JournalEntry journalEntry) {
        log.info(" >>  Received Journal: " + journalEntry);
    }
}
