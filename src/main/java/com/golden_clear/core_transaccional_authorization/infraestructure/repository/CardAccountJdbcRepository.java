package com.golden_clear.core_transaccional_authorization.infraestructure.repository;

import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

public class CardAccountJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public CardAccountJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void debitCardBalance(String cardToken, String transactionId,
                                        String merchantId, BigDecimal amount, String currency) {
       jdbcTemplate.execute((Connection connection) -> {
            try (CallableStatement cs = connection.prepareCall(
                    "{call sp_debit_card_balance(?, ?, ?, ?, ?, ?, ?, ?)}")) {

                cs.setString(1, cardToken);
                cs.setString(2, transactionId);
                cs.setString(3, merchantId);
                cs.setBigDecimal(4, amount);
                cs.setString(5, currency);
                cs.registerOutParameter(6, Types.BOOLEAN);
                cs.registerOutParameter(7, Types.VARCHAR);
                cs.registerOutParameter(8, Types.NUMERIC);

                cs.execute();

                return 0;
            }
        });
    }

}
