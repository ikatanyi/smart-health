/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.stock.inventory.domain.aggregation;

import java.time.LocalDate;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class StockEntryGroup {

    private LocalDate date;
    private String transactionNumber;
    private String reason;
    private String status;
    private Double quantity;
    private Long sourceId;
    private String source;
    private Long destinationId;
    private String destination;

    // SELECT date, reference_number, move_type,purpose,store_id from stock_inventory_entries
}
