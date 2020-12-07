package io.smarthealth.clinical.theatre.service;

import io.smarthealth.clinical.theatre.domain.enumeration.FeeCategory;
import io.smarthealth.clinical.theatre.data.TheatreFeeData;
import io.smarthealth.clinical.theatre.domain.TheatreFee;
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
import io.smarthealth.clinical.theatre.domain.TheatreFeeRepository;
import io.smarthealth.clinical.theatre.domain.specification.TheatreFeeSpecification;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class TheatreFeeService {

    private final TheatreFeeRepository repository;
    private final ItemRepository itemRepository;

    @Transactional
    public TheatreFee createConfiguration(TheatreFeeData data) {
        Item theatrefee = itemRepository.findById(data.getItemId())
                .orElseThrow(() -> APIException.notFound("Theatre Fee with ID {0} Not Found", data.getItemId())); 
        TheatreFee config = new TheatreFee();
        config.setIsPercentage(data.isPercentage());
        config.setAmount(data.getValue());
        config.setServiceType(theatrefee);
        config.setFeeCategory(data.getFeeCategory());

        return repository.save(config);
    }

    @Transactional
    public TheatreFee updateConfiguration(Long id, TheatreFeeData data) {
        TheatreFee toUpdate = get(id).orElseThrow(() -> APIException.notFound("Theatre Fee with ID {0} Not Found", id));
        toUpdate.setIsPercentage(data.isPercentage());
        toUpdate.setAmount(data.getValue());
        return repository.save(toUpdate);
    }

    public Optional<TheatreFee> get(Long id) {
        return repository.findById(id);
    }

    public Page<TheatreFee> getProcedures(String itemCode, String term, Boolean isPercentage, BigDecimal valueAmount, FeeCategory feeCategory, Pageable page) {
        Specification<TheatreFee> spec = TheatreFeeSpecification.createConfigSpecification(itemCode, term, isPercentage, valueAmount, feeCategory);
        return repository.findAll(spec, page);
    }

    @Transactional
    public void delete(Long id) {
        TheatreFee toDelete = get(id).orElseThrow(() -> APIException.notFound("Procedure Configuration with ID {0} Not Found", id));
        repository.delete(toDelete);
    }

    public List<TheatreFee> getByItem(Long itemId) {
        return repository.findConfigByItem(itemId);
    }

    public List<TheatreFee> getByItem(Item item) {
        return repository.findByServiceType(item);
    }

    public Optional<TheatreFee> findByItemAndCategory(Item item, FeeCategory category) {
        return repository.findByServiceTypeAndFeeCategory(item, category);
    }
}
