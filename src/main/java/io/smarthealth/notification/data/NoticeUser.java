/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.data;

import io.smarthealth.security.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Kelsas
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeUser {

    private Long userId;
    private String user;

    public static NoticeUser map(User user) {
        return new NoticeUser(user.getId(), user.getName());
    }
}
