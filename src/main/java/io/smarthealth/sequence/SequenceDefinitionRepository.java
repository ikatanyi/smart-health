package io.smarthealth.sequence;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

/**
 * Sequence Definition Repository.
 */
//@RepositoryRestResource(collectionResourceRel = "definitions", path = "definitions")
interface SequenceDefinitionRepository extends CrudRepository<SequenceDefinition, Long> {
//    Iterable<SequenceDefinition> findAllByTenantId(Long tenant);

    List<SequenceDefinition> findAllByTenantId(Long tenant);
    
    SequenceDefinition findByNameAndTenantId(String name, Long tenant);
}
