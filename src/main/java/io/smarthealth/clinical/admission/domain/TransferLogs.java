/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.domain;

import io.smarthealth.clinical.admission.data.BedChargeData;
import io.smarthealth.clinical.admission.data.TransferLogsData;
import io.smarthealth.infrastructure.domain.Auditable;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author kent
 */
@Entity
@Data
@Table(name = "transfer_logs")
public class TransferLogs extends Auditable{
    private String fromBed;
    private String toBed;
    private String fromWard;
    private String toWard;
    private String fromRoom;
    private String toRoom;
    private LocalDate transferDate;
    
    
    public TransferLogsData toData(){
        TransferLogsData data = new TransferLogsData();
        data.setFromBed(this.fromBed);
        data.setFromRoom(this.fromRoom);
        data.setFromWard(this.fromWard);
        data.setToBed(this.toBed);
        data.setToRoom(this.toRoom);
        data.setTransferDate(this.transferDate);        
        return data;
    }
}
