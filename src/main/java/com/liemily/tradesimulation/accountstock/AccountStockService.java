package com.liemily.tradesimulation.accountstock;

import com.liemily.tradesimulation.accountstock.exceptions.InsufficientAccountStockException;
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
    public void addStock(String username, String stockSymbol, int volume) {
        boolean success = accountStockRepository.add(username, stockSymbol, volume) > 0;
        if (!success) {
            accountStockRepository.save(new AccountStock(username, stockSymbol, volume));
        }
    }

    @Transactional
    public void removeStock(String username, String stockSymbol, int volume) throws InsufficientAccountStockException {
        int affectedRows = accountStockRepository.remove(username, stockSymbol, volume);
        if (affectedRows <= 0) {
            throw new InsufficientAccountStockException("Failed to remove " + volume + " " + stockSymbol + " stocks from user " + username + " due to insufficient stock");
        }
    }
}
