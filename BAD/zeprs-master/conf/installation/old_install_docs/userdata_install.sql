-- MySQL dump 10.10
--
-- Host: localhost    Database: userdata
-- ------------------------------------------------------
-- Server version	5.0.24a-community-nt

/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="044b48405b474c455645475041565b5741505b47484d414a503944">[email protected]</a>@CHARACTER_SET_CLIENT */;
/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5b14171f0418131a091a180f1e0904081e0f04091e080e170f08661b">[email protected]</a>@CHARACTER_SET_RESULTS */;
/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9cd3d0d8c3dfd3d0d0ddc8d5d3d2c3dfd3d2d2d9dfc8d5d3d2a1dc">[email protected]</a>@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3b74777f646f72767e646174757e067b">[email protected]</a>@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a6e9eae2f9f3e8eff7f3e3f9e5eee3e5edf59be6">[email protected]</a>@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="216e6d657e676e736468666f7e6a64787e626964626a721c61">[email protected]</a>@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5b14171f04080a170416141f1e661b">[email protected]</a>@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="47080b031814160b1809081302147a07">[email protected]</a>@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `userdata`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `userdata` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `userdata`;

--
-- Table structure for table `account_permissions`
--

DROP TABLE IF EXISTS `account_permissions`;
CREATE TABLE `account_permissions` (
  `pa_id` int(11) NOT NULL auto_increment,
  `pa_role_name_fk` int(11) NOT NULL default '0',
  `pa_username_fk` varchar(50) NOT NULL default '',
  `pa_ap_id_fk` int(11) NOT NULL default '0',
  PRIMARY KEY  (`pa_id`)
) ENGINE=MyISAM AUTO_INCREMENT=710 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `account_permissions`
--


