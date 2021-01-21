/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.domain.impl;

import io.smarthealth.accounting.accounts.data.financial.statement.TransactionList;
import io.smarthealth.accounting.accounts.domain.AccountType;
import io.smarthealth.accounting.accounts.domain.JournalEntryItem;
import io.smarthealth.infrastructure.lang.DateRange;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import io.smarthealth.accounting.accounts.domain.AccountlBalanceRepository;
import java.util.Arrays;
import javax.persistence.criteria.Expression;

/**
 *
 * @author Kelsas
 */
@Repository
@Transactional(readOnly = true)
public class AccountlBalanceRepositoryImpl implements AccountlBalanceRepository {

    EntityManager em;

    public AccountlBalanceRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public BigDecimal getAccountsBalance(String accountNumber, DateRange period) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<JournalEntryItem> journalItem = query.from(JournalEntryItem.class);
//        query.select(
//                cb.sum(
//                        cb.diff(journalItem.get("debit"), journalItem.get("credit"))
//                )
//        );
 
        Expression<BigDecimal> caseExpr = cb.selectCase()
                .when(
                        cb.or(
                                cb.equal(journalItem.get("account").get("type"), AccountType.ASSET),
                                cb.equal(journalItem.get("account").get("type"), AccountType.EXPENSE)
                        ), cb.diff(journalItem.get("debit"), journalItem.get("credit"))
                ).otherwise(
                        cb.diff(journalItem.get("credit"), journalItem.get("debit"))
                ).as(BigDecimal.class);
        
        query.select( cb.sum(caseExpr));
        
        List<Predicate> predicates = new ArrayList<>();
        if (accountNumber != null) {
            predicates.add(cb.equal(journalItem.get("account").get("identifier"), accountNumber));
        }
        if (period != null) {
            predicates.add(
                    cb.between(journalItem.get("journalEntry").get("date"), period.getStartDate(), period.getEndDate())
            );
        }
        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<BigDecimal> typedQuery = em.createQuery(query);
        BigDecimal sum = typedQuery.getSingleResult();
        return sum;
    }

    @Override
    public BigDecimal getAccountsBalance(String accountNumber, LocalDate date) {
        LocalDate period = date == null ? LocalDate.now() : date;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<JournalEntryItem> journalItem = query.from(JournalEntryItem.class); 
        Expression<BigDecimal> caseExpr = cb.selectCase()
                .when(
                        cb.or(
                                cb.equal(journalItem.get("account").get("type"), AccountType.ASSET),
                                cb.equal(journalItem.get("account").get("type"), AccountType.EXPENSE)
                        ), cb.diff(journalItem.get("debit"), journalItem.get("credit"))
                ).otherwise(
                        cb.diff(journalItem.get("credit"), journalItem.get("debit"))
                ).as(BigDecimal.class);
        
        query.select( cb.sum(caseExpr)); 
        List<Predicate> predicates = new ArrayList<>();
        if (accountNumber != null) {
            predicates.add(cb.equal(journalItem.get("account").get("identifier"), accountNumber));
        }
        if (period != null) {
            predicates.add(cb.lessThanOrEqualTo(journalItem.get("journalEntry").get("date"), period));
        }
        query.where(predicates.toArray(new Predicate[0]));

        TypedQuery<BigDecimal> typedQuery = em.createQuery(query);
        BigDecimal sum = typedQuery.getSingleResult(); 
        return sum;
    }

    public List<AccountType> getAccountType() {
        return Arrays.asList(AccountType.REVENUE, AccountType.LIABILITY);
    }

}
