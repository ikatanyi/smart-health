package io.smarthealth.administration.codes.service;

import io.smarthealth.administration.codes.data.CodeData;
import io.smarthealth.administration.codes.data.CodeValueData;
import io.smarthealth.administration.codes.data.CodeValues;
import io.smarthealth.administration.codes.domain.Code;
import io.smarthealth.administration.codes.domain.CodeRepository;
import io.smarthealth.administration.codes.domain.CodeValue;
import io.smarthealth.administration.codes.domain.CodeValueRepository;
import io.smarthealth.infrastructure.exception.APIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */
@Service
public class CodesService {

    private final CodeRepository codeRepository;
    private final CodeValueRepository codeValueRepository;

    public CodesService(CodeRepository codeRepository, CodeValueRepository codeValueRepository) {
        this.codeRepository = codeRepository;
        this.codeValueRepository = codeValueRepository;
    }

    public Code getWithNotFoundDetection(Long id) {
        return codeRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Code with id  {0} not found.", id));
    }

    public Code getCodeByNameWithNotFoundDetection(String name) {
        return codeRepository.findOneByName(name)
                .orElseThrow(() -> APIException.notFound("Code with id  {0} not found.", name));
    }
    
    public CodeValue getCodeValueWithNotFoundDetection(Long id) {
        return codeValueRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Code Value with id  {0} not found.", id));
    }

    public CodeData createCode(CodeData data) {
        if (codeRepository.findOneByName(data.getName()).isPresent()) {
            throw APIException.conflict("Code with name {0} already exisits", data.getName());
        }
        Code code = new Code();
        code.setName(data.getName());
        code.setSystemDefined(data.isSystemDefined());
        Code saved = codeRepository.save(code);
        return CodeData.map(saved);
    }

    public CodeData getCode(Long id) {
        Code codes = getWithNotFoundDetection(id);
        return CodeData.map(codes);
    }

    public CodeValues getValuesByCode(Long id) {
        Code codes = getWithNotFoundDetection(id);
        CodeValues values = new CodeValues();
        values.setName(codes.getName());
        if (codes.getValues().size() > 0) {
            codes.getValues()
                    .stream()
                    .forEach(x -> {
                        values.getValues().add(CodeValueData.map(x));
                    });
        }
        return values;
    }
     public CodeValues getValuesByName(String name) {
        Code codes = getCodeByNameWithNotFoundDetection(name);
        CodeValues values = new CodeValues();
        values.setName(codes.getName());
        if (codes.getValues().size() > 0) {
            codes.getValues()
                    .stream()
                    .forEach(x -> {
                        values.getValues().add(CodeValueData.map(x));
                    });
        }
        return values;
    }

    public CodeData updateCode(Long id, CodeData data) {
        Code codes = getWithNotFoundDetection(id);

        if (codes.isSystemDefined()) {
            throw APIException.badRequest("This code is system defined and cannot be modified or deleted.");
        }
        codes.setName(data.getName());
        Code saved = codeRepository.save(codes);
        return CodeData.map(saved);
    }

    public Long deleteCode(Long id) {
        Code codes = getWithNotFoundDetection(id);

        if (codes.isSystemDefined()) {
            throw APIException.badRequest("This code is system defined and cannot be modified or deleted.");
        }
        codeRepository.delete(codes);
        return id;
    }

    public Page<CodeData> getCodes(Pageable page) {
        Page<CodeData> lists = codeRepository.findAll(page).map(c -> CodeData.map(c));
        return lists;
    }

    public CodeValueData createCodeValue(Long codeId, CodeValueData codeValueData) {
        Code code = getWithNotFoundDetection(codeId);

        CodeValue val = new CodeValue();
        val.setCode(code);
        val.setActive(codeValueData.isActive());
        val.setDescription(codeValueData.getDescription());
        val.setLabel(codeValueData.getName());
        val.setPosition(codeValueData.getPosition());
        val.setMandatory(codeValueData.isMandatory());

        CodeValue cv = codeValueRepository.save(val);

        return CodeValueData.map(cv);
    }

    public CodeValueData getCodeValue(Long codeId, Long codeValueId) {
        getWithNotFoundDetection(codeId);
        CodeValue cv = getCodeValueWithNotFoundDetection(codeValueId);
        return CodeValueData.map(cv);
    }

    public CodeValueData updateCodeValue(Long codeId, Long codeValueId, CodeValueData data) {
        getWithNotFoundDetection(codeId);
        CodeValue cv = getCodeValueWithNotFoundDetection(codeValueId);
        cv.setActive(data.isActive());
        cv.setMandatory(data.isMandatory());
        cv.setDescription(data.getDescription());
        cv.setLabel(data.getName());
        cv.setPosition(data.getPosition());
        CodeValue saved = codeValueRepository.save(cv);
        return CodeValueData.map(saved);
    }

    public Long deleteCodeValue(Long codeId, Long codeValueId) {
        getWithNotFoundDetection(codeId);
        CodeValue cv = getCodeValueWithNotFoundDetection(codeValueId);
        codeValueRepository.delete(cv);
        return codeValueId;
    }

    public Page<CodeValueData> getCodeValues(Pageable page) {
        Page<CodeValueData> lists = codeValueRepository.findAll(page).map(c -> CodeValueData.map(c));
        return lists;
    }

    public List<CodeValueData> getCodeValues(Long codeId) {
        Code code = getWithNotFoundDetection(codeId);
        List<CodeValueData> codes = codeValueRepository.findByCode(code)
                .stream()
                .map(cv -> CodeValueData.map(cv))
                .collect(Collectors.toList());
        return codes;
    }
}
