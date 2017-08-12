package com.liemily.tradesimulation.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

/**
 * Created by Emily Li on 12/08/2017.
 */
public interface AccountRepository extends JpaRepository<Account, String> {
    @Modifying
    @Query("UPDATE Account SET credits = credits - ?2 WHERE username = ?1 AND credits - ?2 >= 0")
    int removeCredits(String username, BigDecimal credits);
}
