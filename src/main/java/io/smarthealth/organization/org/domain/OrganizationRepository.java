package io.smarthealth.organization.org.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 * @param <T> Entity
 */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Optional<Organization> findByCode(String code);
    Optional<Organization> findTopByOrderByOrganizationNameDesc();
}
