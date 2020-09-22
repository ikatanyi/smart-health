package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.BedChargeData;
import io.smarthealth.clinical.admission.domain.Bed;
import io.smarthealth.clinical.admission.domain.BedCharge;
import io.smarthealth.clinical.admission.domain.BedType;
import io.smarthealth.clinical.admission.domain.repository.BedChargeRepository;
import io.smarthealth.clinical.admission.domain.repository.BedTypeRepository;
import io.smarthealth.clinical.admission.domain.specification.BedChargeSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class BedChargeService {

    private final BedChargeRepository bedChargeRepository;
    private final ItemService itemService;
    private final BedTypeService bedTypeService;

    public BedCharge createBedCharge(BedChargeData data) {
        BedCharge bedCharge = data.map();
        BedType bedType = bedTypeService.getBedType(data.getBedTypeId());
        Item item = itemService.findItemEntityOrThrow(data.getItemId());
        bedCharge.setItem(item);
        bedCharge.setBedType(bedType);
        return bedChargeRepository.save(bedCharge);
    }

    public List<BedCharge> createBatchBedCharge(List<BedChargeData> list) {
        List<BedCharge> bedCharges = new ArrayList();
        list.stream().map((data) -> {
            BedCharge bedCharge = data.map();
            BedType bedType = bedTypeService.getBedType(data.getBedTypeId());
            Item item = itemService.findItemEntityOrThrow(data.getItemId());
            bedCharge.setItem(item);
            bedCharge.setBedType(bedType);
            return bedCharge;
        }).forEachOrdered((bedCharge) -> {
            bedCharges.add(bedCharge);
        });
        return bedChargeRepository.saveAll(bedCharges);
    }

    public Page<BedCharge> fetchAllBedCharges(Long bedTypeId, String itemName, Pageable page) {
        Specification<BedCharge> spec = BedChargeSpecification.createSpecification(bedTypeId, itemName);
        return bedChargeRepository.findAll(spec, page);
    }

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
