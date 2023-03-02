--UPDATE `OOP_ATM`.`User` SET Username = 'LOL', PasswordHash = 'pI3IEoY0Z+gzZM6da221m/NK0A8StOBzIYRnViM8AZg=', Active = 1 WHERE UserID = 834181;


SELECT * FROM `OOP_ATM`.`User` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`NormalUser` LIMIT 1000;
SELECT * FROM `OOP_ATM`.`BusinessUser` LIMIT 1000;
SELECT * FROM `OOP_ATM`.`Account` WHERE AccountID = 2318;
SELECT * FROM `OOP_ATM`.`Transaction` WHERE AccountID = 2318;