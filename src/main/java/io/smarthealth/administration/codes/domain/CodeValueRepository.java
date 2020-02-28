package io.smarthealth.administration.codes.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CodeValueRepository extends JpaRepository<CodeValue, Long>, JpaSpecificationExecutor<CodeValue> {

    Optional<CodeValue> findByCodeNameAndId(String codeName, Long id);

    Page<CodeValue> findByCodeName(final String codeName, final Pageable pageable);

    Optional<CodeValue> findByCodeNameAndLabel(String codeName, String label);

    List<CodeValue> findByCode(Code code);
}
