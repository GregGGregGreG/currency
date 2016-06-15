CREATE TABLE exchange_operation
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    from_currency_code VARCHAR(3) NOT NULL,
    to_currency_code VARCHAR(3) NOT NULL,
    from_amount DOUBLE NOT NULL,
    to_amount DOUBLE NOT NULL,
    date DATE NOT NULL
);
CREATE TABLE exchange_rate
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    base_liter_code VARCHAR(3) DEFAULT 'UAH' NOT NULL,
    liter_code VARCHAR(3) DEFAULT 'USD' NOT NULL,
    exchange_date DATE NOT NULL,
    rate DOUBLE NOT NULL
);
CREATE TABLE exchange_task
(
    id BIGINT(20),
    date_added DATE NOT NULL,
    from_ccy VARCHAR(3) NOT NULL,
    to_ccy VARCHAR(3) NOT NULL,
    amount DOUBLE NOT NULL,
    cron VARCHAR(30) NOT NULL,
    active TINYINT(1) DEFAULT '1' NOT NULL
);