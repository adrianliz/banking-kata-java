package com.optivem.kata.banking.core.usecases.viewaccount;

import com.optivem.kata.banking.core.domain.accounts.AccountNumber;
import com.optivem.kata.banking.core.domain.accounts.BankAccount;
import com.optivem.kata.banking.core.domain.accounts.BankAccountRepository;
import com.optivem.kata.banking.core.usecases.UseCase;

import static com.optivem.kata.banking.core.domain.extensions.Extension.extend;

public class ViewAccountUseCase implements UseCase<ViewAccountRequest, ViewAccountResponse> {

    private final BankAccountRepository repository;

    public ViewAccountUseCase(BankAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public ViewAccountResponse handle(ViewAccountRequest request) {
        var accountNumber = getAccountNumber(request);
        var bankAccount = findBankAccount(accountNumber);
        return getResponse(bankAccount);
    }

    private AccountNumber getAccountNumber(ViewAccountRequest request) {
        return AccountNumber.of(request.getAccountNumber());
    }

    private BankAccount findBankAccount(AccountNumber accountNumber) {
        return extend(repository).findRequired(accountNumber);
    }

    private ViewAccountResponse getResponse(BankAccount bankAccount) {
        var response = new ViewAccountResponse();
        response.setAccountNumber(bankAccount.getAccountNumber().value().value());
        response.setFullName(bankAccount.getAccountHolderName().getFullName().value());
        response.setBalance(bankAccount.getBalance().value().value());
        return response;
    }
}