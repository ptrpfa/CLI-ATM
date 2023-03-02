-- UPDATE `OOP_ATM`.`User` SET Username = 'Liu Kai Ping', PasswordHash = 'pI3IEoY0Z+gzZM6da221m/NK0A8StOBzIYRnViM8AZg=' WHERE UserID = 1;
-- UPDATE `OOP_ATM`.`User` SET Username = 'Test User', PasswordHash = 'pI3IEoY0Z+gzZM6da221m/NK0A8StOBzIYRnViM8AZg=', Active = 0 WHERE UserID = 2;


SELECT * FROM `OOP_ATM`.`User` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`NormalUser` LIMIT 1000;
SELECT * FROM `OOP_ATM`.`BusinessUser` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`Account` LIMIT 100; WHERE UserID = 833020;