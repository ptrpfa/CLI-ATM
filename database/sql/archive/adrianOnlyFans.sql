SELECT * FROM  `OOP_ATM`.`User` u
LEFT JOIN `OOP_ATM`.`NormalUser` nu ON u.userid = nu.userid 
LEFT JOIN `OOP_ATM`.`BusinessUser` bu on u.userid = bu.userid
WHERE u.username = 'skyish'

-- SELECT * FROM `OOP_ATM`.`transaction` WHERE transactionNo = "00001096-03-2023";
-- SELECT MAX(SUBSTR(TransactionNo,1, 8)) FROM `OOP_ATM`.`Transaction` WHERE  AccountID =3005