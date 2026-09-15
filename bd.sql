-- Tabla: transacciones_autorizadas
CREATE TABLE transacciones_autorizadas (
                                           codigo_transaccion      VARCHAR(36)     NOT NULL,
                                           toke_tarjeta            VARCHAR(255),
                                           pan_enmascarado         VARCHAR(255),
                                           codigo_terminal         VARCHAR(255),
                                           codigo_comerciante      VARCHAR(255),
                                           cantidad                NUMERIC(19,2),
                                           moneda                  VARCHAR(10),
                                           stan                    VARCHAR(50),
                                           fecha_transmission      TIMESTAMP,
                                           modo_ingreso            VARCHAR(50),
                                           codigo_respuesta        VARCHAR(10),
                                           codigo_authorizacion    VARCHAR(50),
                                           CONSTRAINT pk_transacciones_autorizadas PRIMARY KEY (codigo_transaccion)
);


CREATE TABLE card_account (

                              card_token              VARCHAR(64)     NOT NULL,
                              card_status              VARCHAR(20)     NOT NULL,
                              card_type                 VARCHAR(10)     NOT NULL,
                              expiration_date            CHAR(4)         NOT NULL,
                              available_balance           NUMERIC(18,2)   NOT NULL,
                              credit_limit                 NUMERIC(18,2),
                              daily_limit                    NUMERIC(18,2),
                              daily_spent                     NUMERIC(18,2)   NOT NULL DEFAULT 0,
                              daily_spent_reset_at              DATE            NOT NULL DEFAULT CURRENT_DATE,
                              currency                          CHAR(3)         NOT NULL DEFAULT 'COP',
                              issuer_bin                        VARCHAR(11),
                              created_at                          TIMESTAMPTZ     NOT NULL DEFAULT now(),
                              updated_at                          TIMESTAMPTZ     NOT NULL DEFAULT now(),

                              CONSTRAINT pk_card_account PRIMARY KEY (card_token),

                              CONSTRAINT ck_card_status
                                  CHECK (card_status IN ('ACTIVE', 'BLOCKED', 'EXPIRED', 'CANCELLED')),

                              CONSTRAINT ck_card_type
                                  CHECK (card_type IN ('CREDIT', 'DEBIT')),

                              CONSTRAINT ck_expiration_format
                                  CHECK (expiration_date ~ '^[0-9]{4}$'),

    CONSTRAINT ck_available_balance_non_negative
        CHECK (available_balance >= 0),

    CONSTRAINT ck_daily_spent_non_negative
        CHECK (daily_spent >= 0)
);

CREATE INDEX idx_card_account_status ON card_account (card_status);

CREATE INDEX idx_card_account_issuer_bin ON card_account (issuer_bin);

DELETE FROM card_account WHERE card_token LIKE 'TEST-%';

INSERT INTO card_account
(card_token, card_status, card_type, expiration_date,
 available_balance, credit_limit, daily_limit, daily_spent, currency, issuer_bin)
VALUES
    ('TEST-4111-0001', 'ACTIVE',    'CREDIT', '2812', 5000.00, 5000.00, 3000.00, 0, 'COP', '411111'),
    ('TEST-4111-0002', 'ACTIVE',    'CREDIT', '2812',    5.00, 5000.00, 3000.00, 0, 'COP', '411111'),
    ('TEST-4111-0003', 'BLOCKED',   'CREDIT', '2812', 5000.00, 5000.00, 3000.00, 0, 'COP', '411111'),
    ('TEST-4111-0005', 'ACTIVE',    'CREDIT', '2301', 5000.00, 5000.00, 3000.00, 0, 'COP', '411111'),
    ('TEST-4555-0001', 'ACTIVE',    'DEBIT',  '2812', 1200.00, NULL,     800.00, 0, 'COP', '455500'),
    ('TEST-4111-0006', 'ACTIVE',    'CREDIT', '2812', 5000.00, 5000.00,  500.00, 480.00, 'COP', '411111')
    ON CONFLICT (card_token) DO UPDATE
                                    SET card_status       = EXCLUDED.card_status,
                                    available_balance = EXCLUDED.available_balance,
                                    daily_spent        = EXCLUDED.daily_spent,
                                    updated_at          = now();

CREATE TABLE ledger_entry (

                              entry_id                BIGSERIAL       NOT NULL,
                              transaction_id            VARCHAR(64)     NOT NULL,
                              account_type                VARCHAR(30)     NOT NULL,
                              account_reference             VARCHAR(64)     NOT NULL,
                              entry_side                       VARCHAR(6)      NOT NULL,
                              amount                              NUMERIC(18,2)   NOT NULL,
                              currency                              CHAR(3)         NOT NULL,
                              description                              VARCHAR(200),
                              created_at                                  TIMESTAMPTZ     NOT NULL DEFAULT now(),

                              CONSTRAINT pk_ledger_entry PRIMARY KEY (entry_id),

                              CONSTRAINT ck_ledger_entry_side
                                  CHECK (entry_side IN ('DEBE', 'HABER')),

                              CONSTRAINT ck_ledger_entry_account_type
                                  CHECK (account_type IN ('CARD_HOLDER', 'ACQUIRER_SUSPENSE', 'MERCHANT')),

                              CONSTRAINT ck_ledger_entry_amount_positive
                                  CHECK (amount > 0)
);

CREATE INDEX idx_ledger_entry_transaction ON ledger_entry (transaction_id);

CREATE INDEX idx_ledger_entry_account ON ledger_entry (account_type, account_reference);

CREATE INDEX idx_ledger_entry_created_at ON ledger_entry (created_at);

CREATE OR REPLACE PROCEDURE sp_debit_card_balance(
    IN  p_card_token             VARCHAR,
    IN  p_transaction_id         VARCHAR,
    IN  p_merchant_id             VARCHAR,
    IN  p_amount                   NUMERIC,
    IN  p_currency                  CHAR(3),
    OUT o_success                     BOOLEAN,
    OUT o_authorization_code             VARCHAR,
    OUT o_new_available_balance           NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
v_new_balance NUMERIC;
BEGIN

UPDATE card_account
SET available_balance = available_balance - p_amount,
    daily_spent        = daily_spent + p_amount,
    version              = version + 1,
    updated_at            = now()
WHERE card_token = p_card_token
  AND card_status = 'ACTIVE'
  AND available_balance >= p_amount
  AND (daily_limit IS NULL OR (daily_spent + p_amount) <= daily_limit)
    RETURNING available_balance
INTO v_new_balance;

IF NOT FOUND THEN
        o_success := false;
        o_authorization_code := NULL;
        o_new_available_balance := NULL;
        RETURN;
END IF;

    o_authorization_code := UPPER(SUBSTRING(MD5(RANDOM()::TEXT || CLOCK_TIMESTAMP()::TEXT) FOR 6));

INSERT INTO ledger_entry (transaction_id, account_type, account_reference, entry_side, amount, currency, description)
VALUES (p_transaction_id, 'CARD_HOLDER', p_card_token, 'DEBE', p_amount, p_currency, 'Compra autorizada');

INSERT INTO ledger_entry (transaction_id, account_type, account_reference, entry_side, amount, currency, description)
VALUES (p_transaction_id, 'ACQUIRER_SUSPENSE', p_merchant_id, 'HABER', p_amount, p_currency, 'Compra autorizada');

o_success := true;
    o_new_available_balance := v_new_balance;
END;
$$;