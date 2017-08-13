package com.liemily.tradesimulation.accountstock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Emily Li on 12/08/2017.
 */
@Service
@Lazy
public class AccountStockService {
    private AccountStockRepository accountStockRepository;

    @Autowired
    public AccountStockService(AccountStockRepository accountStockRepository) {
        this.accountStockRepository = accountStockRepository;
    }

    @Transactional(readOnly = true)
    public AccountStock getAccountStockForUser(String username, String stockSymbol) {
        AccountStockId id = new AccountStockId(username, stockSymbol);
        return accountStockRepository.findOne(id);
    }

    @Transactional
    public AccountStock registerStock(AccountStock accountStock) {
        return accountStockRepository.save(accountStock);
    }
}
