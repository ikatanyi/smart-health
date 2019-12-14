package io.smarthealth.infrastructure.utility;
   
import lombok.Data;




/**
 *
 * @author Kelsas
 */  
@Data
public class PageDetails {

    private Integer page;
    private Integer perPage;
    private Integer totalPage;
    private Long totalElements;
    private String reportName;
}
