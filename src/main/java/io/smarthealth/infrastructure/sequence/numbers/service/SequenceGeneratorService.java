package io.smarthealth.infrastructure.sequence.numbers.service;

import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import io.smarthealth.infrastructure.sequence.numbers.data.SequenceNumberFormatData;
import io.smarthealth.infrastructure.sequence.numbers.domain.EntitySequenceType;
import io.smarthealth.infrastructure.sequence.numbers.domain.SequenceNumberFormat;
import io.smarthealth.infrastructure.sequence.numbers.domain.SequenceNumberFormatRepository;

/**
 *
 * @author Kelsas
 */
@Deprecated
@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final SequenceNumberFormatRepository repository;

    @Transactional
    public SequenceNumberFormat createSequenceFormat(SequenceNumberFormatData data) {
        SequenceNumberFormat formart = new SequenceNumberFormat();
        formart.setSequenceType(data.getSequenceType());
        formart.setPrefix(data.getPrefix());
        formart.setSuffix(data.getSuffix());
        formart.setMaxLength(data.getMaxLength());

        return save(formart);
    }

    @Transactional
    public SequenceNumberFormat save(SequenceNumberFormat service) {
        return repository.save(service);
    }

    public Optional<SequenceNumberFormat> getSequenceFormat(Long id) {
        return repository.findById(id);
    }

    public Optional<SequenceNumberFormat> getSequenceFormatByType(EntitySequenceType type) {
        return repository.findBySequenceType(type);
    }

    public SequenceNumberFormat getSequenceFormatByTypeOrThrow(EntitySequenceType type) {
        return repository.findBySequenceType(type)
                .orElseThrow(() -> APIException.notFound("Sequence not Found "));
    }

    public SequenceNumberFormat getSequenceFormatOrThrow(Long id) {
        return getSequenceFormat(id)
                .orElseThrow(() -> APIException.notFound("Sequence {0} not Found", id));
    }

    public Page<SequenceNumberFormat> getSequenceFormats(Integer type, Pageable page) {
        if (type != null) {
            return repository.findBySequenceType(EntitySequenceType.fromInt(type), page);
        }
        return repository.findAll(page);
    }

    public Page<SequenceNumberFormat> getSequenceFormats(Pageable page) {
        return repository.findAll(page);
    }

    public List<SequenceNumberFormatData> getSequenceFormatList() {
        return repository.findAll()
                .stream()
                .map(srv -> srv.toData())
                .collect(Collectors.toList());
    }
    //
}
