-- -- UPDATE `OOP_ATM`.`User` SET Username = 'LOL', PasswordHash = 'pI3IEoY0Z+gzZM6da221m/NK0A8StOBzIYRnViM8AZg=', Active = 1 WHERE UserID = 3;
-- -- UPDATE `OOP_ATM`.`User` SET Active = 1 WHERE UserID = 1;


SELECT * FROM `OOP_ATM`.`Account` WHERE UserID = '5';
SELECT * FROM `OOP_ATM`.`Transaction` WHERE AccountID = 1 ORDER BY TransactionID DESC LIMIT 1;
UPDATE `OOP_ATM`.`User` SET PasswordHash = 'ArfxhNgHlBIduqJuHKkoXEb91opTkVZBz2E019JlMfI=', PasswordSalt = 'Y71q44QlBWZtBKjCCNPoOA==' where userid =1;
SELECT MAX(SUBSTR(TransactionNo,1, 8)) FROM `OOP_ATM`.`Transaction` WHERE  AccountID = 1 LIMIT 4;
DELETE FROM `OOP_ATM`.`Account` WHERE userid =1 AND AccountID =3018;
UPDATE `OOP_ATM`.`Account` SET AvailableBalance = 100, TotalBalance = 5000 WHERE UserId = 1 AND AccountID = 1;
-- SELECT * FROM `OOP_ATM`.`NormalUser` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`BusinessUser` LIMIT 1000;
SELECT * FROM `OOP_ATM`.`Account` WHERE AccountID = 3 LIMIT 1000;
SELECT * FROM `OOP_ATM`.`ChequeAccount`  LIMIT 1000;
SELECT * FROM `OOP_ATM`.`ChequeTransaction` LIMIT 1000;
SELECT * FROM `OOP_ATM`.`Cheque` LIMIT 1000;
SELECT * FROM `OOP_ATM`.`Cheque` WHERE ChequeID IN (SELECT ChequeID FROM `OOP_ATM`.`ChequeTransaction` WHERE AccountID = 3) LIMIT 1000;


-- DELETE FROM `OOP_ATM`.`Account` WHERE userid = 1 AND AccountID =3018;
-- UPDATE `OOP_ATM`.`Account` SET AvailableBalance = 100, TotalBalance = 5000 WHERE UserId = 1 AND AccountID = 1;
-- -- SELECT * FROM `OOP_ATM`.`NormalUser` LIMIT 1000;
-- -- SELECT * FROM `OOP_ATM`.`BusinessUser` LIMIT 1000;

-- -- SELECT * FROM `OOP_ATM`.`Account`;

-- SELECT * FROM `OOP_ATM`.`Transaction` WHERE AccountID = 1 LIMIT 1000;

-- -- SELECT * FROM `OOP_ATM`.`ChequeAccount` WHERE AccountID = 3 LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`Cheque` LIMIT 1000;
-- -- SELECT * FROM `OOP_ATM`.`ChequeTransaction` LIMIT 1000;
