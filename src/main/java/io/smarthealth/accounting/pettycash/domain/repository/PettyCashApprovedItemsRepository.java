/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.pettycash.domain.repository;

import io.smarthealth.accounting.pettycash.domain.PettyCashApprovedItems;
import io.smarthealth.accounting.pettycash.domain.PettyCashRequestItems;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Simon.waweru
 */
public interface PettyCashApprovedItemsRepository extends JpaRepository<PettyCashApprovedItems, Long> {

//    Optional<PettyCashApprovedItems> findByApprovedByAndItemNo(final Employee employee, final PettyCashRequestItems itemNo);
    List<PettyCashApprovedItems> findByItemNo(final PettyCashRequestItems itemNo);

//    @Query("SELECT a FROM PettyCashApprovals a WHERE a.itemNo.requestNo=:requestNo")
//    List<PettyCashApprovedItems> fetchPettyCashApprovalsByRequestNo(final PettyCashRequests request);
    @Modifying
    @Query("UPDATE PettyCashRequestItems p SET p.paid=true where p.id=:id")
    int updateItemPaid(@Param("id") Long id);
}
