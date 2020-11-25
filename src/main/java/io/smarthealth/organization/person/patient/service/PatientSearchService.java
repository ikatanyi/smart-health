///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package io.smarthealth.organization.person.patient.service;
//
//import io.smarthealth.organization.person.patient.domain.Patient;
//import java.util.List;
//import javax.annotation.PostConstruct;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.NoResultException;
//import org.apache.lucene.search.Query;
//import org.hibernate.search.jpa.FullTextEntityManager;
//import org.hibernate.search.jpa.Search;
//import org.hibernate.search.query.dsl.QueryBuilder;
//import org.springframework.stereotype.Service;
//
///**
// *
// * @author Kelsas
// */
//@Service
//public class PatientSearchService {
//
//    private final EntityManager entityManager;
//
//    public PatientSearchService(final EntityManagerFactory entityManagerFactory) {
//        this.entityManager = entityManagerFactory.createEntityManager();
//    }
//
//   @PostConstruct
//    public void initializeHibernateSearch() {
//
//        try {
//            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//            fullTextEntityManager.createIndexer().startAndWait();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }
//    
//    public List<Patient> patientSearch(String searchTerm) {
//        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
//        QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Patient.class).get();
//
//        Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(1).withPrefixLength(1).onFields("givenName", "middleName", "surname", "patientNumber")
//                .matching(searchTerm).createQuery();
//
//        javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, Patient.class);
//
//        // execute search
//        List<Patient> patientList = null;
//        try {
//            patientList = jpaQuery.getResultList();
//        } catch (NoResultException nre) {
//            ;// do nothing
//
//        }
//
//        return patientList;
//    }
//}
