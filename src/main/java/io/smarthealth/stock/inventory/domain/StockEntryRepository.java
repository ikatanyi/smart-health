package io.smarthealth.stock.inventory.domain;

import io.smarthealth.stock.inventory.data.ExpiryStock;
import io.smarthealth.stock.inventory.data.StockMovement;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Kelsas
 */
@Repository
public interface StockEntryRepository extends JpaRepository<StockEntry, Long>, JpaSpecificationExecutor<StockEntry> {

    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId", nativeQuery = true)
    List<StockMovement>getStockEntriesByItem(@Param("itemId") Long itemId);
    
    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId AND st.id=:storeId", nativeQuery = true)
    List<StockMovement> getStockEntriesByStoreIdAndItemId(@Param("itemId") Long itemId,@Param("storeId") Long storeId);
    
    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId AND st.id=:storeId AND transaction_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
    List<StockMovement> getEntriesByItemStoreAndDateRange(@Param("itemId") Long itemId,@Param("storeId") Long storeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
   
    @Query(value = "SELECT se.id, transaction_date AS transDate,p.id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, st.id AS storeId, st.store_name AS storeName, se.issued_to AS description, case when SIGN(se.quantity) = -1 then 0 ELSE se.quantity end AS received, case when SIGN(se.quantity) = -1 then se.quantity ELSE 0 end AS issued, SUM(quantity) over (PARTITION BY store_id, item_id ORDER BY se.id ) as balance, se.price, (se.price * se.quantity) AS total, se.created_on AS createDate, se.created_by AS createBy FROM stock_inventory_entries se JOIN product_services p ON p.id=se.item_id JOIN st_stores st ON st.id=se.store_id WHERE p.id=:itemId  AND transaction_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
    List<StockMovement> getEntriesByItemDateRange(@Param("itemId") Long itemId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
   
    @Query(value = "SELECT ie.item_id AS itemId, p.item_code AS itemCode, p.item_name AS itemName, ie.store_id AS storeId, s.store_name AS storeName, SUM(ie.quantity) AS quantity, ie.batch_no AS batchNo, ie.expiry_date AS expiryDate, DATEDIFF(ie.expiry_date, now()) as days FROM stock_inventory_entries ie CROSS JOIN product_services p CROSS JOIN st_stores s WHERE ie.item_id= p.id AND ie.store_id = s.id GROUP BY ie.store_id, ie.item_id, ie.batch_no HAVING  SUM(ie.quantity)>0", nativeQuery = true)
    List<ExpiryStock> findExpiryStockInterface();
    
    List<StockEntry>findByReferenceNumber(String referenceNumber);
}
