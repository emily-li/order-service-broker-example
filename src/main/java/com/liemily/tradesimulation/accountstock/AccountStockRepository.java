package com.liemily.tradesimulation.accountstock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Emily Li on 12/08/2017.
 */
public interface AccountStockRepository extends JpaRepository<AccountStock, AccountStockId> {
    @Modifying
    @Query("UPDATE AccountStock SET volume = volume + ?3 WHERE username = ?1 AND stock_symbol = ?2")
    int add(String username, String stockSymbol, int volume);

    @Modifying
    @Query("UPDATE AccountStock SET volume = volume - ?3 WHERE username = ?1 AND stock_symbol = ?2 AND volume - ?3 >= 0")
    int remove(String username, String stockSymbol, int volume);
}
