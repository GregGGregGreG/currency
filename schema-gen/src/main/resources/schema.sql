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
    id BIGINT(20),
    added_datetime TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
    from_ccy VARCHAR(3) NOT NULL,
    to_ccy VARCHAR(3) NOT NULL,
    amount DOUBLE NOT NULL,
    cron VARCHAR(30) NOT NULL,
    active TINYINT(1) DEFAULT '1' NOT NULL
);