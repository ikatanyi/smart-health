package io.smarthealth.infrastructure.sequence.service;

import io.smarthealth.infrastructure.sequence.SequenceType;

/**
 *
 * @author Kelsas
 */
@Deprecated
public interface SequenceService {

    public String nextNumber(SequenceType type);
}
