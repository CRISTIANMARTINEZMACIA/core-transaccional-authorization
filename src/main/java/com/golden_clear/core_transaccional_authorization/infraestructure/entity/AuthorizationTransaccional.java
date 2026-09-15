package com.golden_clear.core_transaccional_authorization.infraestructure.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transacciones_autorizadas")
public class AuthorizationTransaccional {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "codigo_transaccion")
    private String transactionId;

    @Column(name = "toke_tarjeta")
    private String cardToken;

    @Column(name = "pan_enmascarado")
    private String panMasked;

    @Column(name = "codigo_terminal")
    private String terminalId;

    @Column(name = "codigo_comerciante")
    private String merchantId;

    @Column(name = "cantidad")
    private BigDecimal amount;

    @Column(name = "moneda")
    private String currency;

    @Column(name = "stan")
    private String stan;

    @Column(name = "fecha_transmission")
    private Instant transmissionDateTime;

    @Column(name = "modo_ingreso")
    private String posEntryMode;

    @Column(name = "codigo_respuesta")
    private String responseCode;

    @Column(name = "codigo_authorizacion")
    private String authorizationCode;
}
