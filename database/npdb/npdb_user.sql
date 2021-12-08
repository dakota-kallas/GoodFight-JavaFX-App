-- MySQL dump 10.13  Distrib 8.0.26, for Win64 (x86_64)
--
-- Host: localhost    Database: npdb
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `Email` varchar(320) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Password` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Type` varchar(12) NOT NULL,
  `FirstName` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LastName` varchar(24) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Salt` varchar(16) NOT NULL,
  `Active` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`Email`),
  UNIQUE KEY `Email_UNIQUE` (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('admin','zVsyBBDb7mMqnmzL+Rp2hDbyOmokKb89I/i9m/rvgh0=','Admin','Admin','Account','cr9d3cB+Ync=',1),('anni@test.com','zf6skNSPQ90w9cxyKOPnLpChVphYW+GYJ+eoahqPV/0=','Donor','Anni','Anderson','zvnwhaOeAYM=',1),('janedoe@test.com','CAQkX/vxyqwVZOmZAN+/ACoYCl/I3a0EtJ5R2r9VARY=','Volunteer','Jane','Doe','0viyV9Juk0Q=',1),('test@test.com','PK+SCRhDRO64+8IsAyl6MwpZOBvOx/HLDYZxe/pxlvk=','Volunteer','Test','User','qmLzTbP8Nw4=',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-12-08 14:34:33
