package com.golden_clear.core_transaccional_authorization.infraestructure.repository;

import com.golden_clear.core_transaccional_authorization.infraestructure.entity.CardAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardAccountRepository extends JpaRepository<CardAccount, String> {

}
