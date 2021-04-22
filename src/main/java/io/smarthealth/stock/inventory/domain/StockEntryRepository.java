package io.smarthealth.stock.inventory.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import io.smarthealth.infrastructure.lang.DateRange;
import io.smarthealth.stock.inventory.data.ExpiryStock;
import io.smarthealth.stock.inventory.data.StockMovement;
import io.smarthealth.stock.inventory.data.StockTransferData;
import io.smarthealth.stock.inventory.domain.enumeration.MovementType;
import io.smarthealth.stock.item.domain.Item;
import io.smarthealth.stock.stores.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Kelsas
 */
@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Long>, JpaSpecificationExecutor<StockEntry> {

    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId", nativeQuery = true)
    List<StockMovement> getStockEntriesByItem(@Param("itemId") Long itemId);

    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId AND st.id=:storeId", nativeQuery = true)
    List<StockMovement> getStockEntriesByStoreIdAndItemId(@Param("itemId") Long itemId, @Param("storeId") Long storeId);

    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId AND st.id=:storeId AND transaction_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
    List<StockMovement> getEntriesByItemStoreAndDateRange(@Param("itemId") Long itemId, @Param("storeId") Long storeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId  AND transaction_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
    List<StockMovement> getEntriesByItemDateRange(@Param("itemId") Long itemId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = "SELECT ie.item_id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, " +
            "ie.store_id AS storeId, s.store_name AS storeName, SUM(ie.quantity) AS quantity, " +
            "ie.batch_no AS batchNo, ie.expiry_date AS expiryDate, " +
            "DATEDIFF(ie.expiry_date, now()) as days FROM stock_inventory_entries ie " +
            "CROSS JOIN product_services p CROSS JOIN st_stores s " +
            "WHERE ie.item_id= p.id AND ie.store_id = s.id GROUP BY ie.store_id, ie.item_id, " +
            "ie.batch_no HAVING  SUM(ie.quantity)>0", nativeQuery = true)
    List<ExpiryStock> findExpiryStockInterface();

    List<StockEntry> findByReferenceNumber(String referenceNumber);

    @Query(value = "SELECT sum(s.quantity) FROM StockEntry s WHERE s.item =:item AND s.store =:store GROUP BY s.item ")
    Double sumQuantities(Item item, Store store);

    List<StockEntry> findByMoveTypeAndReferenceNumber(MovementType type, String invoiceNo);

    List<StockEntry> findStockEntriesByDeliveryNumber(String docNo);
                                                                                                                                                                                                                                //    SELECT e FROM employees e LEFT  JOIN e.posts p GROUP BY e HAVING max(p.timestamp) < ? OR count(p) = 0"
    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' group by s.referenceNumber ")
    Page<StockTransferData> findStockTransfers(Pageable pageable);

    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND s.status IN :status group by s.referenceNumber ")
    Page<StockTransferData> findStockTransfers(@Param("status") Collection<StockEntry.Status> status, Pageable pageable);

    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND d.id = :storeId group by s.referenceNumber ")
    Page<StockTransferData> findStockTransfers(@Param("storeId") Long storeId, Pageable pageable);

    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND s.transactionDate between :start and :end group by s.referenceNumber ")
    Page<StockTransferData> findStockTransfers(LocalDate start,LocalDate end, Pageable pageable);

    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND s.transactionDate between :start and :end AND d.id = :storeId AND s.status IN :status group by s.referenceNumber ")
    Page<StockTransferData> findStockTransfers(LocalDate start,LocalDate end, Long storeId,Collection<StockEntry.Status> status, Pageable pageable);

    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND s.transactionDate between :start and :end AND s.status IN :status group by s.referenceNumber ")
    Page<StockTransferData> findStockTransfers(LocalDate start,LocalDate end, Collection<StockEntry.Status> status, Pageable pageable);

    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND d.id = :storeId AND s.status IN :status group by s.referenceNumber ")
    Page<StockTransferData> findStockTransfers(Long storeId, Collection<StockEntry.Status> status, Pageable pageable);

    @Query("SELECT s FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND s.referenceNumber = :transferNo ")
    List<StockEntry> findStockTransfersItems(@Param("transferNo") String transferNo);

    @Modifying
    @Query("UPDATE StockEntry e SET e.receivedAt=current_timestamp , e.status = 'Received', e.cachedQuantity=0 WHERE  e.referenceNumber = :transferNo")
    void receiveStocks(String transferNo);

    @Modifying
    @Query("UPDATE StockEntry e SET e.receivedAt=current_timestamp , e.status = 'Deleted', e.cachedQuantity=0 WHERE  e.referenceNumber = :transferNo")
    void reverseStockTransfer(String transferNo);

    @Query("SELECT new io.smarthealth.stock.inventory.data.StockTransferData(s.referenceNumber, sum(s.cachedQuantity),s.store.id, s.store.storeName, s.destinationStore.id,s.destinationStore.storeName,s.notes,s.status, s.transactionDate, s.receivedAt) FROM StockEntry s LEFT JOIN s.destinationStore d WHERE s.purpose='Transfer' AND s.referenceNumber = :transferNo group by s.referenceNumber ")
    StockTransferData findStockTransfers(String transferNo);

}
