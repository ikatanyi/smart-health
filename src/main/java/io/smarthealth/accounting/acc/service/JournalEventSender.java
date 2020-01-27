package io.smarthealth.accounting.acc.service;

import io.smarthealth.accounting.acc.events.JournalEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class JournalEventSender {

    @Autowired
    private JmsTemplate jmsTemplate;

//    public void postJournal(JournalEntry journalEntry) {
//        jmsTemplate.convertAndSend("journalQueue", journalEntry);
//    }
    public void process(JournalEvent journalevent) {
        jmsTemplate.convertAndSend("journalQueue", journalevent);
    }

    public void post(Object journal) {
        jmsTemplate.convertAndSend("journalQueue", journal);
    }
}
