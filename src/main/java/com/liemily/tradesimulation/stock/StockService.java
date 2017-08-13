package com.liemily.tradesimulation.stock;

import com.liemily.tradesimulation.stock.exceptions.InsufficientStockException;
import com.liemily.tradesimulation.stock.exceptions.InvalidStockException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Emily Li on 23/07/2017.
 */
@Service
@Lazy
public class StockService {
    private static final Logger logger = LogManager.getLogger(StockService.class);

    private StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void remove(String stockSymbol, int volume) throws InsufficientStockException {
        boolean success = stockRepository.remove(stockSymbol, volume) > 0;
        if (!success) {
            throw new InsufficientStockException("Failed to withdraw " + volume + " " + stockSymbol + " stocks due to insufficient volume");
        }
    }

    @Transactional
    public void add(String stockSymbol, int volume) throws InvalidStockException {
        boolean success = stockRepository.add(stockSymbol, volume) > 0;
        if (!success) {
            throw new InvalidStockException("Failed to add stock for stock symbol " + stockSymbol);
        }
    }

    @Transactional(readOnly = true)
    public Stock getStock(String stockSymbol) throws InvalidStockException {
        Stock availableStock = stockRepository.findOne(stockSymbol);
        if (availableStock == null) {
            throw new InvalidStockException("No such stock with stock symbol " + stockSymbol);
        }
        return availableStock;
    }
}
