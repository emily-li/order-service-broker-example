package com.liemily.tradesimulation.trade;

import com.liemily.tradesimulation.account.Account;
import com.liemily.tradesimulation.account.AccountRepository;
import com.liemily.tradesimulation.account.exceptions.InsufficientFundsException;
import com.liemily.tradesimulation.accountstock.AccountStock;
import com.liemily.tradesimulation.accountstock.AccountStockRepository;
import com.liemily.tradesimulation.accountstock.AccountStockService;
import com.liemily.tradesimulation.stock.Stock;
import com.liemily.tradesimulation.stock.StockRepository;
import com.liemily.tradesimulation.stock.exceptions.InsufficientStockException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Emily Li on 12/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeServiceTest {
    private static final Logger logger = LogManager.getLogger(TradeServiceTest.class);

    @Autowired
    private TradeService tradeService;
    @Autowired
    private AccountStockService accountStockService;

    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountStockRepository accountStockRepository;

    private long tradeId;
    private Stock stock;
    private int volume;

    private String username = "user";
    private String stockSymbol = "SYM";
    private Account account = new Account(username, new BigDecimal(20));

    @Before
    public void setup() throws Exception {
        accountRepository.save(account);

        stockSymbol += UUID.randomUUID();
        stock = new Stock(stockSymbol, new BigDecimal(1.5), 1);
        stockRepository.save(stock);
    }

    @After
    public void tearDown() throws Exception {
        try {
            tradeRepository.delete(tradeId);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete trade " + tradeId + " but it was not present in the database");
        }
        try {
            stockRepository.delete(stock);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete stock " + stockSymbol + " but it was not present in the database");
        }
        try {
            accountRepository.delete(account);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete stock " + stockSymbol + " but it was not present in the database");
        }
    }

    @Test(expected = InsufficientStockException.class)
    public void testProcessInvalidTradeDueToInsufficientStock() throws Exception {
        volume = 10;
        for (Trade.TradeType tradeType : Trade.TradeType.values()) {
            Trade trade = new Trade(username, stockSymbol, volume, tradeType);
            tradeId = tradeService.process(trade);
        }
    }

    @Test(expected = InsufficientFundsException.class)
    public void testProcessInvalidBuyDueToInsufficientCredits() throws Exception {
        volume = 100;
        Trade trade = new Trade(username, stockSymbol, volume, Trade.TradeType.BUY);
        tradeId = tradeService.process(trade);
    }

    @Test
    public void testProcessValidBuy() throws Exception {
        volume = 1;
        Trade trade = new Trade(username, stockSymbol, volume, Trade.TradeType.BUY);
        tradeId = tradeService.process(trade);

        Trade writtenTrade = tradeRepository.findOne(tradeId);
        assertNotNull(writtenTrade);

        AccountStock accountStock = accountStockService.getAccountStockForUser(username, stockSymbol);
        assertEquals(trade.getUsername(), accountStock.getUsername());
        assertEquals(trade.getStockSymbol(), accountStock.getStockSymbol());
        assertEquals(trade.getVolume(), accountStock.getVolume());
    }

    @Test
    public void testProcessValidSell() throws Exception {
        volume = 1;
        accountStockService.addStock(username, stockSymbol, volume);
        Trade trade = new Trade(username, stockSymbol, volume, Trade.TradeType.SELL);
        long tradeId = tradeService.process(trade);

        Trade writtenTrade = tradeRepository.findOne(tradeId);
        assertNotNull(writtenTrade);

        AccountStock accountStock = accountStockService.getAccountStockForUser(username, stockSymbol);
        // Depending on implementation, AccountStock may or may not exist in database once volume for stock is 0
        if (accountStock != null) {
            assertEquals(trade.getUsername(), accountStock.getUsername());
            assertEquals(trade.getStockSymbol(), accountStock.getStockSymbol());
            assertEquals(0, accountStock.getVolume());
        }

        // Assert stock is back on market
        Stock stock = stockRepository.findOne(stockSymbol);
        assertEquals(2, stock.getVolume());
    }
}
