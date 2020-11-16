package io.smarthealth.notify.domain;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import io.smarthealth.infrastructure.domain.Auditable;
import io.smarthealth.notify.data.TextMessageData;
import io.smarthealth.notify.domain.enumeration.ReceiverType;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
@Entity
@Table(name = "text_messages")
public class TextMessage extends Auditable {

    private String name;
    private String message;
    private String status;
    private LocalDate msgDate;
    private String comments;
    @Enumerated(EnumType.STRING)
    private ReceiverType receiverType;
    private String phoneNumber;
    
    public TextMessageData toData(){
        TextMessageData data = new TextMessageData();
        data.setId(this.getId());
        data.setComments(this.getComments());
        data.setMessage(this.getMessage());
        data.setMsgDate(this.getMsgDate());
        data.setName(this.getName());
        data.setReceiverType(this.getReceiverType());
        data.setPhoneNumber(this.getPhoneNumber());
        return data;
    }
}
