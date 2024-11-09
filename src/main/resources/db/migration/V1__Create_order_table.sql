CREATE TABLE orders (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    date        DATE             NOT NULL,
    ticker      VARCHAR(4)       NOT NULL,
    order_side  VARCHAR(4)       NOT NULL,
    volume      BIGINT           NOT NULL,
    price       DOUBLE PRECISION NOT NULL,
    currency    VARCHAR(3)       NOT NULL);