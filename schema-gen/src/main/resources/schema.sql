CREATE TABLE exchange_operation
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    from_ccy VARCHAR(3) NOT NULL,
    to_ccy VARCHAR(3) NOT NULL,
    from_amount DOUBLE NOT NULL,
    to_amount DOUBLE NOT NULL,
    rates_date DATE NOT NULL,
    perform_datetime TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL
);
CREATE TABLE exchange_rate
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    base_ccy VARCHAR(3) DEFAULT 'UAH' NOT NULL,
    ccy VARCHAR(3) NOT NULL,
    exchange_date DATE NOT NULL,
    rate DOUBLE NOT NULL
);
CREATE TABLE exchange_task
(
    id BIGINT(20) PRIMARY KEY NOT NULL,
    added_datetime TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
    from_ccy VARCHAR(3) NOT NULL,
    to_ccy VARCHAR(3) NOT NULL,
    amount DOUBLE NOT NULL,
    cron VARCHAR(30) NOT NULL,
    active TINYINT(1) DEFAULT '1' NOT NULL
);
CREATE TABLE user_role
(
    id BIGINT(20) PRIMARY KEY NOT NULL,
    role_name VARCHAR(50) NOT NULL
);
CREATE UNIQUE INDEX user_role_role_name_uindex ON user_role (role_name);
CREATE TABLE user
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    acc_non_expired TINYINT(1) DEFAULT '1' NOT NULL,
    acc_non_locked TINYINT(1) DEFAULT '1' NOT NULL,
    cred_non_expired TINYINT(1) DEFAULT '1' NOT NULL,
    enabled TINYINT(1) DEFAULT '1' NOT NULL
);
CREATE UNIQUE INDEX user_username_uindex ON user (username);
CREATE TABLE user_user_role
(
    user_id BIGINT(20) NOT NULL,
    role_id BIGINT(20) NOT NULL,
    CONSTRAINT user_user_role_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id),
    CONSTRAINT user_user_role_user_role_id_fk FOREIGN KEY (role_id) REFERENCES user_role (id)
);
CREATE INDEX user_user_role_user_id_fk ON user_user_role (user_id);
CREATE INDEX user_user_role_user_role_id_fk ON user_user_role (role_id);
CREATE TABLE user_details
(
    user_id BIGINT(20) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    CONSTRAINT user_details_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id)
);
CREATE INDEX user_details_user_id_fk ON user_details (user_id);