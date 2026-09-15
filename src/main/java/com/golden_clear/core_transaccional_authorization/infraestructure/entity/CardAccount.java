package com.golden_clear.core_transaccional_authorization.infraestructure.entity;

import com.golden_clear.core_transaccional_authorization.shared.enums.CardStatus;
import com.golden_clear.core_transaccional_authorization.shared.enums.CardType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "card_account")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardAccount {

    @Id
    @Column(name = "card_token", length = 64)
    private String cardToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_status", length = 20, nullable = false)
    private CardStatus cardStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", length = 10, nullable = false)
    private CardType cardType;

    @Column(name = "expiration_date", length = 4, nullable = false)
    private String expirationDate;

    @Column(name = "available_balance", precision = 18, scale = 2, nullable = false)
    private BigDecimal availableBalance;

    @Column(name = "credit_limit", precision = 18, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "daily_limit", precision = 18, scale = 2)
    private BigDecimal dailyLimit;

    @Column(name = "daily_spent", precision = 18, scale = 2, nullable = false)
    private BigDecimal dailySpent;

    @Column(name = "daily_spent_reset_at", nullable = false)
    private LocalDate dailySpentResetAt;

    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "issuer_bin", length = 11)
    private String issuerBin;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
