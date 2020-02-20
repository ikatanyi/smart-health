package io.smarthealth.infrastructure.sequence.domain;

import io.smarthealth.infrastructure.sequence.SequenceType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Deprecated
@Repository
public interface SequenceRepository extends CrudRepository<SequenceFormat, Long>{
     Optional<SequenceFormat> findByIdType(SequenceType type);

//     @Procedure(procedureName = "nextVal")
//     Long nextSequence(@Param("seq_name") String sequenceName, @Param("company_id") String companyId);
      
     @Query(value = "SELECT nextVal(:seq_name, :company_id) as next_sequence", nativeQuery = true)
     Long nextSequence(@Param("seq_name") String sequenceName, @Param("company_id") String companyId);
}
