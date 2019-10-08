/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.data.UomData;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.domain.UomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kelsas
 */
@Service
public class UomService {

    @Autowired
    UomRepository uomRepository;

    @Autowired
    ModelMapper modelMapper;

    public Uom fetchUomById(final Long uomId) {
        try {
            return uomRepository.findById(uomId).orElseThrow(() -> APIException.notFound("Uom identified by  {0} not found", uomId));
        } catch (Exception e) {
            e.printStackTrace();
            throw APIException.internalError("Uom identified by " + uomId + " not found", e.getMessage());
        }
    }

    @Transactional
    public Uom createUom(final UomData uomData) {
        Uom uom = modelMapper.map(uomData, Uom.class);
        uomRepository.save(uom);
        return uom;
    }

    @Transactional
    public Uom updateUom(final Long uomId, final UomData uomData) {
        Uom uom = uomRepository.findById(uomId).orElseThrow(() -> APIException.notFound("Uom identified by  {0} not found", uomId));
        modelMapper.map(uomData, uom);
        uomRepository.save(uom);
        return uom;
    }

    public Page<Uom> fetchAllUom(final Pageable pgbl) {
        return uomRepository.findAll(pgbl);
    }

}
