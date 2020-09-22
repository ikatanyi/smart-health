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

    

    
}
