package io.smarthealth.stock.inventory.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.stock.inventory.domain.StockEntry;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
public class StockTransferData {

    private String transferNo;
    private Double cachedQuantity;
    private Long sourceStoreId;
    private String sourceStore;
    private Long destinationStoreId;
    private String destinationStore;
    private String notes;
    private StockEntry.Status status;
    @JsonFormat(pattern = Constants.DATE_PATTERN)
    private LocalDate createdAt;
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime receivedAt;
    public StockTransferData(String transferNo, Double cachedQuantity, Long sourceStoreId, String sourceStore, Long destinationStoreId, String destinationStore, String notes, StockEntry.Status status, LocalDate createdAt, LocalDateTime receivedAt) {
        this.transferNo = transferNo;
        this.cachedQuantity = cachedQuantity;
        this.sourceStoreId = sourceStoreId;
        this.sourceStore = sourceStore;
        this.destinationStoreId = destinationStoreId;
        this.destinationStore = destinationStore;
        this.notes = notes;
        this.status = status;
        this.createdAt = createdAt;
        this.receivedAt = receivedAt;
    }
}
