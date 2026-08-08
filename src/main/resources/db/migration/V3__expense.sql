CREATE TABLE expense
(
    id         BIGINT IDENTITY(1,1) NOT NULL,
    user_id    VARCHAR(36)    NOT NULL,
    competence DATE           NOT NULL,
    due        DATE           NOT NULL,
    shared     BIT            NOT NULL DEFAULT 0,
    name       VARCHAR(50)    NOT NULL,
    amount     DECIMAL(19, 2) NOT NULL,
    category   INT            NOT NULL,
    detail     VARCHAR(250) NULL,
    created_at DATETIME2      NOT NULL CONSTRAINT df_expense_created_at DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2      NOT NULL CONSTRAINT df_expense_updated_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_expense PRIMARY KEY (id)
);

CREATE INDEX idx_expense_user_competence ON expense (user_id, competence);

CREATE TABLE transaction_expense
(
    id                  BIGINT IDENTITY(1,1) NOT NULL,
    expense             BIGINT NOT NULL,
    transaction_account BIGINT NOT NULL,
    CONSTRAINT pk_transaction_expense PRIMARY KEY (id),
    CONSTRAINT fk_transaction_expense_expense FOREIGN KEY (expense) REFERENCES expense (id),
    CONSTRAINT fk_transaction_expense_transaction FOREIGN KEY (transaction_account) REFERENCES transaction_account (id)
);