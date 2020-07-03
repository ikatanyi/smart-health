package io.smarthealth.accounting.pricelist.domain;

import io.smarthealth.accounting.pricelist.domain.enumeration.PriceCategory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface PriceBookRepository extends JpaRepository<PriceBook, Long>, JpaSpecificationExecutor<PriceBook> {

    Optional<PriceBook> findByName(String name);

    @Query(name = "pricelistEntity.getAllPriceList", nativeQuery = true)
    List<PriceListDTO> getPriceLists();

    List<PriceBook> findByPriceCategory(PriceCategory category);

    @Query(name = "pricelistEntity.searchPriceListByItem", nativeQuery = true)
    List<PriceListDTO> searchPriceListByItem(@Param("item") String item);

    @Modifying
    @Query(value = "INSERT  into price_book_item (amount, price_book_id, item_id) select :amount, :price, :item ", nativeQuery = true)
    int addPriceBookItem(@Param("amount") BigDecimal amount, @Param("price") Long priceId, @Param("item") Long itemId);
    
    
    
}
