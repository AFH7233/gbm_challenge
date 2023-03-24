CREATE TABLE IF NOT EXISTS Accounts (
    id INT AUTO_INCREMENT PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS AccountTransactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    operation ENUM('DEPOSIT', 'RETIRE', 'HOLD', 'CANCEL') NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES Accounts(id)
    );

DELIMITER $$

CREATE PROCEDURE create_account()
BEGIN
    INSERT INTO Accounts() VALUES();
END$$

CREATE PROCEDURE get_account(IN account_id INT)
BEGIN
    SELECT * FROM Accounts WHERE id = account_id;
END$$

CREATE PROCEDURE create_account_transaction(IN account_id INT, IN amount DECIMAL(10, 2), IN operation ENUM('Deposit', 'Retire', 'Hold'), IN timestamp TIMESTAMP)
BEGIN
    INSERT INTO AccountTransactions(account_id, amount, operation, timestamp) VALUES(account_id, amount, operation, timestamp);
END$$

CREATE PROCEDURE get_total_cash_for_account(IN account_id INT)
BEGIN
    SELECT
            SUM(CASE WHEN operation = 'DEPOSIT' THEN amount ELSE 0 END) -
            SUM(CASE WHEN operation = 'RETIRE' THEN amount ELSE 0 END) -
            SUM(CASE WHEN operation = 'HOLD' THEN amount ELSE 0 END) as total_cash
    FROM AccountTransactions
    WHERE account_id = account_id;
END$$

CREATE PROCEDURE update_account_transaction(IN transaction_id INT, IN account_id INT, IN amount DECIMAL(10, 2), IN operation ENUM('DEPOSIT', 'RETIRE', 'HOLD', 'CANCEL') , IN timestamp TIMESTAMP)
BEGIN
    UPDATE AccountTransactions
    SET account_id = account_id,
        amount = amount,
        operation = operation,
        timestamp = timestamp
    WHERE id = transaction_id AND operation = 'Hold';
END$$

DELIMITER ;