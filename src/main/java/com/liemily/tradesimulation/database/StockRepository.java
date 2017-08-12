package com.liemily.tradesimulation.database;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Emily Li on 23/07/2017.
 */
public interface StockRepository extends JpaRepository<Stock, String> {
}