/*!40000 ALTER TABLE `account_permissions` DISABLE KEYS */;
LOCK TABLES `account_permissions` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `account_permissions` ENABLE KEYS */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `firstname` varchar(50) NOT NULL default '',
  `nickname` varchar(50) NOT NULL default '',
  `lastname` varchar(50) NOT NULL default '',
  `email` varchar(50) character set latin1 collate latin1_bin NOT NULL default '',
  `label` varchar(50) character set latin1 collate latin1_bin NOT NULL default '',
  `owner` varchar(50) character set latin1 collate latin1_bin NOT NULL default '',
  `ad_phone` varchar(15) NOT NULL default '',
  `ad_mobile` varchar(15) NOT NULL default '',
  `ad_clinic_id_fk` int(11) NOT NULL default '0',
  `ad_sex` char(1) NOT NULL default '',
  `ad_marital_status_fk` int(11) NOT NULL default '0',
  `ad_job_title_fk` char(2) NOT NULL default '',
  `ad_reg_card` varchar(15) NOT NULL default '',
  `ad_MOH_file_num` varchar(15) NOT NULL default '',
  `ad_MC_num` varchar(15) NOT NULL default '',
  `ad_permissions_fk` char(2) NOT NULL default '3',
  `ad_man_num` varchar(30) NOT NULL default '',
  `ad_hobbies` varchar(200) NOT NULL default '',
  `ad_NCert_num` varchar(30) NOT NULL default '',
  KEY `NICKINDEX` (`nickname`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `address`
--


/*!40000 ALTER TABLE `address` DISABLE KEYS */;
LOCK TABLES `address` WRITE;
INSERT INTO `address` VALUES ('ZEPRS Admin','zepadmin','User','','','global','','',31,'M',5,'1','','','','3','003351','','');
INSERT INTO `address` VALUES ('Local','luser','User','','','global','','',31,'M',5,'1','','','','3','003351','','');
INSERT INTO `address` VALUES ('Report','reports','Viewer','','','global','','',31,'M',5,'1','','','','3','003351','','');
INSERT INTO `address` VALUES ('Clinic','clinic','User','','','global','','',31,'M',5,'1','','','','3','003351','','');
INSERT INTO `address` VALUES ('View','viewall','All','','','global','','',31,'M',5,'1','','','','3','003351','','');
INSERT INTO `address` VALUES ('User','useradmin','Admin','<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b3c6c0d6c1d2d7dedaddf3d0dad7c1c99ddcc1d4">[email protected]</a>','','global','','',31,'M',5,'1','','','','3','003351','','');
INSERT INTO `address` VALUES ('Clerk','clerk','Jane','','','global','','',31,'M',5,'1','','','','3','003351','','');
INSERT INTO `address` VALUES ('Demo','demo','User','','','global','','',31,'M',5,'1','','','','3','003351','','');
UNLOCK TABLES;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;

--
-- Table structure for table `applications`
--

DROP TABLE IF EXISTS `applications`;
CREATE TABLE `applications` (
  `ap_id` int(11) NOT NULL default '0',
  `ap_name` varchar(50) NOT NULL default ''
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `applications`
--


/*!40000 ALTER TABLE `applications` DISABLE KEYS */;
LOCK TABLES `applications` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `applications` ENABLE KEYS */;

--
-- Table structure for table `facilities`
--

DROP TABLE IF EXISTS `facilities`;
CREATE TABLE `facilities` (
  `fa_clinic_id` int(100) NOT NULL auto_increment,
  `fa_institution` varchar(100) NOT NULL default '',
  `fa_description` varchar(100) NOT NULL default '',
  `fa_abreviation` varchar(10) NOT NULL default '',
  `fa_VOIP_phone` int(15) NOT NULL default '0',
  `fa_analogue_phone` varchar(15) NOT NULL default '',
  PRIMARY KEY  (`fa_clinic_id`)
) ENGINE=MyISAM AUTO_INCREMENT=44 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `facilities`
--


/*!40000 ALTER TABLE `facilities` DISABLE KEYS */;
LOCK TABLES `facilities` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `facilities` ENABLE KEYS */;

--
-- Table structure for table `job_title`
--

DROP TABLE IF EXISTS `job_title`;
CREATE TABLE `job_title` (
  `jt_job_title_pk` int(11) NOT NULL auto_increment,
  `jt_job_title_name` varchar(100) NOT NULL default '',
  `jt_sort_order` int(11) NOT NULL default '0',
  PRIMARY KEY  (`jt_job_title_pk`)
) ENGINE=MyISAM AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `job_title`
--


/*!40000 ALTER TABLE `job_title` DISABLE KEYS */;
LOCK TABLES `job_title` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `job_title` ENABLE KEYS */;

--
-- Table structure for table `marital_status`
--

DROP TABLE IF EXISTS `marital_status`;
CREATE TABLE `marital_status` (
  `ms_marital_status_pk` int(11) NOT NULL auto_increment,
  `ms_marital_status_name` varchar(50) NOT NULL default '',
  PRIMARY KEY  (`ms_marital_status_pk`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `marital_status`
--


/*!40000 ALTER TABLE `marital_status` DISABLE KEYS */;
LOCK TABLES `marital_status` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `marital_status` ENABLE KEYS */;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `pe_permissions_pk` int(11) NOT NULL auto_increment,
  `pe_role_name` varchar(20) NOT NULL default '',
  PRIMARY KEY  (`pe_permissions_pk`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `permissions`
--


/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
LOCK TABLES `permissions` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
/*!40103 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c6928f8b83999c898883fb86898a82">[email protected]</a>_TIME_ZONE */;

/*!40101 SET S<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="17465b485a5853522a57585b53">[email protected]</a>_SQL_MODE */;
/*!40014 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8bcdc4d9cec2ccc5d4c0ced2d4c8c3cec8c0d8b6cbc4c7cf">[email protected]</a>_FOREIGN_KEY_CHECKS */;
/*!40014 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5d0813140c0818021e15181e160e601d121119">[email protected]</a>_UNIQUE_CHECKS */;
/*!40101 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="67242f263526243322353834223338242b2e2229335a27282b23">[email protected]</a>_CHARACTER_SET_CLIENT */;
/*!40101 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="185b50594a595b4c5d4a474b5d4c474a5d4b4d544c4b255857545c">[email protected]</a>_CHARACTER_SET_RESULTS */;
/*!40101 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5c1f1310101d08151312031f131212191f08151312611c131018">[email protected]</a>_COLLATION_CONNECTION */;
/*!40111 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c89b99849786879c8d9bf58887848c">[email protected]</a>_SQL_NOTES */;