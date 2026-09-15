package com.golden_clear.core_transaccional_authorization.shared.mappers;

import com.golden_clear.core_transaccional_authorization.dto.request.AuthorizationTransaccionalRequest;
import com.golden_clear.core_transaccional_authorization.dto.response.AuthorizationTransaccionalResponse;
import com.golden_clear.core_transaccional_authorization.dto.response.CardNetworkAuthorizationResponse;
import com.golden_clear.core_transaccional_authorization.dto.response.TransactionEvent;
import com.golden_clear.core_transaccional_authorization.infraestructure.entity.AuthorizationTransaccional;
import com.golden_clear.core_transaccional_authorization.shared.enums.AuthorizationStatus;
import com.golden_clear.core_transaccional_authorization.shared.util.CardTokenGenerator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        imports = {AuthorizationStatus.class, AuthorizationMappingSupport.class, CardTokenGenerator.class})
public interface AuthorizationMapper {

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "cardToken", expression = "java(CardTokenGenerator.generate(request.pan()))")
    @Mapping(target = "panMasked", expression = "java(AuthorizationMappingSupport.maskPan(request.pan()))")
    @Mapping(target = "terminalId", source = "request.terminalId")
    @Mapping(target = "merchantId", source = "request.merchantId")
    @Mapping(target = "amount", source = "request.amount")
    @Mapping(target = "currency", source = "request.currency")
    @Mapping(target = "stan", source = "request.stan")
    @Mapping(target = "transmissionDateTime", source = "request.transmissionDateTime")
    @Mapping(target = "posEntryMode", source = "request.posEntryMode")
    @Mapping(target = "responseCode", source = "networkResponse.responseCode")
    @Mapping(target = "authorizationCode", source = "networkResponse.authorizationCode")
    AuthorizationTransaccional toEntity(AuthorizationTransaccionalRequest request,
                                         CardNetworkAuthorizationResponse networkResponse);

    @Mapping(target = "status", expression = "java(resolveStatus(entity.getResponseCode()))")
    @Mapping(target = "approvedAmount",
            expression = "java(resolveStatus(entity.getResponseCode()) == AuthorizationStatus.APPROVED ? entity.getAmount() : null)")
    @Mapping(target = "declineReason",
            expression = "java(resolveStatus(entity.getResponseCode()) == AuthorizationStatus.APPROVED ? null : AuthorizationMappingSupport.describeResponseCode(entity.getResponseCode()))")
    AuthorizationTransaccionalResponse toResponse(AuthorizationTransaccional entity);

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "eventType", expression = "java(resolveStatus(entity.getResponseCode()).name())")
    @Mapping(target = "cardTokenMasked", source = "panMasked")
    @Mapping(target = "occurredAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "schemaVersion", constant = "1")
    TransactionEvent toTransactionEventResponse(AuthorizationTransaccional entity);

    /**
     * Único método (String -> AuthorizationStatus) del mapper: al no competir con otro de
     * igual firma, MapStruct puede usarlo sin ambigüedad, a diferencia de los helpers
     * String -> String que viven en {@link AuthorizationMappingSupport}.
     */
    default AuthorizationStatus resolveStatus(String responseCode) {
        return AuthorizationMappingSupport.resolveStatus(responseCode);
    }
}
