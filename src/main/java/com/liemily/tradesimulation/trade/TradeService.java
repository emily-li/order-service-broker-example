package com.liemily.tradesimulation.trade;

import com.liemily.tradesimulation.account.exceptions.InsufficientFundsException;
import com.liemily.tradesimulation.broker.Broker;
import com.liemily.tradesimulation.stock.exceptions.InsufficientStockException;
import com.liemily.tradesimulation.stock.exceptions.InvalidStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Created by Emily Li on 12/08/2017.
 */
@Service
@Lazy
public class TradeService {
    private Broker broker;
    private TradeRepository tradeRepository;

    @Autowired
    public TradeService(Broker broker, TradeRepository tradeRepository) {
        this.broker = broker;
        this.tradeRepository = tradeRepository;
    }

    /**
     * Takes an trade and submits it to the Broker for processing
     *
     * @param trade Trade object with account, stock, trade type and trade volume information populated.
     *              The trade ID is expected to be populated by the caller when persisted.
     * @return Returns saved id
     */
    public long process(Trade trade) throws InvalidStockException, InsufficientFundsException, InsufficientStockException {
        broker.process(trade);
        return tradeRepository.save(trade).getTradeId();
    }
}
