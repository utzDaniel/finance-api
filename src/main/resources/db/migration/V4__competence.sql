CREATE TABLE competence
(
    id         INT IDENTITY(1,1) NOT NULL,
    user_id    VARCHAR(36) NOT NULL,
    month_year DATE        NOT NULL,
    status     INT         NOT NULL,
    created_at DATETIME2   NOT NULL CONSTRAINT df_competence_created_at DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2   NOT NULL CONSTRAINT df_competence_updated_at DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_competence PRIMARY KEY (id)
);

CREATE INDEX idx_competence_user_month_year ON competence (user_id, month_year);