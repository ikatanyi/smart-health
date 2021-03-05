package io.smarthealth.accounting.billing.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.clinical.visit.domain.enumeration.PaymentMethod;
import io.smarthealth.debtor.scheme.domain.enumeration.CoPayType;
import io.smarthealth.infrastructure.lang.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
//@AllArgsConstructor
public class VisitBillSummary {

   @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
   private LocalDateTime startVisitDate;
   @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
   private LocalDateTime endVisitDate;
   private String visitNumber;
   private VisitEnum.VisitType visitType;
   private PaymentMethod paymentMethod;
   private VisitEnum.Status visitStatus;
   private String patientNumber;
   private String patientName;
   private BigDecimal debitAmount;
   private BigDecimal creditAmount;
//   private BigDecimal balance;
   private Long payerId;
   private String payerName;
   private Long schemeId;
   private String schemeName;
   private String memberName;
   private String memberNumber;
   private Double copayValue;
   private CoPayType copayType;
   private Boolean hasCapitation;
   private BigDecimal capitationAmount;
   private Integer duration;
   private Boolean hasCopay;

   public VisitBillSummary(LocalDateTime startVisitDate, LocalDateTime endVisitDate, String visitNumber, VisitEnum.VisitType visitType, PaymentMethod paymentMethod, VisitEnum.Status visitStatus, String patientNumber, String patientName, BigDecimal debitAmount, BigDecimal creditAmount, Long payerId, String payerName, Long schemeId, String schemeName, String memberName, String memberNumber, Double copayValue, CoPayType copayType, Boolean hasCapitation, BigDecimal capitationAmount) {
      this.startVisitDate = startVisitDate;
      this.endVisitDate = endVisitDate;
      this.visitNumber = visitNumber;
      this.visitType = visitType;
      this.paymentMethod = paymentMethod;
      this.visitStatus = visitStatus;
      this.patientNumber = patientNumber;
      this.patientName = patientName;
      this.debitAmount = debitAmount;
      this.creditAmount = creditAmount;
      this.payerId = payerId;
      this.payerName = payerName;
      this.schemeId = schemeId;
      this.schemeName = schemeName;
      this.memberName = memberName;
      this.memberNumber = memberNumber;
      this.copayValue = copayValue;
      this.copayType = copayType;
      this.hasCapitation = hasCapitation;
      this.capitationAmount = capitationAmount;
   }

   public BigDecimal getBalance(){
      return debitAmount.subtract(creditAmount);
   }
   @JsonProperty("has_copay")
   public Boolean getHasCopay(){
      return copayValue!=null && copayValue > 0;
   }

   public Integer getDuration(){
      LocalDateTime end = endVisitDate!=null ? endVisitDate : LocalDateTime.now();

      Period period = Period.between(startVisitDate.toLocalDate(), end.toLocalDate());

     return period.getDays();
   }
}
