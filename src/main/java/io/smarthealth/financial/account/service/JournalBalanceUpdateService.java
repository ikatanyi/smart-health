package io.smarthealth.financial.account.service;

import org.springframework.scheduling.annotation.Async; 

/**
 *
 * @author Kelsas
 */

public interface JournalBalanceUpdateService {
      @Async
       void updateRunningBalance();
}
