package io.smarthealth.stock.inventory.data;

import io.smarthealth.stock.inventory.domain.StockEntry;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Getter
@Setter
public class StockTransferReport extends StockTransferData{
    private List<StockEntryData> stockTransferLineItem =new ArrayList<>();
    public StockTransferReport(StockTransferData data) {
        super(data.getTransferNo(), data.getCachedQuantity(), data.getSourceStoreId(), data.getSourceStore(), data.getDestinationStoreId(), data.getSourceStore(), data.getNotes(), data.getStatus(), data.getCreatedAt(), data.getReceivedAt());
    }
    public void addItems(List<StockEntry> data){
        stockTransferLineItem = data.stream()
                .map(StockEntry::toData)
                .collect(Collectors.toList());
    }
}
