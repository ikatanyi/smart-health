package io.smarthealth.infrastructure.sequence.service.impl;

import io.smarthealth.infrastructure.sequence.service.TxnService;
import io.smarthealth.sequence.Snowflake;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Deprecated
@Service
public class TxnServiceSnowflakeImpl implements TxnService {

    private final Snowflake snowflake;

    public TxnServiceSnowflakeImpl(Snowflake snowflake) {
        this.snowflake = snowflake;
    }

    @Override
    public String nextId() {
        return String.valueOf(this.snowflake.nextId());
    }

}
