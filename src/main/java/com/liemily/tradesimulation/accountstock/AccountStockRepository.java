package com.liemily.tradesimulation.accountstock;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Emily Li on 12/08/2017.
 */
public interface AccountStockRepository extends JpaRepository<AccountStock, AccountStockId> {
}
