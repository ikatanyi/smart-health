package io.smarthealth.financial.accounting.domain;

import io.smarthealth.common.domain.Identifiable;
import io.smarthealth.organization.domain.Partner;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * The move holds information about reference (which entity produced this move),
 * relation to partner/customer and date when move was created.
 *
 * When new invoice, purchase, refund or payment is created in the system, new
 * move is created. Journal Entries
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "account_move")
public class Move extends Identifiable {

    public enum Status {
        Draft,
        Posted
    }
    @Column(length = 64)
    private String name;
    @Column(length = 64)
    private String reference;
    private String description;
    private Status status;
    private LocalDateTime datePosted;

    @ManyToOne
    private Partner partner;
    
    @OneToMany(mappedBy = "move")
    private List<MoveLine> moveLines;

}
