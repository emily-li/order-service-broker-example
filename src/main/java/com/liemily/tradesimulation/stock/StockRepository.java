package com.liemily.tradesimulation.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Emily Li on 23/07/2017.
 */
public interface StockRepository extends JpaRepository<Stock, String> {
    @Modifying
    @Query("UPDATE Stock SET volume = volume - ?2 WHERE symbol = ?1 AND volume - ?2 >= 0")
    int withdraw(String stockSymbol, int volume);
}
