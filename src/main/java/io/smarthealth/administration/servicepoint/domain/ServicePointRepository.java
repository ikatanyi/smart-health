package io.smarthealth.administration.servicepoint.domain;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface ServicePointRepository extends JpaRepository<ServicePoint, Long>{
    
}
