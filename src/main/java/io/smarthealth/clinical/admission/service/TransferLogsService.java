package io.smarthealth.clinical.admission.service;

import io.smarthealth.clinical.admission.domain.TransferLogs;
import io.smarthealth.clinical.admission.domain.repository.TransferLogsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Kennedy.Ikatanyi
 */
@Service
@RequiredArgsConstructor
public class TransferLogsService {

    private final TransferLogsRepository transferLogsRepository;


    public Page<TransferLogs> fetchAllTransferLogss(Pageable page) {
        return transferLogsRepository.findAll(page);
    }
    
    public Page<TransferLogs> fetchTransferLogs(Pageable page) {        
        return transferLogsRepository.findAll(page);
    }
}
