CREATE TABLE payroll
(
    id                  BIGINT IDENTITY(1,1) NOT NULL,
    user_id             VARCHAR(36)    NOT NULL,
    competence          DATE           NOT NULL,
    type                INT            NOT NULL,
    entry               DATE           NOT NULL,
    event               INT            NOT NULL,
    quantity            INT            NOT NULL,
    amount              DECIMAL(19, 2) NOT NULL,
    created_at          DATETIME2      NOT NULL CONSTRAINT df_payroll_created_at DEFAULT SYSUTCDATETIME(),
    updated_at          DATETIME2      NOT NULL CONSTRAINT df_payroll_updated_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_payroll PRIMARY KEY (id)
);

CREATE INDEX idx_payroll_user_competence ON payroll (user_id, competence);

CREATE TABLE transaction_payroll
(
    id                  BIGINT IDENTITY(1,1) NOT NULL,
    payroll             BIGINT NOT NULL,
    transaction_account BIGINT NOT NULL,
    CONSTRAINT pk_transaction_payroll PRIMARY KEY (id),
    CONSTRAINT fk_transaction_payroll_payroll FOREIGN KEY (payroll) REFERENCES payroll (id),
    CONSTRAINT fk_transaction_payroll_transaction_account FOREIGN KEY (transaction_account) REFERENCES transaction_account (id)
);