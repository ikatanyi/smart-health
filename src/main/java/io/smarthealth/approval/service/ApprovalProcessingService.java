/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.approval.service;

import io.smarthealth.approval.domain.repo.ApprovalStageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Simon.waweru
 */
@Service
public class ApprovalProcessingService {

    @Autowired
    ApprovalStageRepository approvalStageRepository;
}
