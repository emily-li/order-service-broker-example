package com.liemily.tradesimulation.account;

import com.liemily.tradesimulation.account.exceptions.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by Emily Li on 12/08/2017.
 */
@Service
@Lazy
public class AccountService {
    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public boolean removeCredits(String username, BigDecimal credits) throws InsufficientFundsException {
        boolean success = accountRepository.removeCredits(username, credits) > 0;
        if (!success) {
            throw new InsufficientFundsException("Attempted to withdraw " + credits + " but failed as user " + username + " has insufficient funds");
        }
        return success;
    }
}
