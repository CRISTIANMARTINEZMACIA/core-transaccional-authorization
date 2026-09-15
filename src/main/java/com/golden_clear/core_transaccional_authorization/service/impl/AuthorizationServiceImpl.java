package com.golden_clear.core_transaccional_authorization.service.impl;

import com.golden_clear.core_transaccional_authorization.dto.request.AuthorizationTransaccionalRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.AuthorizationTransaccionalResponse;
import com.golden_clear.core_transaccional_authorization.infraestructure.entity.AuthorizationTransaccional;
import com.golden_clear.core_transaccional_authorization.infraestructure.repository.AuthorizationTransaccionalRepository;
import com.golden_clear.core_transaccional_authorization.service.AuthorizationService;
import com.golden_clear.core_transaccional_authorization.service.CardNetworkService;
import com.golden_clear.core_transaccional_authorization.dto.request.CardNetworkAuthorizationRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.CardNetworkAuthorizationResponse;
import com.golden_clear.core_transaccional_authorization.service.TransactionEventPublisherService;
import com.golden_clear.core_transaccional_authorization.shared.exception.AuthorizationNotFoundException;
import com.golden_clear.core_transaccional_authorization.shared.mappers.AuthorizationMapper;
import com.golden_clear.core_transaccional_authorization.shared.util.CardTokenGenerator;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthorizationServiceImpl implements AuthorizationService {

    private final AuthorizationTransaccionalRepository repository;
    private final CardNetworkService cardNetworkService;
    private final TransactionEventPublisherService transactionEventPublisherService;
    private final AuthorizationMapper mapper;

    @Override
    @Transactional
    @CacheEvict(value = "cardAuthorization")
    public AuthorizationTransaccionalResponse authorize(AuthorizationTransaccionalRequest request) {
        CardNetworkAuthorizationRequest networkRequest = new CardNetworkAuthorizationRequest(
                CardTokenGenerator.generate(request.pan()),
                request.pan(),
                request.expirationDate(),
                request.cvv(),
                request.emvCryptogram(),
                request.merchantId(),
                request.terminalId(),
                request.amount(),
                request.currency(),
                request.stan()
        );


        CardNetworkAuthorizationResponse networkResponse = cardNetworkService.authorize(networkRequest);

        AuthorizationTransaccional entity = mapper.toEntity(request, networkResponse);
        AuthorizationTransaccional saved = repository.save(entity);

        transactionEventPublisherService.publish(mapper.toTransactionEventResponse(saved));

        return mapper.toResponse(saved);
    }



    @Override
    @Cacheable(value = "cardAuthorization", key = "#transactionId")
    public AuthorizationTransaccionalResponse findByTransactionId(String transactionId) {
        AuthorizationTransaccional entity = repository.findById(transactionId)
                .orElseThrow(() -> new AuthorizationNotFoundException(transactionId));
        return mapper.toResponse(entity);
    }
}
