package com.liemily.tradesimulation.database;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Created by Emily Li on 12/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StockServiceTest {
    private static final Logger logger = LogManager.getLogger(StockServiceTest.class);

    @Autowired
    private StockService stockService;
    private String stockSymbol;

    @Before
    public void setup() {
        stockSymbol = "SYM" + UUID.randomUUID();
    }

    @After
    public void tearDown() {
        try {
            stockService.delete(stockSymbol);
        } catch (EmptyResultDataAccessException e) {
            logger.info("Attempted to delete stock " + stockSymbol + " but it was not present in the database");
        }
    }

    @Test
    public void testWriteStock() {
        Stock stock = new Stock(stockSymbol, new BigDecimal(1.5), 1);
        stockService.save(stock);
        Stock foundStock = stockService.findOne(stockSymbol);
        Assert.assertEquals(stock, foundStock);
    }

    @Test
    public void testDeleteStock() {
        Stock stock = new Stock(stockSymbol, new BigDecimal(1.5), 1);
        stockService.save(stock);
        Stock foundStock = stockService.findOne(stockSymbol);
        Assert.assertEquals(stock, foundStock);
        stockService.delete(stockSymbol);
        Stock deletedStock = stockService.findOne(stockSymbol);
        Assert.assertNull(deletedStock);
    }
}
