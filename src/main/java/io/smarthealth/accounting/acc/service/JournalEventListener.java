package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.events.JournalEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Kelsas
 */
@Slf4j
@Component
public class JournalEventListener {

    private final JournalEntryService service;

    public JournalEventListener(JournalEntryService service) {
        this.service = service;
    }

    @JmsListener(destination = "journalQueue", containerFactory = "connectionFactory")
    public void on(Object msg) {
        log.info(" >>  Received Journal: " + msg);
        if (msg instanceof JournalEvent) {
            JournalEvent event = (JournalEvent) msg;
            service.bookJournalEntry(event.getTransactionIdentifier());
        }
    }

//    @JmsListener(destination = "journalQueue", containerFactory = "connectionFactory")
//    public void receiveJournal(JournalEntry journalEntry) {
//        log.info(" >>  Received Journal: " + journalEntry);
//    }
}
