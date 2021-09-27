-- RUN IN ROOT (change 'ironhacker' to match your own local user):
-- GRANT ALL PRIVILEGES ON banking.* TO 'ironhacker'@'localhost';
-- GRANT ALL PRIVILEGES ON banking_test.* TO 'ironhacker'@'localhost';

-- RUN IN LOCAL USER:
-- DROP TABLE STATEMENTS
DROP TABLE IF EXISTS credit_card;
DROP TABLE IF EXISTS checking_account;
DROP TABLE IF EXISTS student_checking;
DROP TABLE IF EXISTS savings;
DROP TABLE IF EXISTS `account`;
DROP TABLE IF EXISTS `admin`;
DROP TABLE IF EXISTS third_party;
DROP TABLE IF EXISTS account_holder;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `role`;

-- CREATE TABLE STATEMENTS
DROP SCHEMA IF EXISTS banking;
CREATE SCHEMA banking;
USE banking;

CREATE TABLE `role`(
	id BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255),
    PRIMARY KEY(id)
);

CREATE TABLE `user`(
	id BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255),
    `password` VARCHAR(255),
    role_id BIGINT,
    PRIMARY KEY(id),
    FOREIGN KEY(role_id) REFERENCES role(id)
);

CREATE TABLE account_holder(
	id BIGINT NOT NULL,
    date_of_birth DATE,
    mailing_city VARCHAR(255),
    mailing_country VARCHAR(255),
    mailing_number VARCHAR(255),
    mailing_street VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    `number` VARCHAR(255),
    street VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY(id) REFERENCES `user`(id)
);

CREATE TABLE third_party(
	id BIGINT,
    hashed_key VARCHAR(255),
    PRIMARY KEY(id),
    FOREIGN KEY(id) REFERENCES `user`(id)
);

CREATE TABLE admin(
	id BIGINT,
    PRIMARY KEY(id),
    FOREIGN KEY(id) REFERENCES `user`(id)
);

CREATE TABLE `account`(
	id BIGINT NOT NULL AUTO_INCREMENT,
    balance_amount DECIMAL(19,2),
    balance_currency VARCHAR(3),
    creation_date DATE,
    current_day_transactions DECIMAL(19,2),
    last_amount DECIMAL(19,2),
    last_currency VARCHAR(3),
    last_transaction_time DATETIME(6),
    max_daily_amount DECIMAL(19,2),
    last_accessed DATE,
    owner_id BIGINT,
    second_owner_id BIGINT,
    PRIMARY KEY(id),
    FOREIGN KEY(owner_id) REFERENCES account_holder(id),
    FOREIGN KEY(owner_id) REFERENCES account_holder(id)
);

CREATE TABLE savings(
	id BIGINT,
    interest_rate DECIMAL(19,4),
    min_balance_amount DECIMAL(19,2),
    min_balance_currency VARCHAR(3),
    penalty_amount DECIMAL(19,2),
    penalty_currency VARCHAR(3),
    secret_key VARCHAR(255),
    `status` VARCHAR(255),
    PRIMARY KEY(id),
    FOREIGN KEY(id) REFERENCES `account`(id)
);

CREATE TABLE student_checking(
	id BIGINT,
    secret_key VARCHAR(255),
    `status` VARCHAR(255),
    PRIMARY KEY(id),
    FOREIGN KEY(id) REFERENCES `account`(id)
);

CREATE TABLE checking_account(
	id BIGINT,
    min_balance_amount DECIMAL(19,2),
    min_balance_currency VARCHAR(3),
    maintenance_fee_amount DECIMAL(19,2),
    maintenance_fee_currency VARCHAR(3),
    penalty_amount DECIMAL(19,2),
    penalty_currency VARCHAR(3),
    secret_key VARCHAR(255),
    `status` VARCHAR(255),
    PRIMARY KEY(id),
    FOREIGN KEY(id) REFERENCES `account`(id)
);

CREATE TABLE credit_card(
	id BIGINT, 
    credit_limit_amount DECIMAL(19,2),
    credit_limit_currency VARCHAR(3),
    interest_rate DECIMAL(19,4),
	penalty_amount DECIMAL(19,2),
    penalty_currency VARCHAR(3),
    PRIMARY KEY(id),
    FOREIGN KEY(id) REFERENCES `account`(id)
);

-- INSERT STATEMENTS
INSERT INTO `role`(name)
VALUES('ADMIN'), ('ACCOUNTHOLDER');

INSERT INTO `user`(name, password, role_id)
VALUES('admin', '$2a$10$MSzkrmfd5ZTipY0XkuCbAejBC9g74MAg2wrkeu8/m1wQGXDihaX3e', 1);

-- TEST DATABASE
DROP SCHEMA IF EXISTS banking_test;
CREATE SCHEMA banking_test;
USE banking_test;
