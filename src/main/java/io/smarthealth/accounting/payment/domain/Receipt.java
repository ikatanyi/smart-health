package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
//    String getCashPoint();
//    public String getCashier();
//    public LocalDateTime getStartDate();
//    public LocalDateTime getEndDate();
//    public String getShiftNo();
//    public ShiftStatus getStatus();
//    public BigDecimal getBalance();

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_receipts") 
//@NamedQuery(name = "t", query = "SELECT R.receipt.shift.cashPoint AS cashPoint,R.receipt.shift.cashier.user.name as cashier, R.receipt.shift.startDate as startDate,  R.receipt.shift.endDate as endDate,  R.receipt.shift.shiftNo as shiftNo,  R.receipt.shift.status as status, SUM(R.amount) as balance  FROM ReceiptTransaction as R GROUP BY R.receipt.shift.shiftNo ORDER BY R.receipt.transactionDate DESC")
public class Receipt extends Auditable {

    private String payer;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal amount;
//    private BigDecimal credit;
    private BigDecimal refundedAmount;
    private String paymentMethod;
    private String referenceNumber; //voucher no,
    private String receiptNo;
    private LocalDateTime transactionDate;
    private String transactionNo;
    private String currency;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_receipts_shift_id"))
    private Shift shift;
    private Boolean voided;
    private String voidedBy;
    private LocalDateTime voidedDatetime;
 
    @OneToMany(mappedBy = "receipt")
    private List<ReceiptTransaction> transactions = new ArrayList<>();

    public void addTransaction(ReceiptTransaction transaction) {
        transaction.setReceipt(this);
        transactions.add(transaction);
    }

    public void addTransaction(List<ReceiptTransaction> transactions) {
        this.transactions = transactions;
        this.transactions.forEach(x -> x.setReceipt(this));
    }

    public ReceiptData toData() {
        ReceiptData data = new ReceiptData();
        data.setId(this.getId());
        data.setPayer(this.payer);
        data.setDescription(this.description);
        data.setAmount(this.amount);
//        data.setCredit(this.credit);
        data.setRefundedAmount(this.refundedAmount);
        data.setPaymentMethod(this.paymentMethod);
        data.setReferenceNumber(this.referenceNumber);
        data.setTransactionNo(this.transactionNo);
        data.setReceiptNo(this.receiptNo);
        data.setCurrency(this.currency);
        data.setCreatedBy(this.getCreatedBy());
        if (this.shift != null) {
            data.setShiftNo(this.shift.getShiftNo());
        }
        data.setTransactions(
                this.transactions
                        .stream().map(x -> x.toData())
                        .collect(Collectors.toList())
        );

        return data;
    }
}

//    private LocalDateTime receiptDatetime;
//    private String paymentMethod;
//    private String receiptNo;
//    private String referenceNumber;
//    private BigDecimal debit;
//    private BigDecimal credit;
//    private String transactionType; // Receipting | Refund | Banking
//    private LocalDate transactionDate;
//    private String transactionNo;
//    private String shiftNo;
