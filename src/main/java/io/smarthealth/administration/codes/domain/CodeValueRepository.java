package io.smarthealth.administration.codes.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodeValueRepository extends JpaRepository<CodeValue, Long>, JpaSpecificationExecutor<CodeValue> {

    Optional<CodeValue> findByCodeNameAndId(String codeName, Long id);

    Optional<CodeValue> findByCodeNameAndLabel(String codeName, String label);

    List<CodeValue> findByCode(Code code);
}
