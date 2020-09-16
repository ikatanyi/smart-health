package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.data.BedTypeData;
import io.smarthealth.clinical.admission.domain.BedCharge;
import io.smarthealth.clinical.admission.domain.BedType;
import io.smarthealth.clinical.admission.domain.repository.BedTypeRepository;
import io.smarthealth.clinical.admission.domain.specification.BedTypeSpecification;
import io.smarthealth.infrastructure.exception.APIException;
import java.util.Optional;
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
public class BedTypeService {
    private final BedTypeRepository bedTypeRepository;
    private final BedChargeService chargeService;

    public BedType createBedType(BedTypeData data) {
        BedType bedType = data.map();
        BedCharge charge = chargeService.getBedCharge(data.getBedChargeId());
        bedType.setBedCharge(charge);
        if (fetchBedTypeByName(data.getName()).isPresent()) {
            throw APIException.conflict("BedType {0} already exists.", data.getName());
        }
        return bedTypeRepository.save(bedType);
    }

    public Page<BedType> fetchAllBedTypes(Pageable page) {
        return bedTypeRepository.findAll(page);
    }
    
    public Page<BedType> fetchBedTypes(String name, Boolean active,  String term, Pageable page) {        
        Specification<BedType>spec = BedTypeSpecification.createSpecification(name, active, term);
        return bedTypeRepository.findAll(spec, page);
    }

    public Optional<BedType> fetchBedTypeByName(String name){
        return bedTypeRepository.findByNameContainingIgnoreCase(name);
    }
    public BedType getBedType(Long id) {
        return bedTypeRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("BedType with id  {0} not found.", id));
    }
    

    public BedType updateBedType(Long id, BedTypeData data) {
        BedType bedType = getBedType(id);
        if (!bedType.getName().equals(data.getName())) {
            bedType.setName(data.getName());
        }
        BedCharge charge = chargeService.getBedCharge(data.getBedChargeId());
        bedType.setBedCharge(charge);
        bedType.setDescription(data.getDescription());
        bedType.setIsActive(data.getActive());
        bedType.setName(data.getName());
        return bedTypeRepository.save(bedType);
    }
}
