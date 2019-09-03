package io.smarthealth.financial.account.service;

import io.smarthealth.financial.account.data.AccountData;
import io.smarthealth.financial.account.domain.Account;
import io.smarthealth.financial.account.domain.AccountRepository;
import io.smarthealth.financial.account.domain.enumeration.AccountType;
import io.smarthealth.financial.account.domain.Ledger;
import io.smarthealth.financial.account.domain.LedgerRepository;
import io.smarthealth.financial.account.domain.TransactionTypeRepository; 
import io.smarthealth.financial.account.domain.specification.AccountSpecification;
import io.smarthealth.infrastructure.exception.APIException; 
import java.util.Optional; 
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 *
 * @author Kelsas
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final LedgerRepository ledgerRepository;
    private final ModelMapper modelMapper;
    private final TransactionTypeRepository transactionTypeRepository;

    public AccountService(AccountRepository accountRepository,
            ModelMapper modelMapper,
            LedgerRepository ledgerRepository,
            TransactionTypeRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.ledgerRepository = ledgerRepository;
        this.transactionTypeRepository = transactionRepository;
        this.modelMapper = modelMapper;
    }

    public AccountData createAccount(AccountData accountData) {
        Account account = convertToEntity(accountData);
        if (accountData.getReferenceAccount() != null) {
            account.setReferenceAccount(
                    findAccount(accountData.getReferenceAccount())
                            .orElseThrow(() -> APIException.notFound("Reference parent account {0} not available.", accountData.getReferenceAccount()))
            );
        }
        Ledger ledger = ledgerRepository.findByIdentifier(accountData.getLedger()).get();
        account.setLedger(ledger);
        return convertToData(accountRepository.save(account));
    }

    public Page<Account> findAllAccount(Pageable page) {
        return accountRepository.findAll(page);
    }

    public Optional<Account> findAccount(String accountCode) {
        return accountRepository.findByIdentifier(accountCode);
    }

    public String modifyAccount(AccountData accountData) {
        Account account = accountRepository.findByIdentifier(accountData.getIdentifier()).get();
        if (accountData.getIdentifier() != null) {
            account.setIdentifier(accountData.getIdentifier());
        }
        if (accountData.getName() != null) {
            account.setName(accountData.getName());
        }
        if (accountData.getType() != null) {
            account.setType(accountData.getType().name());
        }

        Account referenceAccount = null;
        if (accountData.getReferenceAccount() != null) {
            if (!accountData.getReferenceAccount().equals(account.getReferenceAccount().getIdentifier())) {
                referenceAccount = this.accountRepository.findByIdentifier(accountData.getReferenceAccount()).get();
                account.setReferenceAccount(referenceAccount);
            }
        } else {
            account.setReferenceAccount(null);
        }
        Account updated = accountRepository.save(account);
        return updated.getIdentifier();
    }

    public Page<Account> fetchAccounts(final boolean includeClosed, String term, final String type, Pageable pageable) {
        throwIfAccountTypeNotValid(type);

        Specification<Account> spec = AccountSpecification.createSpecification(includeClosed, term, type);
        Page<Account> accounts = accountRepository.findAll(spec, pageable);
        return accounts;
    }

    private void throwIfAccountTypeNotValid(String type) {
        if (type == null) {
            return;
        }
        String types = StringUtils.upperCase(type);
        try {
            AccountType.valueOf(types);
        } catch (Exception ex) {
            throw APIException.badRequest("Account Type : {0} is not supported .. ", type);
        }
    }

    //
    public AccountData convertToData(Account account) {
        AccountData accdata=new AccountData();
        accdata.setType(AccountType.valueOf(account.getType()));
//        accdata.setBalance(account.getBalance());
        accdata.setIdentifier(account.getIdentifier());
        accdata.setLedger(account.getLedger()!=null ? account.getLedger().getIdentifier() : null);
        accdata.setName(account.getName());
        accdata.setReferenceAccount(account.getReferenceAccount()!=null ? account.getReferenceAccount().getIdentifier() : null);
          
        return accdata;
    }

    public Account convertToEntity(AccountData data) {
        Account account = modelMapper.map(data, Account.class); 
        return account;
    }
}
