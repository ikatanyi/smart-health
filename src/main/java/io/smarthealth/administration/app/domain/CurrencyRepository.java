/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.administration.app.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Kelsas
 */
public interface CurrencyRepository extends JpaRepository<Currency, Long>{
      Page<Currency> findByActiveTrue(Pageable page);

    Optional<Currency> findByName(String name);
    
     Optional<Currency> findByCode(String code);
     
}
