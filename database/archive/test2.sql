-- UPDATE `OOP_ATM`.`User` SET Username = 'LOL', PasswordHash = 'pI3IEoY0Z+gzZM6da221m/NK0A8StOBzIYRnViM8AZg=', Active = 1 WHERE UserID = 3;
-- UPDATE `OOP_ATM`.`User` SET Active = 1 WHERE UserID = 1;


SELECT * FROM `OOP_ATM`.`Account` WHERE userid =1 LIMIT 1000;
DELETE FROM `OOP_ATM`.`Account` WHERE userid =1 AND AccountID =3018;
UPDATE `OOP_ATM`.`Account` SET AvailableBalance = 100, TotalBalance = 5000 WHERE UserId = 1 AND AccountID = 1;
-- SELECT * FROM `OOP_ATM`.`NormalUser` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`BusinessUser` LIMIT 1000;

-- SELECT * FROM `OOP_ATM`.`Account`;

SELECT * FROM `OOP_ATM`.`Transaction` WHERE AccountID = 1 LIMIT 1000;

-- SELECT * FROM `OOP_ATM`.`ChequeAccount` WHERE AccountID = 3 LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`Cheque` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`ChequeTransaction` LIMIT 1000;
