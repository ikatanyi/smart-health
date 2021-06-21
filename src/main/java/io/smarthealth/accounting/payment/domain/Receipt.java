package io.smarthealth.accounting.payment.domain;

import io.smarthealth.accounting.cashier.domain.Shift;
import io.smarthealth.accounting.payment.data.ReceiptData;
import io.smarthealth.clinical.visit.data.enums.VisitEnum;
import io.smarthealth.infrastructure.domain.Auditable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import io.smarthealth.accounting.payment.domain.enumeration.ReceiptType;

/**
 *
 * @author Kelsas
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "acc_receipts")
public class Receipt extends Auditable {

    public static enum Type{
        Refund,
        Payment
    }
    private String payer;
    private String description; //Insurance payment | Cheque deposit
    private BigDecimal amount;
//    private BigDecimal credit;
    private BigDecimal tenderedAmount;
    private BigDecimal refundedAmount;
    private BigDecimal paid;
    private String paymentMethod;
    private String referenceNumber; //voucher no,
    private String receiptNo;
    private LocalDateTime transactionDate;
    private String transactionNo;
    private String currency;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_receipts_shift_id"))
    private Shift shift;
    private Boolean prepayment;
    private String receivedFrom;
    //receipt transaction POS | Remittance | Deposit | Payment
    private Boolean voided;
    private String voidedBy;
    private LocalDateTime voidedDatetime;
    private String comments;
    @Enumerated(EnumType.STRING)
    private VisitEnum.VisitType visitType;
    @Enumerated(EnumType.STRING)
    private Type type = Type.Payment;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<ReceiptTransaction> transactions = new ArrayList<>();

    @Where(clause = "voided = false")
    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<ReceiptItem> receiptItems = new ArrayList<>();

    public void addTransaction(ReceiptTransaction transaction) {
        transaction.setReceipt(this);
        transactions.add(transaction);
    }

    public void addTransaction(List<ReceiptTransaction> transactions) {
        this.transactions = transactions;
        this.transactions.forEach(x -> x.setReceipt(this));
    }

    public void addReceiptItem(ReceiptItem item) {
        item.setReceipt(this);
        receiptItems.add(item);
    }

    public void addReceiptItem(List<ReceiptItem> items) {
        this.receiptItems = items;
        this.receiptItems.forEach(x -> x.setReceipt(this));
    }

    public ReceiptData toData() {
        ReceiptData data = new ReceiptData();
        data.setId(this.getId());
        data.setPayer(this.payer);
        data.setDescription(this.description);
        data.setAmount(this.amount);
//        data.setCredit(this.credit);
        data.setRefundedAmount(this.refundedAmount);
        data.setTenderedAmount(this.tenderedAmount);
        data.setPaymentMethod(this.paymentMethod);
        data.setReferenceNumber(this.referenceNumber);
        data.setTransactionNo(this.transactionNo);
        data.setReceiptNo(this.receiptNo);
        data.setCurrency(this.currency);
        data.setPaid(this.getPaid());
        data.setPrepayment(this.prepayment);
        data.setReceivedFrom(this.getReceivedFrom());
        data.setTransactionDate(this.getTransactionDate());
        data.setCreatedBy(this.getCreatedBy());
        if(this.getShift()!=null){
            data.setShiftData(this.getShift().toData());
        }
        if (this.shift != null) {
            data.setShiftNo(this.shift.getShiftNo());
        }
        data.setTransactions(
                this.transactions
                        .stream().map(x -> x.toData())
                        .collect(Collectors.toList())
        );
        data.setReceiptItems(
                this.getReceiptItems()
                        .stream().map(x -> x.toData())
                        .collect(Collectors.toList())
        );

        data.setVoided(this.voided);
        data.setVoidedBy(this.voidedBy);
        data.setVoidedDatetime(this.voidedDatetime);
        data.setComments(this.comments);

        if (prepayment) {
            data.setReceiptType(ReceiptType.Deposit);
        } else {

            if (this.receiptItems.isEmpty() && (refundedAmount.compareTo(BigDecimal.ZERO) == 0)) {
                data.setReceiptType(ReceiptType.Payment);
            } else {
                data.setReceiptType(ReceiptType.POS);
            }
        }

        data.setVisitType(this.visitType);
        data.setCreatedOn(LocalDateTime.ofInstant(this.getCreatedOn(), ZoneId.systemDefault()));

        return data;
    }
}
