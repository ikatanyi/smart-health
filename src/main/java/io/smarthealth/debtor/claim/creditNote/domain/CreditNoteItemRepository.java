/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.claim.creditNote.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kennedy.Imbenzi
 */
public interface CreditNoteItemRepository extends JpaRepository<CreditNoteItem, Long>{
    
}
