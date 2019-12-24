package io.smarthealth.stock.inventory.service;

import io.smarthealth.infrastructure.exception.APIException;
import io.smarthealth.stock.inventory.data.RequisitionData;
import io.smarthealth.stock.inventory.domain.Requisition;
import io.smarthealth.stock.inventory.domain.RequisitionItem;
import io.smarthealth.stock.inventory.domain.RequisitionRepository;
import io.smarthealth.stock.inventory.domain.enumeration.RequisitionStatus;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.item.domain.Uom;
import io.smarthealth.stock.item.service.ItemService;
import io.smarthealth.stock.item.service.UomService;
import io.smarthealth.stock.stores.domain.Store;
import io.smarthealth.stock.stores.service.StoreService;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class RequisitionService {

    private final RequisitionRepository requisitionRepository; 
    private final StoreService storeService;
    private final ItemService itemService;
    private final UomService uomService;

    public RequisitionService(RequisitionRepository requisitionRepository, StoreService storeService, ItemService itemService, UomService uomService) {
        this.requisitionRepository = requisitionRepository;
        this.storeService = storeService;
        this.itemService = itemService;
        this.uomService = uomService;
    }
 
    public RequisitionData createRequisition(RequisitionData requisition) {
        Requisition data = new Requisition();
        data.setTransactionDate(requisition.getTransactionDate());
        data.setRequiredDate(requisition.getRequiredDate());
        if (requisition.getStoreid() != null) {
            Store store = storeService.getStore(requisition.getStoreid()).get();
            data.setStore(store);
        }
        data.setRequestionNumber(requisition.getRequestionNo());
        data.setStatus(RequisitionStatus.Draft);
        data.setType(requisition.getRequisitionType());
        data.setRequestedBy(requisition.getRequestedBy());
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
                                if(d.getUom()!=null){
                                    Uom um=uomService.fetchUomById(d.getUomId());
                                    item.setUom(um);
                                }

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

    public Page<Requisition> getRequisitions(String status, Pageable page) {
        RequisitionStatus state = null;
        if (EnumUtils.isValidEnum(RequisitionStatus.class, status)) {
            state = RequisitionStatus.valueOf(status);
            return requisitionRepository.findByStatus(state, page);
        }
        return requisitionRepository.findAll(page);
    }
}
