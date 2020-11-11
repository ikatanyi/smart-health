/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.person.patient.domain.impl;

import io.smarthealth.organization.person.patient.domain.CustomizedPatientRepository;
import io.smarthealth.organization.person.patient.domain.Patient;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

/**
 *
 * @author Kelsas
 */
public class CustomizedPatientRepositoryImpl implements CustomizedPatientRepository {

    @PersistenceContext
    private EntityManager em;
//hibernate-search lucene 
    @Override
    public List<Patient> search(String terms, int limit, int offset) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Patient.class).get();

        org.apache.lucene.search.Query luceneQuery = queryBuilder
                .keyword()
                .onFields("givenName", "middleName", "surname", "patientNumber")
                .matching(terms)
                .createQuery(); 

        // wrap Lucene query in a javax.persistence.Query
        javax.persistence.Query jpaQuery
                = fullTextEntityManager.createFullTextQuery(luceneQuery, Patient.class);
        jpaQuery.setMaxResults(limit);
        jpaQuery.setFirstResult(offset);
        // execute search
        return jpaQuery.getResultList();
    }

}
