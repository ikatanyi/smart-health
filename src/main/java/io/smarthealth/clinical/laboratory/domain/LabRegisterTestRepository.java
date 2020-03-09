package io.smarthealth.clinical.laboratory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface LabRegisterTestRepository extends JpaRepository<LabRegisterTest, Long>{
    @Modifying
    @Query("UPDATE LabRegisterTest t SET t.collected=true, t.collectionDateTime=CURRENT_TIMESTAMP, t.collectedBy=:collectedBy, t.specimen=:specimen WHERE t.id=:id") 
    int updateTestCollected(@Param("collectedBy") String collectedBy,@Param("specimen") String specimen, @Param("id") Long id);
    
   @Modifying
    @Query("UPDATE LabRegisterTest t SET t.entered=true, t.entryDateTime=CURRENT_TIMESTAMP, t.enteredBy=:enteredBy WHERE t.id=:id") 
    int updateTestEntry(@Param("enteredBy") String enteredBy, @Param("id") Long id);
    
    @Modifying
    @Query("UPDATE LabRegisterTest t SET t.validated=true, t.validationDateTime=CURRENT_TIMESTAMP, t.validatedBy=:validatedBy WHERE t.id=:id") 
    int updateTestValidation(@Param("validatedBy") String validatedBy, @Param("id") Long id);
    
    @Modifying
    @Query("UPDATE LabRegisterTest t SET t.paid=true WHERE t.id=:id") 
    int updateTestPaid(@Param("id") Long id);
    
}
