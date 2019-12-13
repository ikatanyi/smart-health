package io.smarthealth.infrastructure.sequence.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.domain.SequenceData;
import io.smarthealth.infrastructure.sequence.domain.SequenceDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */
@Service
@Slf4j
public class SequenceManagerService {

    private final SequenceDataRepository repository;
    private final String companyId = "1";

    public SequenceManagerService(SequenceDataRepository repository) {
        this.repository = repository;
    }

    public void initSequences() {
        List<SequenceType> initList = new ArrayList<>();

        for (SequenceType type : SequenceType.values()) {
            if (!repository.findBySequenceName(type.getSequenceName()).isPresent()) {
                initList.add(type);
            }
        }
        List<SequenceData> seqsData = initList.stream()
                .map(seq -> new SequenceData(seq.getSequenceName(), companyId))
                .collect(Collectors.toList());
        log.info("initializing Smarthealth sequences ...");
        repository.saveAll(seqsData);

    }

    public Page<SequenceData> findAllSequences(Pageable page) {
        return repository.findAll(page);
    }

    public Optional<SequenceData> getSequence(String name) {
        return repository.findById(name);
    }

    public SequenceData findOneWithNoFoundDetection(String sequenceName) {
        return getSequence(sequenceName)
                .orElseThrow(() -> APIException.notFound("Sequence with name {0} not found", sequenceName));
    }

    public SequenceData updateSequenceData(String sequenceName, SequenceData data) {
        SequenceData seq = findOneWithNoFoundDetection(sequenceName);
        if (!Objects.equals(seq.getSequenceIncrement(), data.getSequenceIncrement())) {
            seq.setSequenceIncrement(data.getSequenceIncrement());
        }

        if (!Objects.equals(seq.getSequenceCurValue(), data.getSequenceCurValue())) {
            seq.setSequenceCurValue(data.getSequenceCurValue());
        }
        SequenceData sd = repository.save(seq);
        return sd;
    }
}
