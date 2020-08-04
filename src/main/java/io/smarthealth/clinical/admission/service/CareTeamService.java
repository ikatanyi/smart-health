/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.domain.repository.CareTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
@RequiredArgsConstructor
public class CareTeamService {

    private final CareTeamRepository careTeamRepository;

    
}
