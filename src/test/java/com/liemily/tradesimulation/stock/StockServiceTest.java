package com.liemily.tradesimulation.stock;

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

import static org.junit.Assert.*;

/**
 * Created by Emily Li on 12/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StockServiceTest {
    private static final Logger logger = LogManager.getLogger(StockServiceTest.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    private String stockSymbol;
    private Stock stock;

    @Before
    public void setup() {
        stockSymbol = "SYM" + UUID.randomUUID();
        stock = new Stock(stockSymbol, new BigDecimal(1.5), 1);
        stockRepository.save(stock);
    }

    @After
    public void tearDown() {
        try {
            stockRepository.delete(stockSymbol);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete stock " + stockSymbol + " but it was not present in the stock");
        }
    }

    @Test
    public void testWithdrawAvailableStock() {
        boolean success = stockService.withdraw(stockSymbol, 1);

        Stock updatedStock = stockRepository.findOne(stockSymbol);
        assertEquals(0, updatedStock.getVolume());
        assertTrue(success);
    }

    @Test
    public void testWithdrawUnavailableStock() {
        boolean success = stockService.withdraw(stockSymbol, 2);

        Stock updatedStock = stockRepository.findOne(stockSymbol);
        assertEquals(1, updatedStock.getVolume());
        assertFalse(success);
    }
}
