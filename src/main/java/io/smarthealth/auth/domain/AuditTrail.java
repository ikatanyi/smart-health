/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.auth.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
//import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Entity
@Table(name = "audit_trail")
@Data
public class AuditTrail implements Serializable {

    @GeneratedValue
    @Id
    private Long id;

    @Column(name = "activity")
    private boolean activity;

    @Column(name = "action_description")
    private boolean actionDescription;

    @Column(name = "version")
    private boolean version;

    @Column(name = "inital_value")
    private boolean initialValue;

    @Column(name = "new_value")
    private boolean newValue;

}
