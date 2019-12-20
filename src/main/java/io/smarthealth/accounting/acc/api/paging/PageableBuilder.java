package io.smarthealth.accounting.acc.api.paging;

 
//import javax.annotation.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageableBuilder {

  public static Pageable create(
       final Integer pageIndex,
       final Integer size,
       final String sortColumn,
       final String sortDirection) {
    final Integer pageIndexToUse = pageIndex != null ? pageIndex : 0;
    final Integer sizeToUse = size != null ? size : 20;
    final String sortColumnToUse = sortColumn != null ? sortColumn : "identifier";
    final Sort.Direction direction = sortDirection != null ? Sort.Direction.valueOf(sortDirection.toUpperCase()) : Sort.Direction.ASC;
    return PageRequest.of(pageIndexToUse, sizeToUse, direction, sortColumnToUse);
  }

}
