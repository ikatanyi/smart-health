package io.smarthealth.infrastructure.sequence.service;

import io.smarthealth.infrastructure.sequence.SequenceType;
import io.smarthealth.infrastructure.sequence.domain.SequenceFormat;
import io.smarthealth.infrastructure.sequence.domain.SequenceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *  Database Based sequence generator
 * @author Kelsas
 */
@Service
public class DatabaseSequenceImpl implements SequenceService {

    private final SequenceRepository repository;
    private String companyId="1";

    public DatabaseSequenceImpl(SequenceRepository repository) {
        this.repository = repository;
    }

    @Override
    public String nextNumber(SequenceType type) { 
        Long number = repository.nextSequence(type.sequenceName, companyId);
        Optional<SequenceFormat> format = repository.findByIdType(type);
        
         String nextVal = String.valueOf(number);
         if (format.isPresent()) { 
            SequenceFormat id = format.get();
            
            if(id.getMaxLength() > 0){
                nextVal = StringUtils.leftPad(nextVal, id.getMaxLength(), '0');
            }
            
            String prefix = id.getPrefix();
            String suffix = id.getSuffix();

            if (prefix != null) {
                prefix = prefix.substring(0, Math.min(prefix.length(), 4));
                nextVal = prefix + nextVal;
            }

            if (suffix != null) {
                nextVal = nextVal + suffix;
            }
        } 
        return nextVal;
    }

}
