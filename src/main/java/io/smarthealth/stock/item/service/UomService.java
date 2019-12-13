/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.item.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.data.UomData;
import io.smarthealth.stock.item.data.Uoms;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.domain.UomRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return uomRepository.findById(uomId).orElseThrow(() -> APIException.notFound("Uom identified by  {0} not found", uomId));
    }
    public Optional<Uom> findUomById(Long id){
        return uomRepository.findById(id);
    }
    public Uom getUomWithNoFoundDetection(Long id){
        return uomRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Uom identified by  {0} not found", id));
    }

    @Transactional
    public Uom createUom(final UomData uomData) {
        Uom uom = modelMapper.map(uomData, Uom.class);
        uomRepository.save(uom);
        return uom;
    }

    @Transactional
    public Uom updateUom(final Long uomId, final UomData uomData) {
        Uom uom = getUomWithNoFoundDetection(uomId);
        modelMapper.map(uomData, uom);
        uomRepository.save(uom);
        return uom;
    }

    public Page<Uom> fetchAllUom(final Pageable pgbl) {
        return uomRepository.findAll(pgbl);
    }
   public List<Uoms> getAllUnitofMeasure(){
        Page<Uoms> list=uomRepository.findAll(Pageable.unpaged()).map(um -> Uoms.map(um));
        return list.getContent();
   }
}
