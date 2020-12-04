/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.procedure.service;

import io.smarthealth.clinical.procedure.domain.ProcedureConfigurationRepository;
import io.smarthealth.clinical.procedure.domain.ProcedureConfiguration;
import io.smarthealth.clinical.procedure.data.ProcedureConfigurationData;
import io.smarthealth.clinical.procedure.domain.enumeration.FeeCategory;
import io.smarthealth.clinical.procedure.domain.specification.ProcedureSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.ItemRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class ProcedureConfigurationService {

    private final ProcedureConfigurationRepository repository;
    private final ItemRepository itemRepository;

    @Transactional
    public ProcedureConfiguration createConfiguration(ProcedureConfigurationData data) {
        Item procedure = itemRepository.findById(data.getItemId())
                .orElseThrow(() -> APIException.notFound("Procedure with ID {0} Not Found", data.getItemId()));
        ProcedureConfiguration config = new ProcedureConfiguration(data.isPercentage(), data.getValue(), procedure, data.getFeeCategory());

        return repository.save(config);
    }

    @Transactional
    public ProcedureConfiguration updateConfiguration(Long id, ProcedureConfigurationData data) {
        ProcedureConfiguration toUpdate = get(id).orElseThrow(() -> APIException.notFound("Procedure Configuration with ID {0} Not Found", id));
        toUpdate.setPercentage(data.isPercentage());
        toUpdate.setValueAmount(data.getActualAmount());
        return repository.save(toUpdate);
    }

    public Optional<ProcedureConfiguration> get(Long id) {
        return repository.findById(id);
    }

    public Page<ProcedureConfiguration> getProcedures(String itemCode, String term, Boolean isPercentage, BigDecimal valueAmount, FeeCategory feeCategory, Pageable page) {
        Specification<ProcedureConfiguration> spec = ProcedureSpecification.createConfigSpecification(itemCode, term, isPercentage, valueAmount, feeCategory);
        return repository.findAll(spec, page);
    }

    @Transactional
    public void delete(Long id) {
        ProcedureConfiguration toDelete = get(id).orElseThrow(() -> APIException.notFound("Procedure Configuration with ID {0} Not Found", id));
        repository.delete(toDelete);
    }

    public List<ProcedureConfiguration> getByItem(Long itemId) {
        return repository.findConfigByItem(itemId);
    }
}
