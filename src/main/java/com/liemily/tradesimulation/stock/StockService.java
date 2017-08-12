package com.liemily.tradesimulation.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Emily Li on 23/07/2017.
 */
@Service
public class StockService {
    private StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public boolean withdrawStock(String stockSymbol, int volume) {
        return stockRepository.withdraw(stockSymbol, volume) > 0;
    }
}
