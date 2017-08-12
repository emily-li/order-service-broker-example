package com.liemily.tradesimulation.broker;

import com.liemily.tradesimulation.account.AccountService;
import com.liemily.tradesimulation.account.exceptions.InsufficientFundsException;
import com.liemily.tradesimulation.stock.Stock;
import com.liemily.tradesimulation.stock.StockService;
import com.liemily.tradesimulation.stock.exceptions.InsufficientStockException;
import com.liemily.tradesimulation.stock.exceptions.InvalidStockException;
import com.liemily.tradesimulation.trade.Trade;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by Emily Li on 12/08/2017.
 */
@Component
@Lazy
public class Broker {
    private static final Logger logger = LogManager.getLogger(Broker.class);
    private StockService stockService;
    private AccountService accountService;

    @Autowired
    public Broker(StockService stockService, AccountService accountService) {
        this.stockService = stockService;
        this.accountService = accountService;
    }

    public boolean process(Trade trade) throws InvalidStockException, InsufficientFundsException, InsufficientStockException {
        BigDecimal requiredCredits = calculateRequiredCredits(trade.getStockSymbol(), trade.getVolume());
        boolean withdrawCreditsSuccess = withdrawCredits(trade.getUsername(), requiredCredits);
        if (!withdrawCreditsSuccess) {
            throw new InsufficientFundsException("Unable to withdraw credits from user " + trade.getUsername());
        } else {
            return buy(trade);
        }
    }

    private boolean buy(Trade trade) throws InsufficientStockException {
        return stockService.withdraw(trade.getStockSymbol(), trade.getVolume());
    }

    private boolean withdrawCredits(String username, BigDecimal credits) throws InsufficientFundsException {
        boolean success = accountService.removeCredits(username, credits);
        logger.info("Success status removing " + credits + " credits from account " + username + " was: " + success);
        return success;
    }

    private BigDecimal calculateRequiredCredits(String stockSymbol, int volume) throws InvalidStockException {
        Stock availableStock = stockService.getStock(stockSymbol);
        if (availableStock == null) {
            throw new InvalidStockException("No available stock with stock symbol " + stockSymbol);
        }
        return availableStock.getValue().multiply(new BigDecimal(volume));
    }
}
