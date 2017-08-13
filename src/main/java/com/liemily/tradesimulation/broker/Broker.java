package com.liemily.tradesimulation.broker;

import com.liemily.tradesimulation.account.AccountService;
import com.liemily.tradesimulation.account.exceptions.InsufficientFundsException;
import com.liemily.tradesimulation.accountstock.AccountStockService;
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
import org.springframework.transaction.annotation.Transactional;

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
    private AccountStockService accountStockService;

    @Autowired
    public Broker(StockService stockService, AccountService accountService, AccountStockService accountStockService) {
        this.stockService = stockService;
        this.accountService = accountService;
        this.accountStockService = accountStockService;
    }

    @Transactional(rollbackFor = {InsufficientFundsException.class, InsufficientStockException.class, InvalidStockException.class})
    public void process(Trade trade) throws InsufficientFundsException, InsufficientStockException, InvalidStockException {
        Trade.TradeType tradeType = trade.getTradeType();
        if (tradeType.equals(Trade.TradeType.BUY)) {
            buy(trade);
        } else if (tradeType.equals(Trade.TradeType.SELL)) {
            sell(trade);
        }
    }

    @Transactional(rollbackFor = {InsufficientFundsException.class, InsufficientStockException.class, InvalidStockException.class})
    public void buy(Trade trade) throws InsufficientFundsException, InsufficientStockException, InvalidStockException {
        BigDecimal requiredCredits = calculateRequiredCredits(trade.getStockSymbol(), trade.getVolume());
        accountService.removeCredits(trade.getUsername(), requiredCredits);
        stockService.remove(trade.getStockSymbol(), trade.getVolume());
        accountStockService.addStock(trade.getUsername(), trade.getStockSymbol(), trade.getVolume());
    }

    @Transactional(rollbackFor = {InsufficientStockException.class, InvalidStockException.class})
    public void sell(Trade trade) throws InsufficientStockException, InvalidStockException {
        accountStockService.removeStock(trade.getUsername(), trade.getStockSymbol(), trade.getVolume());
        stockService.add(trade.getStockSymbol(), trade.getVolume());
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateRequiredCredits(String stockSymbol, int volume) throws InvalidStockException {
        Stock availableStock = stockService.getStock(stockSymbol);
        if (availableStock == null) {
            throw new InvalidStockException("No available stock with stock symbol " + stockSymbol);
        }
        return availableStock.getValue().multiply(new BigDecimal(volume));
    }
}
