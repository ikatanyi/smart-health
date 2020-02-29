package io.smarthealth.administration.codes.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeValueRepository extends JpaRepository<CodeValue, Long> {

    List<CodeValue> findByCode(Code code);

    List<CodeValue> findByCodeAndIsActive(Code code, boolean active);
}
