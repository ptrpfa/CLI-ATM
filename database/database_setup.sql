-- MySQL dump 10.13  Distrib 8.0.32, for macos13.0 (arm64)
--
-- Host: localhost    Database: OOP_ATM
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `OOP_ATM`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `OOP_ATM` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `OOP_ATM`;

--
-- Table structure for table `Account`
--

DROP TABLE IF EXISTS `Account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Account` (
  `AccountID` int unsigned NOT NULL AUTO_INCREMENT,
  `UserID` int unsigned NOT NULL,
  `AccountNo` varchar(200) NOT NULL,
  `Name` varchar(200) DEFAULT NULL,
  `Description` varchar(500) DEFAULT NULL,
  `HoldingBalance` double NOT NULL DEFAULT '0',
  `AvailableBalance` double NOT NULL DEFAULT '0',
  `TotalBalance` double NOT NULL DEFAULT '0',
  `TransferLimit` double DEFAULT NULL,
  `WithdrawalLimit` double DEFAULT NULL,
  `OpeningDate` datetime NOT NULL,
  `Active` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`AccountID`),
  UNIQUE KEY `AccountID_UNIQUE` (`AccountID`),
  UNIQUE KEY `AccountNo_UNIQUE` (`AccountNo`),
  KEY `Account_User_idx` (`UserID`),
  CONSTRAINT `Account_User` FOREIGN KEY (`UserID`) REFERENCES `User` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Account`
--

LOCK TABLES `Account` WRITE;
/*!40000 ALTER TABLE `Account` DISABLE KEYS */;
/*!40000 ALTER TABLE `Account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `BusinessUser`
--

DROP TABLE IF EXISTS `BusinessUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `BusinessUser` (
  `UserID` int unsigned NOT NULL,
  `UEN` varchar(50) NOT NULL,
  `BusinessName` varchar(200) NOT NULL,
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `UserID_UNIQUE` (`UserID`),
  UNIQUE KEY `UEN_UNIQUE` (`UEN`),
  CONSTRAINT `BusinessUser_User` FOREIGN KEY (`UserID`) REFERENCES `User` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BusinessUser`
--

