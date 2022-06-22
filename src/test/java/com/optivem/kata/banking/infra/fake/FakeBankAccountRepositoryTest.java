package com.optivem.kata.banking.infra.fake;

import com.optivem.kata.banking.core.domain.accounts.AccountNumber;
import com.optivem.kata.banking.core.domain.accounts.TransactionAmount;
import com.optivem.kata.banking.core.domain.common.exceptions.RepositoryMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.optivem.kata.banking.core.common.Verifications.verifyThat;
import static com.optivem.kata.banking.core.common.builders.entities.BankAccountTestBuilder.bankAccount;

class FakeBankAccountRepositoryTest {

    private FakeBankAccountRepository repository;

    @BeforeEach
    private void init() {
        this.repository = new FakeBankAccountRepository();
    }

    @Test
    void should_return_empty_result_when_account_number_does_not_exist() {
        var accountNumber = "GB36BARC20038032622823";

        verifyThat(repository).shouldNotContain(accountNumber);
    }

    @Test
    void should_return_bank_account_when_account_number_exists() {
        var accountNumber = "GB36BARC20038032622823";
        var bankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .build();

        repository.add(bankAccount);

        verifyThat(repository).shouldContain(bankAccount);
    }

    @Test
    void should_retrieve_updated_bank_account_after_update() {
        var accountNumber = "GB36BARC20038032622823";
        var balance = 40;

        var bankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        var updateBalance = 50;

        var expectedUpdatedBankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(updateBalance)
                .build();

        repository.add(bankAccount);

        bankAccount.deposit(TransactionAmount.of(10));

        repository.update(bankAccount);

        verifyThat(repository).shouldContain(expectedUpdatedBankAccount);
    }

    @Test
    void should_not_be_able_to_change_bank_account_after_add() {
        var accountNumber = "GB36BARC20038032622823";
        var balance = 40;
        var bankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        var expectedBankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        repository.add(bankAccount);

        bankAccount.deposit(TransactionAmount.of(60));

        verifyThat(repository).shouldContain(expectedBankAccount);
    }

    @Test
    void should_not_be_able_to_change_bank_account_after_find() {
        var accountNumber = "GB36BARC20038032622823";
        var balance = 40;
        var bankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        var expectedBankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        repository.add(bankAccount);

        var retrievedBankAccount = repository.find(AccountNumber.of(accountNumber)).get();

        retrievedBankAccount.deposit(TransactionAmount.of(20));

        verifyThat(repository).shouldContain(expectedBankAccount);
    }

    @Test
    void should_not_be_able_to_change_bank_account_after_update() {
        var accountNumber = "GB36BARC20038032622823";
        var balance = 40;
        var bankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        var expectedBankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        repository.add(bankAccount);

        var retrievedBankAccount = verifyThat(repository).shouldContain(accountNumber);

        repository.update(retrievedBankAccount);

        retrievedBankAccount.deposit(TransactionAmount.of(10));

        verifyThat(repository).shouldContain(expectedBankAccount);
    }


    @Test
    void should_throw_exception_when_attempt_add_bank_account_with_same_account_number_twice() {
        var accountNumber = "GB36BARC20038032622823";
        var balance = 40;
        var balance2 = 60;

        var bankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance)
                .build();

        var bankAccount2 = bankAccount()
                .withAccountNumber(accountNumber)
                .withBalance(balance2)
                .build();

        repository.add(bankAccount);

        verifyThat(() -> repository.add(bankAccount2)).shouldThrowRepositoryException(RepositoryMessages.REPOSITORY_CONSTRAINT_VIOLATION);
    }

    @Test
    void should_throw_exception_when_attempt_to_update_non_existent_bank_account() {
        var accountNumber = "GB36BARC20038032622823";

        var bankAccount = bankAccount()
                .withAccountNumber(accountNumber)
                .build();

        verifyThat(() -> repository.update(bankAccount)).shouldThrowRepositoryException(RepositoryMessages.REPOSITORY_CANNOT_UPDATE_NON_EXISTENT);
    }
}