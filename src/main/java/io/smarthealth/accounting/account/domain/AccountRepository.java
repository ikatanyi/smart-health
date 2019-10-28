package io.smarthealth.accounting.account.domain;

import io.smarthealth.accounting.account.domain.enumeration.AccountCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Kelsas
 */
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Optional<Account> findByAccountNumber(final String accountNumber);

    @Query("SELECT CASE WHEN count(a) > 0 THEN true ELSE false END FROM Account a where a.accountNumber = :account")
    Boolean existsByAccount(@Param("account") final String accountNumber);

    @Query("SELECT CASE WHEN count(a) > 0 THEN true ELSE false END FROM Account a where a.parentAccount = :account")
    Boolean existsByParentAccount(@Param("account") final Account account);

//    Stream<Account> findByBalanceIsNot(final Double value);
    List<Account> findByParentAccountIsNull();
    
    List<Account> findByParentAccount(Account parentAccount);

    List<Account> findByParentAccountIsNullAndAccountType(final AccountType type);
    
    @Query("SELECT a FROM Account a WHERE a.parentAccount IS NULL and a.accountType.glAccountType=:glcategory")
     List<Account> findParentAccountIsNullAndAccountCategory(@Param(value = "glcategory") AccountCategory accountCategory);
     
     @Query("SELECT a FROM Account a WHERE  a.accountType.glAccountType=:glcategory")
     List<Account> findByAccountsCategory(@Param(value = "glcategory") AccountCategory accountCategory);
    
    List<Account> findByAccountType(final AccountType type);

    List<Account> findByParentAccountOrderByAccountNumber(final Account parentAccount);

    //account balances
}
