package com.golden_clear.core_transaccional_authorization.service;

import com.golden_clear.core_transaccional_authorization.infraestructure.entity.CardAccount;


import java.util.Optional;

public interface CardAccountService {

    Optional<CardAccount> getCardAccount(String cardToken);

}
