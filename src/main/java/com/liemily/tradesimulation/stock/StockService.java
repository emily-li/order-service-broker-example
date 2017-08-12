package com.liemily.tradesimulation.stock;

import com.liemily.tradesimulation.stock.exceptions.InsufficientStockException;
import com.liemily.tradesimulation.stock.exceptions.InvalidStockException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Emily Li on 23/07/2017.
 */
@Service
public class StockService {
    private static final Logger logger = LogManager.getLogger(StockService.class);

    private StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public boolean withdraw(String stockSymbol, int volume) throws InsufficientStockException {
        boolean success = stockRepository.withdraw(stockSymbol, volume) > 0;
        if (!success) {
            throw new InsufficientStockException("Failed to withdraw " + volume + " " + stockSymbol + " stocks due to insufficient volume");
        }
        return success;
    }

    @Transactional(readOnly = true)
    public Stock getStock(String stockSymbol) throws InvalidStockException {
        Stock availableStock = stockRepository.findOne(stockSymbol);
        if (availableStock == null) {
            throw new InvalidStockException("No available stock with stock symbol " + stockSymbol);
        }
        return availableStock;
    }
}
