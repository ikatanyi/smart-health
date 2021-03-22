package io.smarthealth.administration.codes.service;

import io.smarthealth.administration.codes.data.CodeValueData;
import io.smarthealth.administration.codes.domain.Code;
import io.smarthealth.administration.codes.domain.CodeValue;
import io.smarthealth.administration.codes.domain.CodeValueRepository;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.List;
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
public class CodeService {

    private final CodeValueRepository repository;

    public CodeValue createCodeValue(CodeValueData data) {
        CodeValue codeValue = new CodeValue();
        codeValue.setActive(Boolean.TRUE);
        codeValue.setCode(data.getCode());
        codeValue.setCodeValue(data.getCodeValue());
        codeValue.setPosition(data.getPosition());
        return repository.save(codeValue);
    }

    public CodeValue getCodeValueById(Long id) {
        return getCodeValue(id)
                .orElseThrow(() -> APIException.notFound("Code Value with Id {0} not Found", id));
    }

    public Optional<CodeValue> getCodeValue(Long id) {
        return repository.findById(id);
    }

    public List<CodeValue> getCodeValues(Code code) {
        if (code == null) {
            return repository.findAll();
        }
        return repository.findByCode(code);
    }

    public List<CodeValue> getCodeValuesAndValueLike(Code code, String value) {
        if (code == null) {
            return repository.findAll();
        }
        if(value==null){
            return  repository.findByCode(code);
        }
        return repository.findByCodeAndCodeValueContainingIgnoreCase(code, value);
    }

    public List<CodeValue> getCodeValues(Code code, boolean active) {
        return repository.findByCodeAndIsActive(code, active);
    }

    public List<CodeValue> getCodeValues() {
        return repository.findAll();
    }

    public Page<CodeValue> getCodeValues(Pageable page) {
        return repository.findAll(page);
    }

    public CodeValue updateCodeValue(Long id, CodeValueData data) {
        CodeValue cValue = getCodeValueById(id);
        cValue.setActive(data.isActive());
        cValue.setCode(data.getCode());
        cValue.setCodeValue(data.getCodeValue());
        cValue.setPosition(data.getPosition());
        return repository.save(cValue);
    }

    public void deleteCodeValue(Long id) {
        CodeValue cValue = getCodeValueById(id);
        repository.delete(cValue);
    }
}
