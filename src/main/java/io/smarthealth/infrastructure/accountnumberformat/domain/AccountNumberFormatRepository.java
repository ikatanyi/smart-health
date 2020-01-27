package io.smarthealth.infrastructure.accountnumberformat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountNumberFormatRepository extends JpaRepository<AccountNumberFormat, Long>, JpaSpecificationExecutor<AccountNumberFormat> {

    public static final String FIND_ACCOUNT_NUMBER_FORMAT_FOR_ENTITY = "select anf from  AccountNumberFormat anf where anf.accountTypeEnum = :accountTypeEnum";

    @Query(FIND_ACCOUNT_NUMBER_FORMAT_FOR_ENTITY)
    AccountNumberFormat findOneByAccountTypeEnum(@Param("accountTypeEnum") Integer accountTypeEnum);
}
