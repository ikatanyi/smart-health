/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.inpatient.setup.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.inpatient.setup.data.BedChargeData;
import io.smarthealth.inpatient.setup.data.BedData;
import io.smarthealth.inpatient.setup.domain.Bed;
import io.smarthealth.inpatient.setup.domain.BedCharge;
import io.smarthealth.inpatient.setup.domain.BedRepository;
import io.smarthealth.inpatient.setup.domain.Room;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class BedService {

    private final BedRepository repository;
    private final RoomService roomService;
    private final ItemService itemService;

    public Bed createBed(BedData data) {
        Room room = roomService.getRoomOrThrow(data.getRoomId());
        Bed bed = new Bed();
        bed.setActive(Boolean.TRUE);
        bed.setDescription(data.getDescription());
        bed.setName(data.getName());
        bed.setRoom(room);
        bed.setStatus(data.getStatus());
        if (!data.getBedCharges().isEmpty()) {
            bed.addCharges(
                    data.getBedCharges().stream().map(x -> createBedCharge(x)).collect(Collectors.toList())
            );
        }

        return repository.save(bed);
    }

    public Optional<Bed> getBed(Long id) {
        return repository.findById(id);
    }

    public Bed getBedOrThrow(Long id) {
        return getBed(id)
                .orElseThrow(() -> APIException.notFound("Bed with ID {0} Not Found", id));
    }

    public Page<Bed> getBeds(Pageable page) {
        return repository.findAll(page);
    }

    public Bed updateBed(Long id, BedData data) {
        Bed bed = getBedOrThrow(id);
        bed.setActive(Boolean.TRUE);
        bed.setDescription(data.getDescription());
        bed.setName(data.getName());
        bed.setStatus(data.getStatus());
        return repository.save(bed);
    } 
    
    public Bed save(Bed bed){
        return repository.save(bed);
    }

    private BedCharge createBedCharge(BedChargeData data) {
        Item item = itemService.findByItemCodeOrThrow(data.getItemCode());

        BedCharge charge = new BedCharge();
        charge.setActive(Boolean.TRUE);
        charge.setItem(item);
        charge.setRate(data.getRate());

        return charge;
    }

    public void deleteBed(Long id) {
        Bed bed = getBedOrThrow(id);
        repository.delete(bed);
    }
}
