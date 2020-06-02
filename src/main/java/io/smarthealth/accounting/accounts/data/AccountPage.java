package io.smarthealth.accounting.accounts.data;

import java.util.List;

@SuppressWarnings("unused")
public class AccountPage {

    private List<AccountData> accounts;
    private Integer totalPages;
    private Long totalElements;

    public AccountPage() {
        super();
    }

    public List<AccountData> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountData> accounts) {
        this.accounts = accounts;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }
}
