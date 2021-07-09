package io.smarthealth.infrastructure.utility;
  
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 *
 * @author Kelsas
 */   
public class PageDetails {

    private Integer page;
    private Integer perPage;
    private Integer totalPage;
    private Long totalElements;
    private String reportName;
    //added for Pato
    private Integer male_under_5;
    private Integer male_above_5;
    private Integer female_under_5;
    private Integer female_above_5;



    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String reportPeriod;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    //for Pato
    public Integer getMale_under_5() {
        return male_under_5;
    }

    public void setMale_under_5(Integer male_under_5) {
        this.male_under_5 = male_under_5;
    }

    public Integer getMale_above_5() {
        return male_above_5;
    }

    public void setMale_above_5(Integer male_above_5) {
        this.male_above_5 = male_above_5;
    }

    public Integer getFemale_under_5() {
        return female_under_5;
    }

    public void setFemale_under_5(Integer female_under_5) {
        this.female_under_5 = female_under_5;
    }

    public Integer getFemale_above_5() {
        return female_above_5;
    }

    public void setFemale_above_5(Integer female_above_5) {
        this.female_above_5 = female_above_5;
    }



}
