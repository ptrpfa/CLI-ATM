# Local MySQL Database connection

### Program Files
---
```
README.md (this file)

dependencies/ (folder containing all project dependencies)

settings.config (configuration file)

Database.java (class for handling database connections)
```


## Getting Started
---
1. Install and setup MySQL on your local machine ([Instructions](#a-local-mysql-setup))
2. Download MySQL JDBC Driver ([Instructions](#b-download-jdbc-driver))
3. Setup Database ([Instructions](#c-setup-mysql-database))
4. Program Execution ([Instructions](#d-program-usage))

### a) Local MySQL Setup
---
*<u>Windows Machine</u>*
1. Download the MySQL installer (available [here](https://dev.mysql.com/downloads/installer/)) and run it. For more instructions on specific configurations, you can refer to [this](https://dev.mysql.com/doc/refman/8.0/en/windows-installation.html).
2. Ensure that MySQL is running by using the following command on a terminal: <br>
   ```
   C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld
   ```
3. We will now proceed to reset the `root` user's password as no password is set by default. Run the following command to start a MySQL client: <br>
   ```
   mysql -u root
   ```
4. Update the `root` user's password using the following commands:
   ```
    UPDATE mysql.user SET authentication_string=PASSWORD('<New Password>') WHERE User='root';
    FLUSH PRIVILEGES;
    exit;
   ```
<br>

*<u>macOS Machine</u>*
1. Ensure that you have brew installed on your machine (refer [here](https://docs.brew.sh/Installation) to install).
2. Download MySQL using the following command on a terminal: <br>
   ```
   brew install mysql
   ```
3. Run MySQL in the background via the following command: <br>
    ```
    brew services start mysql
    ```
4. We will now proceed to reset the `root` user's password as no password is set by default. Run the following command to start a MySQL client: <br>
    ```
    mysql -u root
    ```
5. Update the `root` user's password using the following commands:
   ```
    UPDATE mysql.user SET authentication_string=PASSWORD('<New Password>') WHERE User='root';
    FLUSH PRIVILEGES;
    exit;
   ```

### b) Download JDBC Driver
---
*<u>Windows Machine</u>*
1. Download the latest MySQL JDBC driver from [here](https://dev.mysql.com/downloads/connector/j/).
2. Extract the driver into the dependencies directory: `C:~\ATM\dependencies`.
3. Locate the mysql-connector.jar file and take note of its filepath. <br>For example: `./dependencies/mysql-connector-j-8.0.32/mysql-connector-j-8.0.32.jar`

<br>

*<u>macOS Machine</u>*
1. Download the latest 'Platform Independent' MySQL JDBC driver from [here](https://dev.mysql.com/downloads/connector/j/).
2. Extract the driver into the dependencies directory: `~\ATM\dependencies`.
3. Locate the mysql-connector.jar file and take note of its filepath. <br> For example: `./dependencies/mysql-connector-j-8.0.32/mysql-connector-j-8.0.32.jar`

### c) Setup MySQL Database
---
1. Ensure that the database_setup.sql file is downloaded.
2. On a terminal, import the database using the following command: <br>
   ```
   mysql -u root -p < DatabaseSetup.sql
   ```
   Alternatively, run the following commands to first create a blank database, and to populate it: <br>
   ```
   mysql -u root -p < Blank_20Feb2023.sql
   mysql -u root -p OOP_ATM < Migrate_27Feb2023.sql
   ```
### d) Program Usage
---
*<u>Windows Machine</u>*
1. Compile the `.java` program files into `.class` files using the following command: 
   ```
   javac *.java
   ```
2. Run the program, whilst ensuring that the filepath of the JDBC driver's JAR file is included in the class path, using the following command:
   ```
   # Include filepath of JDBC driver's JAR file (take note of semi-colon ;)
   java -cp .;<filepath to JAR file> <program>

   # Example
   java -cp .;./dependencies/mysql-connector-j-8.0.32/mysql-connector-j-8.0.32.jar Database
   ```

*<u>macOS Machine</u>*
1. Compile the `.java` program files into `.class` files using the following command: 
   ```
   javac *.java
   ```
2. Run the program, whilst ensuring that the filepath of the JDBC driver's JAR file is included in the class path, using the following command:
   ```
   # Include filepath of JDBC driver's JAR file (take note of colon :)
   java -cp .:<filepath to JAR file> <program>

   # Example
   java -cp .:./dependencies/mysql-connector-j-8.0.32/mysql-connector-j-8.0.32.jar Database
   ```

*<u>Compilation & Execution</u>*
1. Alternatively, the program can be compiled and executed together using a single command:
   ```
   # Include filepath of JDBC driver's JAR file
   java -cp <filepath to JAR file> <program.java>

   # Example
   java -cp ./dependencies/mysql-connector-j-8.0.32/mysql-connector-j-8.0.32.jar Database.java
   ```
