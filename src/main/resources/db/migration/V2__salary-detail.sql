CREATE TABLE salary_detail_item_type (
	id INT NOT NULL,
	name VARCHAR(50) NOT NULL,
	CONSTRAINT pk_salary_detail_item_type PRIMARY KEY (id),
	CONSTRAINT uq_salary_detail_item_type_name UNIQUE (name)
);

INSERT INTO salary_detail_item_type (id, name)
VALUES
    (1, 'Desconto'),
    (2, 'Provento'),
    (3, 'Benefício');

CREATE TABLE salary_detail_item (
    id INT IDENTITY(1,1) NOT NULL,
    code INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT pk_salary_detail_item PRIMARY KEY (id),
    CONSTRAINT uq_salary_detail_item_code_name UNIQUE (code, name)
);

INSERT INTO salary_detail_item (code, name)
VALUES
    ( 300, 'Férias'),
    ( 301, 'Med. Férias Val'),
    ( 302, 'Med. Férias Hor'),
    ( 305, '1/3 Sobre Férias'),
    ( 576, 'INSS Férias');

CREATE TABLE salary_detail (
	 id BIGINT IDENTITY(1,1) NOT NULL,
	 user_id VARCHAR(36) NOT NULL,
	 competence_date DATETIME2 NOT NULL,
     item_type_id INT NOT NULL,
     item_id INT NOT NULL,
	 quantity INT NOT NULL,
	 amount DECIMAL(19,2) NOT NULL,
	 created_at DATETIME2 NOT NULL CONSTRAINT df_salary_detail_created_at DEFAULT SYSUTCDATETIME(),
	 updated_at DATETIME2 NOT NULL CONSTRAINT df_salary_detail_updated_at DEFAULT SYSUTCDATETIME(),
	 CONSTRAINT pk_salary_detail PRIMARY KEY (id),
	 CONSTRAINT fk_salary_detail_item_type FOREIGN KEY (item_type_id) REFERENCES salary_detail_item_type(id),
	 CONSTRAINT fk_salary_detail_item      FOREIGN KEY (item_id) REFERENCES salary_detail_item(id),
	 CONSTRAINT ck_salary_detail_competence_date CHECK (CAST(competence_date AS TIME(0)) = '00:00:00'),
	 CONSTRAINT ck_salary_detail_quantity	     CHECK (quantity >= 0),
	 CONSTRAINT ck_salary_detail_amount	         CHECK (amount >= 0)
);

CREATE INDEX idx_salary_detail_user_competence ON salary_detail (user_id, competence_date);
CREATE INDEX idx_salary_detail_type            ON salary_detail (item_type_id);
CREATE INDEX idx_salary_detail_item            ON salary_detail (item_id);

ALTER TABLE salary_detail
    ADD CONSTRAINT uq_salary_detail UNIQUE (user_id, competence_date, item_type_id, item_id);