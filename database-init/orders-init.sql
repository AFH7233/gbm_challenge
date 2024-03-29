CREATE TABLE IF NOT EXISTS OrderHistory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    operation ENUM('BUY', 'SELL') NOT NULL,
    issuer_name VARCHAR(255) NOT NULL,
    total_shares INT NOT NULL,
    share_price DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL
    );

DELIMITER //

CREATE PROCEDURE create_order_history_entry(
    IN p_account_id BIGINT,
    IN p_operation ENUM('BUY', 'SELL'),
    IN p_issuer_name VARCHAR(255),
    IN p_total_shares INT,
    IN p_share_price DECIMAL(10, 2),
    IN p_timestamp TIMESTAMP
        )
BEGIN
    INSERT INTO OrderHistory (account_id, operation, issuer_name, total_shares, share_price, timestamp)
    VALUES (p_account_id, p_operation, p_issuer_name, p_total_shares, p_share_price, p_timestamp);
END //

CREATE PROCEDURE get_shares_held_by_account(
    IN p_account_id BIGINT
)
BEGIN
    SELECT
        issuer_name,
        SUM(CASE WHEN operation = 'BUY' THEN total_shares ELSE -total_shares END) AS total_shares,
        MAX(share_price) AS last_share_price
    FROM
        OrderHistory
    WHERE
            account_id = p_account_id
    GROUP BY
        issuer_name
    HAVING
            total_shares > 0;
END //

CREATE PROCEDURE get_last_order_by_account(
    IN p_account_id BIGINT
)
BEGIN
    SELECT *
    FROM OrderHistory
    WHERE account_id = p_account_id AND timestamp = (
        SELECT MAX(timestamp)
        FROM OrderHistory
        WHERE account_id = p_account_id
        );
END //

DELIMITER ;