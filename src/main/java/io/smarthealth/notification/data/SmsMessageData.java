package io.smarthealth.notification.data;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.fasterxml.jackson.annotation.JsonFormat;
import static io.smarthealth.infrastructure.lang.Constants.DATE_PATTERN;
import io.smarthealth.notification.domain.SmsMessage;
import io.smarthealth.notification.domain.enumeration.ReceiverType;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Kennedy.Imbenzi
 */
@Data
public class SmsMessageData {

    private Long id;
    private String name;
    private String message;
    private String phoneNumber;//muliple specified numbers should be separated with a comma
    private String receiverId;//staffnumber/patientNumber
    private String status = "Unsent";
    private LocalDate msgDate = LocalDate.now();
    private String comments;
    @Enumerated(EnumType.STRING)
    private ReceiverType receiverType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate visitDate;

    public SmsMessage map() {
        SmsMessage msg = new SmsMessage();
        msg.setMessage(this.getMessage());
        msg.setMsgDate(this.getMsgDate());
        msg.setName(this.getName());
        msg.setPhoneNumber(this.getPhoneNumber());
        msg.setReceiverType(this.getReceiverType());
        msg.setStatus(this.getStatus());
        return msg;
    }
}
