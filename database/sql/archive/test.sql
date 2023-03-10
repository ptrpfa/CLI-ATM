USE OOP_ATM;

SELECT * FROM User;
SELECT * FROM NormalUser;
SELECT * FROM BusinessUser;


-- USE OOP_ATM;
-- SELECT * FROM user WHERE username = "MB3DS8Bl";
-- USE OOP_ATM;
-- SELECT * FROM normaluser WHERE UserID = 833024;
-- USE OOP_ATM;
-- UPDATE user SET PasswordSalt = "VX0vi5UiwA/mlQDmLi2CbA==", PasswordHash = "LEzQnNubVnVkpjRK7svpJrlKmf/a7mvRKkBaHX2e/AY=" WHERE UserID = 1;

-- SELECT * FROM `OOP_ATM`.`Cheque` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`ChequeAccount` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`ChequeTransaction` LIMIT 1000;

-- UPDATE User SET Username = 'test69', PasswordSalt = 'fDrQKS5vwNTwF9Jnk0uv0A==', PasswordHash = 'oJs08QBWZJHyHdyTCjTPzQ==' WHERE UserID = 3;

-- SELECT * FROM `OOP_ATM`.`User` LIMIT 1000;
-- SELECT * FROM `OOP_ATM`.`ChequeAccount` WHERE accountID=3  LIMIT 1000;
SELECT * FROM `OOP_ATM`.`Cheque` WHERE chequeID IN (SELECT chequeID FROM OOP_ATM.ChequeAccount WHERE accountID = 3) LIMIT 1000;