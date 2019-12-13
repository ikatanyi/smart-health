package io.smarthealth.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 *
 * @author Kelsas
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String roleName);

//    @Query("SELECT COUNT(a) FROM USER a JOIN a.roles r WHERE r.id = :roleId AND a.enabled = true")
//    Integer getCountOfRolesAssociatedWithUsers(@Param("roleId") Long roleId);

}
