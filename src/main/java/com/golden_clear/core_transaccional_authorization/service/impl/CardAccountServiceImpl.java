package com.golden_clear.core_transaccional_authorization.service.impl;

import com.golden_clear.core_transaccional_authorization.infraestructure.entity.CardAccount;
import com.golden_clear.core_transaccional_authorization.infraestructure.repository.CardAccountRepository;
import com.golden_clear.core_transaccional_authorization.service.CardAccountService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardAccountServiceImpl implements CardAccountService {

    private final CardAccountRepository cardAccountRepository;

    public CardAccountServiceImpl(CardAccountRepository cardAccountRepository){
        this.cardAccountRepository = cardAccountRepository;
    }

    @Override
    @Cacheable(value = "cardAuthorization", key = "#cardToken")
    public Optional<CardAccount> getCardAccount(String cardToken) {
        return cardAccountRepository.findById(cardToken);
    }
}
