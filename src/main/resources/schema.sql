DROP TABLE IF EXISTS `stock`;
DROP TABLE IF EXISTS `trade`;

CREATE TABLE IF NOT EXISTS `stock` (
    `symbol` VARCHAR(50)    NOT NULL PRIMARY KEY,
    `value`  DECIMAL(13,2)  NOT NULL,
    `volume` INT            NOT NULL
);

CREATE TABLE IF NOT EXISTS `trade` (
    `order_id`      BIGINT      NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `username`      VARCHAR(50) NOT NULL,
    `stock_symbol`  VARCHAR(50) NOT NULL,
    `volume`        INT         NOT NULL,
    `trade_type`    VARCHAR(50) NOT NULL
);