package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.events.JournalEvent;
import io.smarthealth.accounting.billing.domain.PatientBill;
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
    public void onReceive(Object journalEvent) {
         if(journalEvent instanceof PatientBill){
             PatientBill bill=new PatientBill();
             service.createJournalEntry(bill);
         }
    }
}

//    @JmsListener(destination = "journalQueue", containerFactory = "connectionFactory")
//    public void receiveJournal(JournalEntry journalEntry) {
//        log.info(" >>  Received Journal: " + journalEntry);
//    }
//}
