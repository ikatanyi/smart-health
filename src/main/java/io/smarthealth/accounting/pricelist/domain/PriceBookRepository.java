package io.smarthealth.accounting.pricelist.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface PriceBookRepository extends JpaRepository<PriceBook, Long>,JpaSpecificationExecutor<PriceBook>  {
  Optional<PriceBook> findByName(String name);
  
  @Query(name = "pricelistEntity.getAllPriceList", nativeQuery = true)
  List<PriceListDTO> getPriceLists();
  
  @Query(name = "pricelistEntity.searchPriceListByItem", nativeQuery = true)
  List<PriceListDTO>  searchPriceListByItem(@Param("item") String item);
  
}
