package io.smarthealth.administration.codes.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long>, JpaSpecificationExecutor<Code> {

    Optional<Code> findOneByName(String name);
}
