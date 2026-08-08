CREATE TABLE account
(
    id         INT IDENTITY(1,1) NOT NULL,
    code       UNIQUEIDENTIFIER NOT NULL,
    user_id    VARCHAR(36)      NOT NULL,
    competence DATE             NOT NULL,
    name       VARCHAR(50)      NOT NULL,
    bank       INT              NOT NULL,
    type       INT              NOT NULL,
    link       INT              NOT NULL,
    balance    DECIMAL(19, 2)   NOT NULL,
    created_at DATETIME2        NOT NULL CONSTRAINT df_account_created_at DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2        NOT NULL CONSTRAINT df_account_updated_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_account PRIMARY KEY (id)
);

CREATE INDEX idx_account_user_competence ON account (user_id, competence);

CREATE TABLE transaction_account
(
    id               BIGINT IDENTITY(1,1) NOT NULL,
    account          INT            NOT NULL,
    method           INT            NOT NULL,
    name             VARCHAR(50)    NOT NULL,
    debit            BIT            NOT NULL,
    amount           DECIMAL(19, 2) NOT NULL,
    date_transaction DATE           NOT NULL,
    created_at       DATETIME2      NOT NULL CONSTRAINT df_transaction_account_created_at DEFAULT SYSUTCDATETIME(),
    updated_at       DATETIME2      NOT NULL CONSTRAINT df_transaction_account_updated_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_transaction_account PRIMARY KEY (id),
    CONSTRAINT fk_transaction_account_account FOREIGN KEY (account) REFERENCES account (id)
);

CREATE INDEX idx_transaction_account_account ON transaction_account (account);

CREATE TABLE transaction_account_transfer
(
    id                  BIGINT IDENTITY(1,1) NOT NULL,
    account_origin      INT NOT NULL,
    account_destination INT NULL,
    transaction_account BIGINT NOT NULL,
    CONSTRAINT pk_transaction_account_transfer PRIMARY KEY (id),
    CONSTRAINT fk_transaction_account_transfer_account_origin FOREIGN KEY (account_origin) REFERENCES account (id),
    CONSTRAINT fk_transaction_account_transfer_account_destination FOREIGN KEY (account_destination) REFERENCES account (id),
    CONSTRAINT fk_transaction_account_transfer_transaction FOREIGN KEY (transaction_account) REFERENCES transaction_account (id)
);
