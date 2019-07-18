package io.smarthealth.infrastructure.repository;

import io.smarthealth.infrastructure.domain.Identifiable;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 *
 * @author Kelsas
 */
@NoRepositoryBean
public interface UuidRepository<T extends Identifiable> extends JpaRepository<T, Long> {

    Optional<T> findByUuid(String uuid);
}
