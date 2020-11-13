package io.smarthealth.notify.data;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import io.smarthealth.notify.domain.TextMessage;
import io.smarthealth.notify.domain.enumeration.ReceiverType;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class TextMessageData {
    private Long id;
    private String name;
    private String message;
    private String phoneNumber;
    private String receiverId;//staffnumber/patientNumber
    private String status = "Unsent";
    private LocalDate msgDate = LocalDate.now();
    private String comments;
    @Enumerated(EnumType.STRING)
    private ReceiverType receiverType;
    
    public TextMessage map(){
        TextMessage msg = new TextMessage();
        msg.setMessage(this.getMessage());
        msg.setMsgDate(this.getMsgDate());
        msg.setName(this.getName());
        msg.setPhoneNumber(this.getPhoneNumber());
        msg.setReceiverType(this.getReceiverType());
        msg.setStatus(this.getStatus());
        return msg;
    }
}
