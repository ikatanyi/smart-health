package io.smarthealth.infrastructure.codes.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodeRepository extends JpaRepository<Code, Long>, JpaSpecificationExecutor<Code> {

    Optional<Code> findOneByName(String name);
}
