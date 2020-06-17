/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.security.data;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class ActiveUserStore {

    public List<String> users;

    public ActiveUserStore() {
        users = new ArrayList<>();
    }
}
