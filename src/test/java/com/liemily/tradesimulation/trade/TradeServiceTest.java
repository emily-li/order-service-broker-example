package com.liemily.tradesimulation.trade;

import com.liemily.tradesimulation.stock.Stock;
import com.liemily.tradesimulation.stock.StockRepository;
import com.liemily.tradesimulation.stock.StockServiceTest;
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
    private static final Logger logger = LogManager.getLogger(StockServiceTest.class);

    @Autowired
    private TradeService tradeService;
    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private StockRepository stockRepository;

    private Trade trade;
    private Stock stock;

    private String username = "user";
    private String stockSymbol = "SYM";
    private int volume;

    @Before
    public void setup() throws Exception {
        stockSymbol += UUID.randomUUID();
        stock = new Stock(stockSymbol, new BigDecimal(1.5), 1);
        stockRepository.save(stock);
    }

    @After
    public void tearDown() throws Exception {
        try {
            tradeRepository.delete(trade);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete trade " + trade.getOrderId() + " but it was not present in the database");
        }
        try {
            stockRepository.delete(stock);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete stock " + stockSymbol + " but it was not present in the database");
        }
    }

    @Test
    public void testProcessInvalidBuyDueToInsufficientStock() throws Exception {
        volume = 10;
        trade = new Trade(username, stockSymbol, volume, Trade.TradeType.BUY);
        trade = tradeService.process(trade);
        assertEquals(0, trade.getOrderId());
    }

    @Test
    public void testProcessValidBuy() throws Exception {
        volume = 1;
        trade = new Trade(username, stockSymbol, volume, Trade.TradeType.BUY);
        trade = tradeService.process(trade);
        assertTrue(trade.getOrderId() > 0);
    }
}
