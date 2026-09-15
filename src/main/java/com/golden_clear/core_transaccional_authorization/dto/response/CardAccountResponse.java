package com.golden_clear.core_transaccional_authorization.dto.response;

import com.golden_clear.core_transaccional_authorization.shared.enums.CardStatus;
import com.golden_clear.core_transaccional_authorization.shared.enums.CardType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardAccountResponse(
      String cardToken,
      CardStatus cardStatus,
      CardType cardType,
      String expirationDate,
      BigDecimal availableBalance,
      BigDecimal creditLimit,
      BigDecimal dailyLimit,
      BigDecimal dailySpent,
      LocalDate dailySpentResetAt,
      String currency,
      String issuerBin
) {
}
