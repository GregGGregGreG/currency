CREATE TABLE exchange_rate
(
    id BIGINT(20) PRIMARY KEY NOT NULL,
    base_liter_code VARCHAR(3) DEFAULT 'UAH' NOT NULL,
    liter_code VARCHAR(3) DEFAULT 'USD' NOT NULL,
    exchange_date DATE NOT NULL,
    rate DOUBLE NOT NULL
);
CREATE TABLE exchange_operation
(
    id BIGINT(20) PRIMARY KEY NOT NULL,
    from_currency_code VARCHAR(3) DEFAULT 'UAH' NOT NULL,
    to_currency_code VARCHAR(3) DEFAULT 'USD' NOT NULL,
    from_amount DOUBLE NOT NULL,
    to_amount DOUBLE NOT NULL,
    date DATE NOT NULL
);