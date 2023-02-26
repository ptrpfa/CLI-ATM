USE OOP_ATM;

SELECT * FROM Account WHERE UserID = "834845" ;

-- SELECT * FROM Account;

USE OOP_ATM;

SELECT MAX(RIGHT(AccountNo, 9)) AS max_next_account_number
FROM Account
WHERE LEFT(AccountNo, 4) = '407-' AND UserID = '834845';

UPDATE Account SET AccountNo = CONCAT('407-', LPAD(RIGHT(AccountNo, 9) + 1, 9, '0')) WHERE UserID = 834845 AND AccountID = 3004;

