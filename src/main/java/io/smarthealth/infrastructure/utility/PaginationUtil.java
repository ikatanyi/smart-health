package io.smarthealth.infrastructure.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 *
 * @author Kelsas
 */
public class PaginationUtil {
    
      public static Pageable createPageRequest(final Integer pageIndex, final Integer size, final String sortColumn, final String sortDirection, final String defaultSortColumn) {
        final Integer pageIndexToUse = pageIndex != null ? pageIndex : 0;
        final Integer sizeToUse = size != null ? size : 20;
        final String sortColumnToUse = sortColumn != null ? sortColumn : defaultSortColumn;
        final Sort.Direction direction = sortDirection != null ? Sort.Direction.valueOf(sortDirection.toUpperCase()) : Sort.Direction.ASC;

        return PageRequest.of(pageIndexToUse, sizeToUse, direction, sortColumnToUse);
    }
    public static Pageable createPageRequest(final Integer pageIndex, final Integer size) {
        final Integer pageIndexToUse = pageIndex != null ? pageIndex : 0;
        final Integer sizeToUse = size != null ? size : 20;
      
        return PageRequest.of(pageIndexToUse, sizeToUse);
    }
}
