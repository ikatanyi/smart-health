package io.smarthealth.accounting.acc.data.v1;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.smarthealth.infrastructure.lang.Constants;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class JournalEntry {

    private String journalNumber;
    @NotNull
    @JsonFormat(pattern = Constants.DATE_TIME_PATTERN)
    private LocalDateTime transactionDate;
    private String transactionNo;
    private String transactionType;
    private String clerk;
    private String note;
    @NotNull
    @Valid
    private Set<Debtor> debtors;
    @NotNull
    @Valid
    private Set<Creditor> creditors;
    private State state;
    @Length(max = 2048)
    private String message;
    private Double amount;
    private String createdBy;

    public JournalEntry() {
        super();
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(final String transactionType) {
        this.transactionType = transactionType;
    }

    public String getClerk() {
        return this.clerk;
    }

    public void setClerk(final String clerk) {
        this.clerk = clerk;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    public Set<Debtor> getDebtors() {
        return this.debtors;
    }

    public void setDebtors(final Set<Debtor> debtors) {
        this.debtors = debtors;
    }

    public Set<Creditor> getCreditors() {
        return this.creditors;
    }

    public void setCreditors(final Set<Creditor> creditors) {
        this.creditors = creditors;
    }

    public String getState() {
        return this.state != null ? this.state.name() : "";
    }

    public void setState(final String state) {
        this.state = State.valueOf(state);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public String getJournalNumber() {
        return journalNumber;
    }

    public void setJournalNumber(String journalNumber) {
        this.journalNumber = journalNumber;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    @SuppressWarnings("WeakerAccess")
    public enum State {
        PENDING,
        PROCESSED
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JournalEntry that = (JournalEntry) o;
        return Objects.equals(journalNumber, that.journalNumber)
                && Objects.equals(transactionDate, that.transactionDate)
                && Objects.equals(transactionType, that.transactionType)
                && Objects.equals(clerk, that.clerk)
                && Objects.equals(note, that.note)
                && Objects.equals(debtors, that.debtors)
                && Objects.equals(creditors, that.creditors)
                && state == that.state
                && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalNumber, transactionDate, transactionType, clerk, note, debtors, creditors, state, message);
    }

    @Override
    public String toString() {
        return "JournalEntry{"
                + "journalNumber='" + journalNumber + '\''
                + ", transactionDate='" + transactionDate + '\''
                + ", transactionType='" + transactionType + '\''
                + ", clerk='" + clerk + '\''
                + ", note='" + note + '\''
                + ", debtors=" + debtors
                + ", creditors=" + creditors
                + ", state=" + state
                + ", message='" + message + '\''
                + '}';
    }
 
}