LOCK TABLES `BusinessUser` WRITE;
/*!40000 ALTER TABLE `BusinessUser` DISABLE KEYS */;
/*!40000 ALTER TABLE `BusinessUser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Cheque`
--

DROP TABLE IF EXISTS `Cheque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Cheque` (
  `ChequeID` int unsigned NOT NULL AUTO_INCREMENT,
  `IssuerAccount` int unsigned DEFAULT NULL,
  `RecipientAccount` int unsigned DEFAULT NULL,
  `IssuingTransaction` int unsigned DEFAULT NULL,
  `ReceivingTransaction` int unsigned DEFAULT NULL,
  `ChequeNo` varchar(200) NOT NULL,
  `Value` double unsigned NOT NULL DEFAULT '0',
  `Date` datetime NOT NULL,
  `Status` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`ChequeID`),
  UNIQUE KEY `ChequeID_UNIQUE` (`ChequeID`),
  UNIQUE KEY `ChequeNo_UNIQUE` (`ChequeNo`),
  KEY `Cheque_IA_idx` (`IssuerAccount`),
  KEY `Cheque_RA_idx` (`RecipientAccount`),
  KEY `Cheque_IT_idx` (`IssuingTransaction`),
  KEY `Cheque_RT_idx` (`ReceivingTransaction`),
  CONSTRAINT `Cheque_IA` FOREIGN KEY (`IssuerAccount`) REFERENCES `Account` (`AccountID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Cheque_IT` FOREIGN KEY (`IssuingTransaction`) REFERENCES `Transaction` (`TransactionID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Cheque_RA` FOREIGN KEY (`RecipientAccount`) REFERENCES `Account` (`AccountID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Cheque_RT` FOREIGN KEY (`ReceivingTransaction`) REFERENCES `Transaction` (`TransactionID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Cheque`
--

LOCK TABLES `Cheque` WRITE;
/*!40000 ALTER TABLE `Cheque` DISABLE KEYS */;
/*!40000 ALTER TABLE `Cheque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ChequeAccount`
--

DROP TABLE IF EXISTS `ChequeAccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChequeAccount` (
  `ID` int unsigned NOT NULL AUTO_INCREMENT,
  `ChequeID` int unsigned NOT NULL,
  `AccountID` int unsigned NOT NULL,
  `Type` int unsigned NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  KEY `ChequeAccount_Account_idx` (`AccountID`),
  KEY `ChequeAccount_Cheque_idx` (`ChequeID`),
  CONSTRAINT `ChequeAccount_Account` FOREIGN KEY (`AccountID`) REFERENCES `Account` (`AccountID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ChequeAccount_Cheque` FOREIGN KEY (`ChequeID`) REFERENCES `Cheque` (`ChequeID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChequeAccount`
--

LOCK TABLES `ChequeAccount` WRITE;
/*!40000 ALTER TABLE `ChequeAccount` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChequeAccount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ChequeTransaction`
--

DROP TABLE IF EXISTS `ChequeTransaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ChequeTransaction` (
  `ID` int unsigned NOT NULL AUTO_INCREMENT,
  `ChequeID` int unsigned NOT NULL,
  `TransactionID` int unsigned NOT NULL,
  `Type` int unsigned NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ID_UNIQUE` (`ID`),
  KEY `ChequeTransaction_Cheque_idx` (`ChequeID`),
  KEY `ChequeTransaction_Transaction_idx` (`TransactionID`),
  CONSTRAINT `ChequeTransaction_Cheque` FOREIGN KEY (`ChequeID`) REFERENCES `Cheque` (`ChequeID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ChequeTransaction_Transaction` FOREIGN KEY (`TransactionID`) REFERENCES `Transaction` (`TransactionID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ChequeTransaction`
--

LOCK TABLES `ChequeTransaction` WRITE;
/*!40000 ALTER TABLE `ChequeTransaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `ChequeTransaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `NormalUser`
--

DROP TABLE IF EXISTS `NormalUser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `NormalUser` (
  `UserID` int unsigned NOT NULL,
  `NRIC` varchar(50) NOT NULL,
  `FirstName` varchar(100) NOT NULL,
  `MiddleName` varchar(100) DEFAULT NULL,
  `LastName` varchar(100) DEFAULT NULL,
  `Gender` varchar(50) NOT NULL,
  `Birthday` datetime NOT NULL,
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `UserID_UNIQUE` (`UserID`),
  CONSTRAINT `NormalUser_User` FOREIGN KEY (`UserID`) REFERENCES `User` (`UserID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `NormalUser`
--

LOCK TABLES `NormalUser` WRITE;
/*!40000 ALTER TABLE `NormalUser` DISABLE KEYS */;
/*!40000 ALTER TABLE `NormalUser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Transaction`
--

DROP TABLE IF EXISTS `Transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Transaction` (
  `TransactionID` int unsigned NOT NULL AUTO_INCREMENT,
  `AccountID` int unsigned NOT NULL,
  `TransactionNo` varchar(200) NOT NULL,
  `Datetime` datetime NOT NULL,
  `ValueDatetime` datetime NOT NULL,
  `Debit` double NOT NULL DEFAULT '0',
  `Credit` double NOT NULL DEFAULT '0',
  `Balance` double NOT NULL DEFAULT '0',
  `Status` int NOT NULL DEFAULT '0',
  `Remarks` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`TransactionID`),
  UNIQUE KEY `TransactionID_UNIQUE` (`TransactionID`),
  UNIQUE KEY `TransactionNo_UNIQUE` (`TransactionNo`),
  KEY `Transaction_Account_idx` (`AccountID`),
  CONSTRAINT `Transaction_Account` FOREIGN KEY (`AccountID`) REFERENCES `Account` (`AccountID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Transaction`
--

LOCK TABLES `Transaction` WRITE;
/*!40000 ALTER TABLE `Transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `Transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `UserID` int unsigned NOT NULL AUTO_INCREMENT,
  `Username` varchar(100) NOT NULL,
  `PasswordSalt` varchar(100) NOT NULL,
  `PasswordHash` varchar(500) NOT NULL,
  `Email` varchar(250) NOT NULL,
  `Phone` varchar(50) NOT NULL,
  `AddressOne` varchar(200) DEFAULT NULL,
  `AddressTwo` varchar(200) DEFAULT NULL,
  `AddressThree` varchar(200) DEFAULT NULL,
  `PostalCode` varchar(50) DEFAULT NULL,
  `RegistrationDate` datetime NOT NULL,
  `Active` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `UserID_UNIQUE` (`UserID`),
  UNIQUE KEY `Username_UNIQUE` (`Username`),
  UNIQUE KEY `Email_UNIQUE` (`Email`),
  UNIQUE KEY `Phone_UNIQUE` (`Phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-02-15 17:32:50
