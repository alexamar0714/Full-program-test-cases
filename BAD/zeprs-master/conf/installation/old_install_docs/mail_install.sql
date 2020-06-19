-- MySQL dump 10.10
--
-- Host: localhost    Database: mail
-- ------------------------------------------------------
-- Server version	5.0.24a-community-nt

/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="1c535058435f545d4e5d5f48594e434f5948435f5055595248215c">[email protected]</a>@CHARACTER_SET_CLIENT */;
/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="89c6c5cdd6cac1c8dbc8caddccdbd6daccddd6dbccdadcc5dddab4c9">[email protected]</a>@CHARACTER_SET_RESULTS */;
/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="2e61626a716d6162626f7a676160716d6160606b6d7a676160136e">[email protected]</a>@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="c7888b8398938e8a82989d888982fa87">[email protected]</a>@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f7b8bbb3a8a2b9bea6a2b2a8b4bfb2b4bca4cab7">[email protected]</a>@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="420d0e061d040d10070b050c1d09071b1d010a070109117f02">[email protected]</a>@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e3acafa7bcb0b2afbcaeaca7a6dea3">[email protected]</a>@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="612e2d253e32302d3e2f2e3524325c21">[email protected]</a>@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `mail`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `mail` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `mail`;

--
-- Table structure for table `accountuser`
--

DROP TABLE IF EXISTS `accountuser`;
CREATE TABLE `accountuser` (
  `username` varchar(50) character set latin1 collate latin1_bin NOT NULL default '',
  `password` varchar(41) character set latin1 collate latin1_bin NOT NULL default '',
  `prefix` varchar(50) NOT NULL default '',
  `domain_name` varchar(255) NOT NULL default '',
  `date_created` datetime NOT NULL default '2003-10-01 00:00:00',
  `date_passwd_changed` datetime NOT NULL default '0000-00-00 00:00:00',
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `accountuser`
--


/*!40000 ALTER TABLE `accountuser` DISABLE KEYS */;
LOCK TABLES `accountuser` WRITE;
INSERT INTO accountuser VALUES ('zepadmin',password('zepadmin11'),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');
INSERT INTO accountuser VALUES ('luser',password(''),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');
INSERT INTO accountuser VALUES ('reports',password('reports11'),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');
INSERT INTO accountuser VALUES ('clinic',password('clinic11'),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');
INSERT INTO accountuser VALUES ('viewall',password('viewall'),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');
INSERT INTO accountuser VALUES ('useradmin',password('useradmin01'),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');
INSERT INTO accountuser VALUES ('clerk',password('clerk01'),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');
INSERT INTO accountuser VALUES ('demo',password('demo11'),'','','2003-10-30 09:19:47','2003-10-30 09:19:47');

UNLOCK TABLES;
/*!40000 ALTER TABLE `accountuser` ENABLE KEYS */;
/*!40103 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d3879a9e968c899c9d96ee939c9f97">[email protected]</a>_TIME_ZONE */;

/*!40101 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="3c6f6d706371737879017c737078">[email protected]</a>_SQL_MODE */;
/*!40014 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="66202934232f2128392d233f39252e23252d355b26292a22">[email protected]</a>_FOREIGN_KEY_CHECKS */;
/*!40014 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b4e1fafde5e1f1ebf7fcf1f7ffe789f4fbf8f0">[email protected]</a>_UNIQUE_CHECKS */;
/*!40101 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="5b18131a091a180f1e0904081e0f041817121e150f661b14171f">[email protected]</a>_CHARACTER_SET_CLIENT */;
/*!40101 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="44070c051605071001161b1701101b1601171108101779040b0800">[email protected]</a>_CHARACTER_SET_RESULTS */;
/*!40101 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="db989497979a8f92949584989495959e988f929495e69b94979f">[email protected]</a>_COLLATION_CONNECTION */;
/*!40111 SET <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6734362b3829283322345a27282b23">[email protected]</a>_SQL_NOTES */;
