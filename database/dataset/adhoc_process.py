# Add-on program to perform extra dataset processing
import mysql.connector
import re

# Initialise database connection
db_password = input("Enter db password: ").strip()
mysql_db = mysql.connector.connect(host="localhost", user="root", password=db_password, database="OOP_ATM")
db_cursor = mysql_db.cursor()

# Initialise dictionaries
accounts = {}
cheques = {}


# Populate accounts dictionary
db_cursor.execute("SELECT * FROM Account;")
for i in db_cursor:
    accounts[i[0]] = i[2]

# Populate cheques dictionary
db_cursor.execute("SELECT * FROM Cheque;")
for i in db_cursor:
    cheques[i[0]] = i[5]

# Add dash(-) in between AccountNo 
for k in cheques.keys():
    cheques[k] = re.sub(r"^(\d{6}:\d{6}:)(\d{3})(\d{9})$",r"\1\2-\3", cheques[k])
for k in accounts.keys():
    accounts[k] = re.sub(r"^(\d{3})(\d{9})$", r"\1-\2", accounts[k])

# Loop to execute updates
for k, v in accounts.items():
    sql = "UPDATE Account SET AccountNo = %s WHERE AccountID = %s;"
    values = (v, k)
    db_cursor.execute(sql, values)
for k, v in cheques.items():
    sql = "UPDATE Cheque SET ChequeNo = %s WHERE ChequeID = %s;"
    values = (v, k)
    db_cursor.execute(sql, values)

# Effect changes
mysql_db.commit()

# Close connection
db_cursor.close()
mysql_db.close()