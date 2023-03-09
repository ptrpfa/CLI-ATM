
SELECT * FROM `OOP_ATM`.`transaction` WHERE transactionNo = "00001096-03-2023";
SELECT MAX(SUBSTR(TransactionNo,1, 8)) FROM `OOP_ATM`.`Transaction` WHERE  AccountID =3005