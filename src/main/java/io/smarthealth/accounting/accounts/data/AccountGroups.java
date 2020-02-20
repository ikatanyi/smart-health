/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.accounting.accounts.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Kelsas
 */
@Data
public class AccountGroups {
    
    private List<SimpleAccountData> assets = new ArrayList<>();
    private List<SimpleAccountData> liabilities = new ArrayList<>();
    private List<SimpleAccountData> equity = new ArrayList<>();
    private List<SimpleAccountData> revenue = new ArrayList<>();
    private List<SimpleAccountData> expenses = new ArrayList<>();
     
}
