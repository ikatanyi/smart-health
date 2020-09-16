package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.BedChargeData;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.BedCharge;
import io.smarthealth.clinical.admission.domain.repository.BedChargeRepository;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class BedChargeService {

    private final BedChargeRepository bedChargeRepository;
    private final ItemService itemService;

    public BedCharge createBedCharge(BedChargeData data) {
        BedCharge bedCharge = data.map();
        Item item = itemService.findItemEntityOrThrow(data.getItemId());
        bedCharge.setItem(item);
        return bedChargeRepository.save(bedCharge);
    }

    public Page<BedCharge> fetchAllBedCharges(Pageable page) {
        return bedChargeRepository.findAll(page);
    }

//    public Optional<Bed> fetchBedByName(String name){
//        return bedChargeRepository.findByNameContainingIgnoreCase(name);
//    }
    public BedCharge getBedCharge(Long id) {
        return bedChargeRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("BedCharge with id  {0} not found.", id));
    }

    public BedCharge updateBedCharge(Long id, BedChargeData data) {
        BedCharge bedCharge = getBedCharge(id);
        bedCharge.setActive(data.getActive());
        bedCharge.setRate(data.getRate());
        Item item = itemService.findItemEntityOrThrow(data.getItemId());
        bedCharge.setItem(item);
        return bedChargeRepository.save(bedCharge);
    }
}
