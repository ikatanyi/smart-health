package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.security.util.SecurityUtils;
import io.smarthealth.sequence.SequenceNumberService;
import io.smarthealth.sequence.Sequences;
import io.smarthealth.stock.inventory.data.RequisitionData;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.domain.RequisitionItem;
import io.smarthealth.stock.inventory.domain.RequisitionRepository;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import io.smarthealth.stock.inventory.domain.specification.RequisitionSpecification;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Kelsas
 */
@Service
@RequiredArgsConstructor
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final StoreService storeService;
    private final ItemService itemService;
    private final SequenceNumberService sequenceNumberService;

    public RequisitionData createRequisition(RequisitionData requisition) {
        String reqNo = sequenceNumberService.next(1L, Sequences.RequistionNumber.name());

        Requisition data = new Requisition();
        data.setTransactionDate(requisition.getTransactionDate());
        data.setRequiredDate(requisition.getRequiredDate());
        if (requisition.getStoreId() != null) {
            Store store = storeService.getStore(requisition.getStoreId()).get();
            data.setStore(store);
        }
        if (requisition.getRequestingStoreId() != null) {
            Store store = storeService.getStore(requisition.getRequestingStoreId()).get();
            data.setRequestingStore(store);
        }
        data.setRequestionNumber(reqNo);
        data.setStatus(RequisitionStatus.Draft);
        data.setType(requisition.getRequisitionType());
        data.setRequestedBy(requisition.getRequestedBy() != null ? requisition.getRequestedBy() : SecurityUtils.getCurrentUserLogin().get());
        data.setTerms(requisition.getTerms());

        if (requisition.getRequistionLines() != null) {
            data.addRequsitionItems(
                    requisition.getRequistionLines()
                            .stream()
                            .map(d -> {
                                RequisitionItem item = new RequisitionItem();
                                Item i = itemService.findById(d.getItemId()).get();
                                item.setItem(i);
                                item.setQuantity(d.getQuantity());
                                item.setReceivedQuantity(0);
                                item.setPrice(d.getPrice());

                                return item;
                            }).collect(Collectors.toList())
            );
        }//then we need to save this
        Requisition savedRequisition = requisitionRepository.save(data);
        return RequisitionData.map(savedRequisition);
    }

    public Optional<Requisition> findByRequsitionNumber(final String requistionNo) {
        return requisitionRepository.findByRequestionNumber(requistionNo);
    }

    public Requisition findOneWithNoFoundDetection(Long id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> APIException.notFound("Requisition with Id {0} not found", id));
    }

    public Page<Requisition> getRequisitions(List<RequisitionStatus> status, Pageable page) {
        Specification<Requisition> spec = RequisitionSpecification.createSpecification(status);
        return requisitionRepository.findAll(spec,page);
    }

    public Requisition saveRequisition(Requisition r) {
        return requisitionRepository.save(r);
    }
}
