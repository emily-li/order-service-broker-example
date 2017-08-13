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
import static org.junit.Assert.assertTrue;

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

    private Trade trade;
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
            tradeRepository.delete(trade.getTradeId());
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete trade " + trade.getTradeId() + " but it was not present in the database");
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
    public void testProcessInvalidBuyDueToInsufficientStock() throws Exception {
        volume = 10;
        trade = new Trade(username, stockSymbol, volume, Trade.TradeType.BUY);
        trade = tradeService.process(trade);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testProcessInvalidBuyDueToInsufficientCredits() throws Exception {
        volume = 100;
        trade = new Trade(username, stockSymbol, volume, Trade.TradeType.BUY);
        trade = tradeService.process(trade);
    }

    @Test
    public void testProcessValidBuy() throws Exception {
        volume = 1;
        trade = new Trade(username, stockSymbol, volume, Trade.TradeType.BUY);
        trade = tradeService.process(trade);

        AccountStock accountStock = accountStockService.getAccountStockForUser(username, stockSymbol);
        assertEquals(trade.getUsername(), accountStock.getUsername());
        assertEquals(trade.getStockSymbol(), accountStock.getStockSymbol());
        assertEquals(trade.getVolume(), accountStock.getVolume());
        assertTrue(trade.getTradeId() > 0);
    }
}
