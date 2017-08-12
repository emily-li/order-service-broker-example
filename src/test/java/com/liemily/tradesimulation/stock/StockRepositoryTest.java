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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by Emily Li on 12/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StockRepositoryTest {
    private static final Logger logger = LogManager.getLogger(StockServiceTest.class);

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
    public void testWriteStock() {
        Stock foundStock = stockRepository.findOne(stockSymbol);
        assertEquals(stock, foundStock);
    }

    @Test
    public void testDeleteStock() {
        Stock foundStock = stockRepository.findOne(stockSymbol);
        assertEquals(stock, foundStock);
        stockRepository.delete(stockSymbol);
        Stock deletedStock = stockRepository.findOne(stockSymbol);
        assertNull(deletedStock);
    }

    @Test
    public void testUpdateStock() {
        stock = new Stock(stockSymbol, new BigDecimal(2.0), 2);
        stockRepository.save(stock);

        Stock foundStock = stockRepository.findOne(stockSymbol);
        assertEquals(stock, foundStock);
    }

    @Test
    public void testGetStocks() {
        Stock stock2 = new Stock(stockSymbol + "2", new BigDecimal(2), 2);

        stockRepository.save(stock2);

        Collection<Stock> writtenStocks = new HashSet<>();
        writtenStocks.add(stock);
        writtenStocks.add(stock2);

        List<Stock> foundStocks = stockRepository.findAll();
        assertTrue(foundStocks.containsAll(writtenStocks));
        assertTrue(writtenStocks.containsAll(foundStocks));
    }
}
