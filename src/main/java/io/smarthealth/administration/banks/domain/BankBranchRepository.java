package io.smarthealth.administration.banks.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Simon.Waweru
 */
public interface BankBranchRepository extends JpaRepository<BankBranch, Long> {

    Optional<BankBranch> findByBranchNameAndBank(final String branchName, final Bank mainBank);

    Page<BankBranch> findByBank(final Bank mainBank, final Pageable pageable);

    Optional<BankBranch> findByBranchCode(final String code);
}
