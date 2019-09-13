package io.smarthealth.accounting.account.service;

import org.springframework.scheduling.annotation.Async; 

/**
 *
 * @author Kelsas
 */

public interface JournalBalanceUpdateService {
      @Async
       void updateRunningBalance();
}
