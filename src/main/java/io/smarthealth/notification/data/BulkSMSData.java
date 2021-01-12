/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.data;

import io.smarthealth.notification.domain.enumeration.ReceiverType;
import java.time.LocalDate;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Data;

/**
 *
 * @author Simon.waweru
 */
@Data
public class BulkSMSData {

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
}
