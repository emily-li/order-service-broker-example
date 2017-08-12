package com.liemily.tradesimulation.broker;

import com.liemily.tradesimulation.stock.StockService;
import com.liemily.tradesimulation.trade.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by Emily Li on 12/08/2017.
 */
@Component
@Lazy
public class Broker {
    private StockService stockService;

    @Autowired
    public Broker(StockService stockService) {
        this.stockService = stockService;
    }

    public boolean process(Trade trade) {
        return buy(trade);
    }

    private boolean buy(Trade trade) {
        return stockService.withdraw(trade.getStockSymbol(), trade.getVolume());
    }
}
