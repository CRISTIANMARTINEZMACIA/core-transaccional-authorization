package com.golden_clear.core_transaccional_authorization.service.impl;

import com.golden_clear.core_transaccional_authorization.dto.request.CardNetworkAuthorizationRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.CardNetworkAuthorizationResponse;
import com.golden_clear.core_transaccional_authorization.infraestructure.entity.CardAccount;
import com.golden_clear.core_transaccional_authorization.infraestructure.repository.CardAccountRepository;
import com.golden_clear.core_transaccional_authorization.service.CardAccountService;
import com.golden_clear.core_transaccional_authorization.service.CardNetworkService;
import com.golden_clear.core_transaccional_authorization.shared.enums.CardStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class CardNetworkSimulatorService implements CardNetworkService {

    private static final String RESPONSE_CODE_APPROVED = "00";
    private static final String RESPONSE_CODE_INVALID_CARD = "14";
    private static final String RESPONSE_CODE_INSUFFICIENT_FUNDS = "51";
    private static final String RESPONSE_CODE_EXPIRED_CARD = "54";
    private static final String RESPONSE_CODE_EXCEEDS_DAILY_LIMIT = "61";
    private static final String RESPONSE_CODE_RESTRICTED_CARD = "62";

    private static final int MIN_LATENCY_MS = 50;
    private static final int MAX_LATENCY_MS = 300;

    private final CardAccountRepository cardAccountRepository;

    @Override
    @Transactional
    public CardNetworkAuthorizationResponse authorize(CardNetworkAuthorizationRequest request) {
        simulateNetworkLatency();

        Optional<CardAccount> cardAccountOptional = cardAccountRepository.findById(request.cardToken());

        if (cardAccountOptional.isEmpty()) {
            return new CardNetworkAuthorizationResponse(RESPONSE_CODE_INVALID_CARD, null);
        }

        CardAccount cardAccount = cardAccountOptional.get();

        if (cardAccount.getCardStatus() != CardStatus.ACTIVE) {
            return new CardNetworkAuthorizationResponse(RESPONSE_CODE_RESTRICTED_CARD, null);
        }

        if (isExpired(cardAccount.getExpirationDate())) {
            return new CardNetworkAuthorizationResponse(RESPONSE_CODE_EXPIRED_CARD, null);
        }

        resetDailySpentIfNewDay(cardAccount);

        if (cardAccount.getDailyLimit() != null
                && cardAccount.getDailySpent().add(request.amount()).compareTo(cardAccount.getDailyLimit()) > 0) {
            return new CardNetworkAuthorizationResponse(RESPONSE_CODE_EXCEEDS_DAILY_LIMIT, null);
        }

        if (cardAccount.getAvailableBalance().compareTo(request.amount()) < 0) {
            return new CardNetworkAuthorizationResponse(RESPONSE_CODE_INSUFFICIENT_FUNDS, null);
        }

        String authorizationCode = generateAuthorizationCode();
        return new CardNetworkAuthorizationResponse(RESPONSE_CODE_APPROVED, authorizationCode);
    }

    private boolean isExpired(String expirationDateMMYY) {
        int month = Integer.parseInt(expirationDateMMYY.substring(0, 2));
        int year = 2000 + Integer.parseInt(expirationDateMMYY.substring(2, 4));
        return YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    private void resetDailySpentIfNewDay(CardAccount cardAccount) {
        if (cardAccount.getDailySpentResetAt().isBefore(LocalDate.now())) {
            cardAccount.setDailySpent(BigDecimal.ZERO);
            cardAccount.setDailySpentResetAt(LocalDate.now());
        }
    }

    private String generateAuthorizationCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }

    private void simulateNetworkLatency() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(MIN_LATENCY_MS, MAX_LATENCY_MS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
