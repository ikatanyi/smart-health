package io.smarthealth.security.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
    
    @Modifying
    @Query("UPDATE User u SET u.lastLogin=CURRENT_TIMESTAMP where u.username=:username OR u.email =:username")
    int updateLastLogin(@Param("username") String username);
    
}
