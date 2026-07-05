CREATE TABLE salary (
	id BIGINT IDENTITY(1,1) NOT NULL,
	user_id VARCHAR(36) NOT NULL,
	competence_date DATETIME2 NOT NULL,
	gross_salary DECIMAL(19,2) NOT NULL,
	net_salary DECIMAL(19,2) NOT NULL,
	created_at DATETIME2 NOT NULL CONSTRAINT df_salary_created_at DEFAULT SYSUTCDATETIME(),
	updated_at DATETIME2 NOT NULL CONSTRAINT df_salary_updated_at DEFAULT SYSUTCDATETIME(),
	CONSTRAINT pk_salary PRIMARY KEY (id),
	CONSTRAINT uq_salary_user_competence_date UNIQUE (user_id, competence_date),
	CONSTRAINT ck_salary_competence_date_only_date CHECK (CAST(competence_date AS TIME(0)) = '00:00:00'),
	CONSTRAINT ck_salary_gross_salary CHECK (gross_salary >= 0),
	CONSTRAINT ck_salary_net_salary CHECK (net_salary >= 0)
);

CREATE INDEX idx_salary_user_id ON salary (user_id);
CREATE INDEX idx_salary_competence_date ON salary (competence_date);