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
public interface OrganisationRepository extends JpaRepository<Organisation, String> {

    Optional<Organisation> findByCode(String code);
    Optional<Organisation> findTopByOrderByOrganizationNameDesc();
}
