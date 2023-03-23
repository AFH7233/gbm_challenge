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
DELIMITER ;