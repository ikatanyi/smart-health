/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.financial.account.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  Account Mapper Class to Internal Implementation
 * 
 * @author Kelsas
 */
public interface AccountRuleMapRepository extends JpaRepository<AccountRuleMap, Long>{
    
}
