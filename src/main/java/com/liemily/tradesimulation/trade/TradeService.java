package com.liemily.tradesimulation.trade;

import com.liemily.tradesimulation.broker.Broker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Created by Emily Li on 12/08/2017.
 */
@Component
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
     * @param trade Trade object with user, stock, trade type and trade volume information populated.
     *              The trade ID is expected to be populated by the caller when persisted.
     * @return Returns Trade object with id
     */
    Trade process(Trade trade) {
        boolean success = broker.process(trade);
        if (success) {
            trade = save(trade);
        }
        return trade;
    }

    private Trade save(Trade trade) {
        return tradeRepository.save(trade);
    }
}
