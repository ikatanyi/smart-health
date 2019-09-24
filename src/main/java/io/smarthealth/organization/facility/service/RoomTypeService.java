/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.organization.facility.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.organization.facility.data.RoomTypeData;
import io.smarthealth.organization.facility.domain.RoomType;
import io.smarthealth.organization.facility.domain.RoomTypeRepository;
import java.util.Optional;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author simz
 */
@Data
@Service
public class RoomTypeService {

    @Autowired
    RoomTypeRepository roomTypeRepository;

    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public RoomType createRoomType(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    public Page<RoomType> fetchAllRoomTypes(Pageable pageable) {
        return roomTypeRepository.findAll(pageable);
    }

    public Optional<RoomType> fetchRoomTypeByCode(String code) {
        return roomTypeRepository.findByTypeCode(code)/*.orElseThrow(()-> APIException.notFound("Room type identified by code {0} not found", code))*/;
    }

    public RoomTypeData convertRoomTypeEntityToData(RoomType roomType) {
        return modelMapper.map(roomType, RoomTypeData.class);
    }

    public RoomType convertRoomTypeDataToEntity(RoomTypeData roomTypeData) {
        return modelMapper.map(roomTypeData, RoomType.class);
    }

}
