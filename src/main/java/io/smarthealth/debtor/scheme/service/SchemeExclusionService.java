/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.debtor.scheme.service;

import io.smarthealth.debtor.payer.domain.SchemeRepository;
import io.smarthealth.debtor.scheme.data.SchemeExclusionData;
import io.smarthealth.debtor.scheme.domain.SchemeExclusionRepository;
import io.smarthealth.stock.item.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.smarthealth.debtor.payer.domain.Scheme;
import io.smarthealth.debtor.scheme.domain.SchemeExclusions;
import io.smarthealth.debtor.scheme.domain.specification.SchemeSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class SchemeExclusionService {

    private final SchemeExclusionRepository repository;
    private final SchemeRepository schemeRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public List<SchemeExclusions> create(List<SchemeExclusionData> data) {

        List<SchemeExclusions> list = data.stream()
                .map(x -> {
                    Scheme scheme = schemeRepository.findById(x.getSchemeId())
                            .orElseThrow(() -> APIException.notFound("Scheme with ID {0} Not Found", x.getSchemeId()));

                    Item item = itemRepository.findById(x.getItemId())
                            .orElseThrow(() -> APIException.notFound("Item with ID {0} Not Found", x.getItemId()));

                    SchemeExclusions exclusion = new SchemeExclusions();
                    exclusion.setScheme(scheme);
                    exclusion.setItem(item);
                    exclusion.setExclusionDate(LocalDateTime.now());
                    return exclusion;
                })
                .collect(Collectors.toList());

        return repository.saveAll(list);
    }

    @Transactional
    public SchemeExclusions update(Long id, SchemeExclusionData data) {
        SchemeExclusions toUpdate = get(id).orElseThrow(() -> APIException.notFound("Scheme Exclusion with ID {0} Not Found", id));

        Scheme scheme = schemeRepository.findById(data.getSchemeId())
                .orElseThrow(() -> APIException.notFound("Scheme with ID {0} Not Found", data.getSchemeId()));

        Item item = itemRepository.findById(data.getItemId())
                .orElseThrow(() -> APIException.notFound("Item with ID {0} Not Found", data.getItemId()));

        toUpdate.setScheme(scheme);
        toUpdate.setItem(item);
        return repository.save(toUpdate);
    }

    public Optional<SchemeExclusions> get(Long id) {
        return repository.findById(id);
    }

    public Page<SchemeExclusions> get(Long itemId, Long schemeId, Pageable page) {
        Specification<SchemeExclusions> spec = SchemeSpecification.createSchemeExclusionSpecification(itemId, schemeId);
        return repository.findAll(spec, page);
    }

    @Transactional
    public void delete(Long id) {
        SchemeExclusions toDelete = get(id).orElseThrow(() -> APIException.notFound("Scheme Exclusion with ID {0} Not Found", id));
        repository.delete(toDelete);
    }

    public Page<SchemeExclusions> getByScheme(Long itemId, Pageable page) {
        Scheme scheme = schemeRepository.findById(itemId).orElse(null);
        if (scheme == null) {
            return Page.empty(page);
        }
        return repository.findByScheme(scheme, page);
    }

    public Page<SchemeExclusions> getByScheme(Scheme scheme, Pageable page) {
        if (scheme == null) {
            return Page.empty(page);
        }
        return repository.findByScheme(scheme, page);
    }

}
