package io.smarthealth.inpatient.setup.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.inpatient.setup.data.WardData;
import io.smarthealth.inpatient.setup.domain.Ward;
import io.smarthealth.inpatient.setup.domain.WardRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class WardService {

    private final WardRepository repository;
    //create

    public Ward createWard(WardData data) {
        Ward ward = new Ward();
        ward.setActive(Boolean.TRUE);
        ward.setDescription(data.getDescription());
        ward.setName(data.getName());
        return repository.save(ward);
    }

    public Optional<Ward> getWard(Long id) {
        return repository.findById(id);
    }

    public Ward getWardOrThrow(Long id) {
        return getWard(id)
                .orElseThrow(() -> APIException.notFound("Ward with ID {0} Not Found", id));
    }

    public Page<Ward> getWards(Pageable page) {
        return repository.findAll(page);
    }

    public Ward updateWard(Long id, WardData data) {
        Ward ward = getWardOrThrow(id);
        ward.setActive(Boolean.TRUE);
        ward.setDescription(data.getDescription());
        ward.setName(data.getName());
        return repository.save(ward);
    }

    public void deleteWard(Long id) {
        Ward ward = getWardOrThrow(id);
        repository.delete(ward);
    }
}
