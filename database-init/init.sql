CREATE TABLE IF NOT EXISTS Accounts (
        id INT AUTO_INCREMENT PRIMARY KEY,
        cash DECIMAL(10, 2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS Issuers (
       id INT AUTO_INCREMENT PRIMARY KEY,
       account_id INT NOT NULL,
       symbol VARCHAR(10) NOT NULL,
    shares INT NOT NULL,
    share_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES Accounts(id)
    );

DELIMITER $$

CREATE PROCEDURE create_account(IN cash DECIMAL(10, 2))
BEGIN
INSERT INTO Accounts(cash) VALUES(cash);
END$$

CREATE PROCEDURE update_account(IN account_id INT, IN cash DECIMAL(10, 2))
BEGIN
UPDATE Accounts SET cash = cash WHERE id = account_id;
END$$

CREATE PROCEDURE get_account(IN account_id INT)
BEGIN
SELECT * FROM Accounts WHERE id = account_id;
END$$

CREATE PROCEDURE create_issuer(IN account_id INT, IN symbol VARCHAR(10), IN shares INT, IN share_price DECIMAL(10, 2))
BEGIN
INSERT INTO Issuers(account_id, symbol, shares, share_price) VALUES(account_id, symbol, shares, share_price);
END$$

CREATE PROCEDURE update_issuer(IN issuer_id INT, IN shares INT, IN share_price DECIMAL(10, 2))
BEGIN
UPDATE Issuers SET shares = shares, share_price = share_price WHERE id = issuer_id;
END$$

CREATE PROCEDURE get_issuer(IN issuer_id INT)
BEGIN
SELECT * FROM Issuers WHERE id = issuer_id;
END$$


CREATE PROCEDURE get_issuers_from_account(IN account_id INT)
BEGIN
SELECT * FROM Issuers WHERE account_id =account_id;
END$$

DELIMITER ;