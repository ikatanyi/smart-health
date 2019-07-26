package io.smarthealth.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
    
}
