-- create-tables.sql
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- Copyright Ⓒ 2014-2015 Universiteit Gent
-- 
-- This file is part of the Degage Web Application
-- 
-- Corresponding author (see also AUTHORS.txt)
-- 
-- Kris Coolsaet
-- Department of Applied Mathematics, Computer Science and Statistics
-- Ghent University 
-- Krijgslaan 281-S9
-- B-9000 GENT Belgium
-- 
-- The Degage Web Application is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Affero General Public License as published by
-- the Free Software Foundation, either version 3 of the License, or
-- (at your option) any later version.
-- 
-- The Degage Web Application is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU Affero General Public License for more details.
-- 
-- You should have received a copy of the GNU Affero General Public License
-- along with the Degage Web Application (file LICENSE.txt in the
-- distribution).  If not, see <http://www.gnu.org/licenses/>.

-- Creates the tables

-- Note we use TIMESTAMP only for registering changes in the database
-- User visible dates and times are represented by DATETIME, and have no timezone. For example, if from England you
-- make a reservation of a car, you should do this in Belgian time. Reservation intervals across a daylight saving time border,
-- may be one hour off.
--
-- All times displayed by the web application should therefore be regarded as with respect to a fixed time zone,
-- the time zone of the server. The corresponding Java type is LocalDateTime.
--
-- This choice is not without difficulties, but we think this is the behaviour which the user expects,

SET names utf8;
SET default_storage_engine=INNODB;

-- TODO: split into separate files

-- TABLES
-- ~~~~~~

CREATE TABLE `settings` (
  `setting_name` CHAR(32) NOT NULL,
  `setting_value` VARCHAR(256) NULL DEFAULT NULL,
  `setting_after` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`setting_name`,`setting_after`)
);


CREATE TABLE `files` (
  `file_id` INT NOT NULL AUTO_INCREMENT,
  `file_path` VARCHAR(255) NOT NULL,
  `file_name` VARCHAR(128) NULL,
  `file_content_type` VARCHAR(64) NULL,
  `file_created_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `file_updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`file_id`)
);


CREATE TABLE `addresses` (
  `address_id` INT NOT NULL AUTO_INCREMENT,
  `address_country` VARCHAR(64) NOT NULL DEFAULT 'België',
  `address_city` VARCHAR(64) NOT NULL DEFAULT '',
  `address_zipcode` VARCHAR(12) NOT NULL DEFAULT '',
  `address_street` VARCHAR(64) NOT NULL DEFAULT '',
  `address_number` VARCHAR(12) NOT NULL DEFAULT '',
  `address_created_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `address_updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`address_id`)
);

CREATE TABLE `users` (
	`user_id` INT NOT NULL AUTO_INCREMENT,
	`user_email` VARCHAR(64) NOT NULL,
	`user_password` CHAR(64) NOT NULL,
	`user_firstname` VARCHAR(64) NOT NULL,
	`user_lastname` VARCHAR(64) NOT NULL,
	`user_cellphone` VARCHAR(24),
	`user_phone` VARCHAR(24),
	`user_address_domicile_id` INT,
	`user_address_residence_id` INT,
	`user_driver_license_id` VARCHAR(32),
	`user_driver_license_date` DATE,
	`user_identity_card_id` VARCHAR(32), -- Identiteitskaartnr
	`user_identity_card_registration_nr` VARCHAR(32), -- Rijksregisternummer
	`user_status` ENUM('REGISTERED','FULL_VALIDATING','FULL','BLOCKED','DROPPED','INACTIVE') NOT NULL DEFAULT 'REGISTERED', -- Stadia die de gebruiker moet doorlopen
	`user_damage_history` TEXT,
	`user_agree_terms` BIT(1),
	`user_image_id` INT,
	`user_degage_id` INT,
	`user_date_joined` DATE,
	`user_deposit` INT,
	`user_fee` INT DEFAULT 0, -- one time membership fee, null means unknown
	`user_contract` DATE DEFAULT NULL, -- null means: not signed
	`user_vat` VARCHAR(32),
	`user_last_notified` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`user_created_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`user_updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`user_id`),
	FOREIGN KEY (`user_address_domicile_id`) REFERENCES addresses(`address_id`),
	FOREIGN KEY (`user_address_residence_id`) REFERENCES addresses(`address_id`),
	FOREIGN KEY (`user_image_id`) REFERENCES files(`file_id`),
	UNIQUE INDEX `user_email` (`user_email`)
);

CREATE TABLE `verifications` (
	`verification_ident` CHAR(37) NOT NULL,
	`verification_email` VARCHAR(64) NOT NULL,
	`verification_type` ENUM('REGISTRATION','PWRESET') NOT NULL DEFAULT 'REGISTRATION',
	`verification_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (`verification_ident`)
);

CREATE TABLE idfiles (
    user_id INT NOT NULL,
    file_id INT NOT NULL,
    PRIMARY KEY (user_id, file_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (file_id) REFERENCES files(file_id) ON DELETE CASCADE
);

CREATE TABLE licensefiles (
    user_id INT NOT NULL,
    file_id INT NOT NULL,
    PRIMARY KEY (user_id, file_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (file_id) REFERENCES files(file_id)  ON DELETE CASCADE
);

CREATE TABLE `userroles` (
	`userrole_userid` INT NOT NULL,
	`userrole_role` ENUM('SUPER_USER', 'CAR_OWNER', 'CAR_USER', 'INFOSESSION_ADMIN', 'CONTRACT_ADMIN', 'PROFILE_ADMIN',
	                     'RESERVATION_ADMIN', 'CAR_ADMIN') NOT NULL,
	PRIMARY KEY (`userrole_userid`, `userrole_role`),
	FOREIGN KEY (`userrole_userid`) REFERENCES users(`user_id`)
);

CREATE TABLE `carinsurances` (
	`insurance_id` INT NOT NULL,
	`insurance_name` VARCHAR(64),
	`insurance_expiration` DATE,
	`insurance_contract_id` VARCHAR(64), -- Polisnr
	`insurance_bonus_malus` VARCHAR(64),
	`insurance_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`insurance_id`)
);

CREATE TABLE `technicalcardetails` (
	`details_id` INT NOT NULL,
	`details_car_license_plate` VARCHAR(64),
	`details_car_registration` INT,
	`details_car_chassis_number` VARCHAR(17),
	`details_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`details_id`),
	UNIQUE INDEX `ix_details` (`details_car_license_plate`, `details_car_chassis_number`),
	FOREIGN KEY (`details_car_registration`) REFERENCES files(`file_id`)
);

CREATE TABLE `cars` (
	`car_id` INT NOT NULL AUTO_INCREMENT,
	`car_name` VARCHAR(64) NOT NULL,
	`car_email` VARCHAR(64) NOT NULL,
	`car_type` VARCHAR(64) NOT NULL DEFAULT '0',
	`car_brand` VARCHAR(64) NOT NULL DEFAULT '0',
	`car_location` INT,
	`car_seats` TINYINT UNSIGNED,
	`car_doors` TINYINT UNSIGNED,
	`car_year` INT,
	`car_manual` BIT(1) NOT NULL DEFAULT 0,
	`car_gps` BIT(1) NOT NULL DEFAULT 0,
	`car_hook` BIT(1) NOT NULL DEFAULT 0,
	`car_fuel` ENUM('PETROL','DIESEL', 'BIODIESEL', 'LPG', 'CNG', 'HYBRID', 'ELECTRIC'),
	`car_fuel_economy` INT,
	`car_estimated_value` INT,
	`car_owner_annual_km` INT,
	`car_owner_user_id` INT NOT NULL,
	`car_comments` VARCHAR(4096),
	`car_active` BIT(1) NOT NULL DEFAULT 0,
	`car_images_id` INT,
	`car_deprec` INT, -- cents per 10 km
	`car_deprec_limit` INT, -- no depreciation cost once this number of km is reached
	`car_deprec_last` INT, -- last km for which the depreciation cost was already billed
	`car_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`car_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`car_id`),
	FOREIGN KEY (`car_owner_user_id`) REFERENCES users(`user_id`) ON DELETE CASCADE,
	FOREIGN KEY (`car_location`) REFERENCES addresses(`address_id`) ON DELETE CASCADE,
	FOREIGN KEY (`car_images_id`) REFERENCES files(`file_id`)
);

create table carpreferences (
   user_id int references users(user_id),
   car_id int references cars(car_id),
   primary key (user_id, car_id)
);

CREATE TABLE `reservations` (
	`reservation_id` INT NOT NULL AUTO_INCREMENT,
	-- IMPORTANT: do not change the order of these statuses. Indices are used in code
	-- marked with [ENUM INDEX]
    `reservation_status` ENUM(
        'REFUSED', 'CANCELLED', 'CANCELLED_LATE', -- first those that are 'deleted'
        'REQUEST',  -- no ride available, can be in the past
        'ACCEPTED', -- no ride available, must be in the future
        'REQUEST_DETAILS', 'DETAILS_PROVIDED', 'DETAILS_REJECTED', 'FINISHED',
        'FROZEN'
        ) NOT NULL DEFAULT 'REQUEST',
	`reservation_car_id` INT NOT NULL,
	`reservation_user_id` INT NOT NULL,
	`reservation_owner_id` INT NOT NULL,
	`reservation_privileged` BIT(1) NOT NULL DEFAULT 0,
	`reservation_from` DATETIME NOT NULL,
	`reservation_to` DATETIME NOT NULL,
	`reservation_message` VARCHAR(4096),
	`reservation_archived`  BIT(1) NOT NULL DEFAULT 0,
	`reservation_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`reservation_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`reservation_id`),
	FOREIGN KEY (`reservation_car_id`) REFERENCES cars(`car_id`),
	FOREIGN KEY (`reservation_user_id`) REFERENCES users(`user_id`),
	FOREIGN KEY (`reservation_owner_id`) REFERENCES users(`user_id`)
);

CREATE TABLE `infosessions` (
	`infosession_id` INT NOT NULL AUTO_INCREMENT,
	`infosession_type` ENUM('NORMAL', 'OWNER', 'OTHER') NOT NULL DEFAULT 'NORMAL',
	`infosession_timestamp` TIMESTAMP NULL,
	`infosession_address_id` INT NOT NULL,
	`infosession_host_user_id` INT,
	`infosession_max_enrollees` INT,
	`infosession_comments` VARCHAR(4096),
	`infosession_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`infosession_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`infosession_id`),
	FOREIGN KEY (`infosession_host_user_id`) REFERENCES users(`user_id`),
	FOREIGN KEY (`infosession_address_id`) REFERENCES addresses(`address_id`)
);

CREATE TABLE `infosessionenrollees` ( -- Wie is ingeschreven?
	`infosession_id` INT NOT NULL,
	`infosession_enrollee_id` INT NOT NULL,
	`infosession_enrollment_status` ENUM('ENROLLED', 'PRESENT', 'ABSENT') NOT NULL DEFAULT 'ENROLLED',
	PRIMARY KEY (`infosession_id`, `infosession_enrollee_id`),
	FOREIGN KEY (`infosession_enrollee_id`) REFERENCES users(`user_id`),
	FOREIGN KEY (`infosession_id`) REFERENCES infosessions(`infosession_id`) ON DELETE CASCADE
);

CREATE VIEW `infosessions_extended` AS
    SELECT
        infosession_id, infosession_type,
        infosession_timestamp, infosession_max_enrollees, infosession_comments,
        address_id, address_country, address_city, address_zipcode, address_street, address_number,
        user_id, user_firstname, user_lastname, user_phone, user_email, user_status, user_cellphone, user_degage_id,
        count(infosession_enrollee_id) AS enrollee_count
    FROM infosessions AS ses
        JOIN users ON infosession_host_user_id = user_id
        JOIN addresses ON infosession_address_id = address_id
        LEFT JOIN infosessionenrollees USING (infosession_id)
    GROUP BY infosession_id;

CREATE TABLE `carprivileges` (
	`car_privilege_user_id` INT NOT NULL,
	`car_privilege_car_id` INT NOT NULL,
	PRIMARY KEY (`car_privilege_user_id`,`car_privilege_car_id`),
	FOREIGN KEY (`car_privilege_user_id`) REFERENCES users(`user_id`),
	FOREIGN KEY (`car_privilege_car_id`) REFERENCES cars(`car_id`)
);

CREATE TABLE `carcostcategories` (
    category_id INT NOT NULL,
    category_description TEXT,
    PRIMARY KEY (`category_id`)
);

CREATE TABLE `carcosts` (
	`car_cost_id` INT NOT NULL AUTO_INCREMENT,
    `car_cost_category_id` INT NOT NULL,
	`car_cost_car_id` INT NOT NULL,
	`car_cost_proof` INT,
	`car_cost_amount` INT NOT NULL,
	`car_cost_spread` INT NOT NULL DEFAULT 12,
	`car_cost_description` TEXT,
	`car_cost_status` ENUM('REQUEST','ACCEPTED', 'REFUSED', 'FROZEN') NOT NULL DEFAULT 'REQUEST', -- approved by car_admin
	`car_cost_time` DATE,
	`car_cost_mileage` INT,
	`car_cost_start` DATE,
	`car_cost_already_paid` INT NOT NULL DEFAULT 0,
	`car_cost_comment` VARCHAR(4096),
	`car_cost_archived` BIT(1) NOT NULL DEFAULT 0,
	`car_cost_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`car_cost_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`car_cost_id`),
	FOREIGN KEY (`car_cost_car_id`) REFERENCES cars(`car_id`),
	FOREIGN KEY (`car_cost_category_id`) REFERENCES carcostcategories(`category_id`),
	FOREIGN KEY (`car_cost_proof`) REFERENCES files(`file_id`)
);

CREATE TABLE `carrides` (
  `car_ride_car_reservation_id` INT NOT NULL, -- also primary key
  `car_ride_start_km` INTEGER NOT NULL DEFAULT 0,
  `car_ride_end_km` INTEGER NOT NULL DEFAULT 0,
  `car_ride_damage` BIT(1) NOT NULL DEFAULT 0,
  `car_ride_cost` DECIMAL(19,4) DEFAULT NULL,
  `car_ride_billed` DATE DEFAULT NULL,
  `car_ride_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `car_ride_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`car_ride_car_reservation_id`),
  FOREIGN KEY (`car_ride_car_reservation_id`) REFERENCES reservations(`reservation_id`)
);

-- view of trips that are not archived
CREATE VIEW `trips` AS
    SELECT
       reservation_id,
       reservation_status,
       reservation_car_id,
       reservation_user_id,
       reservation_owner_id,
       reservation_privileged,
       reservation_from,
       reservation_to,
       reservation_message,
       reservation_created_at,
       car_ride_start_km,
       car_ride_end_km,
       car_ride_damage
    FROM reservations
         LEFT JOIN carrides ON reservation_id = car_ride_car_reservation_id
         WHERE NOT reservation_archived;

CREATE TABLE `refuels` (
	`refuel_id` INT NOT NULL AUTO_INCREMENT,
	`refuel_car_ride_id` INT NOT NULL,
	`refuel_file_id` INT,
	`refuel_eurocents` INT,
	`refuel_status` ENUM('REQUEST','ACCEPTED', 'REFUSED', 'FROZEN') NOT NULL DEFAULT 'REQUEST',
	`refuel_km` INTEGER,
	`refuel_amount` VARCHAR(16), -- amount of fuel, free format
	`refuel_message` VARCHAR(4096),
	`refuel_archived`  BIT(1) NOT NULL DEFAULT 0,
   	`refuel_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   	`refuel_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`refuel_id`),
	FOREIGN KEY (`refuel_car_ride_id`) REFERENCES reservations(`reservation_id`),
	FOREIGN KEY (`refuel_file_id`) REFERENCES files(`file_id`)
);

CREATE TABLE `damages` (
	`damage_id` INT NOT NULL AUTO_INCREMENT,
	`damage_car_ride_id` INT NOT NULL,
	`damage_description` TEXT,
	`damage_finished` BIT(1) NOT NULL DEFAULT 0,
	`damage_time` DATE,
   	`damage_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   	`damage_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`damage_id`),
	FOREIGN KEY (`damage_car_ride_id`) REFERENCES carrides(`car_ride_car_reservation_id`)
);

CREATE TABLE damagefiles (
    damage_id INT NOT NULL,
    file_id INT NOT NULL,
    PRIMARY KEY (damage_id, file_id),
    FOREIGN KEY (damage_id) REFERENCES damages(damage_id),
    FOREIGN KEY (file_id) REFERENCES files(file_id)
);

CREATE TABLE `damagelogs` (
	`damage_log_id` INT NOT NULL AUTO_INCREMENT,
	`damage_log_damage_id` INT NOT NULL,
	`damage_log_description` TEXT,
   	`damage_log_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   	`damage_log_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`damage_log_id`),
	FOREIGN KEY (`damage_log_damage_id`) REFERENCES damages(`damage_id`)
);

CREATE TABLE `messages` ( -- from user to user != notifications
	`message_id` INT NOT NULL AUTO_INCREMENT,
	`message_from_user_id` INT NOT NULL,
	`message_to_user_id` INT NOT NULL,
	`message_read` BIT(1) NOT NULL DEFAULT 0,
	`message_subject` VARCHAR(255) NOT NULL DEFAULT 'Bericht van een Dégage-gebruiker',
	`message_body` TEXT NOT NULL,
   `message_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   `message_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`message_id`),
	FOREIGN KEY (`message_from_user_id`) REFERENCES users(`user_id`),
	FOREIGN KEY (`message_to_user_id`) REFERENCES users(`user_id`)
);

CREATE TABLE `notifications` ( -- from system to user
	`notification_id` INT NOT NULL AUTO_INCREMENT,
	`notification_user_id` INT NOT NULL,
	`notification_read` BIT(1) NOT NULL DEFAULT 0,
	`notification_subject` VARCHAR(255),
	`notification_body` TEXT,
   `notification_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   `notification_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	PRIMARY KEY (`notification_id`),
	FOREIGN KEY (`notification_user_id`) REFERENCES users(`user_id`)
);

CREATE TABLE announcements (
    `announcement_key` VARCHAR(16),
    `announcement_description` VARCHAR(64),
    `announcement_html` TEXT NOT NULL,
    `announcement_md` TEXT NOT NULL,
    `announcement_updated_at` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (announcement_key)
);

CREATE TABLE `approvals` (
  `approval_id` INT NOT NULL AUTO_INCREMENT,
  `approval_user` INT NULL DEFAULT NULL,
  `approval_admin` INT NULL DEFAULT NULL,
  `approval_submission` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `approval_date` TIMESTAMP NULL DEFAULT NULL,
  `approval_status` ENUM('PENDING','ACCEPTED','DENIED') NOT NULL DEFAULT 'PENDING',
  `approval_infosession` INT NULL DEFAULT NULL,
  `approval_user_message` TEXT NULL,
  `approval_admin_message` TEXT NULL,
  PRIMARY KEY (`approval_id`),
  INDEX `FK_approval_user` (`approval_user`),
  INDEX `FK_approval_admin` (`approval_admin`),
  INDEX `FK_approval_session` (`approval_infosession`),
  CONSTRAINT `FK_approval_admin` FOREIGN KEY (`approval_admin`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_approval_session` FOREIGN KEY (`approval_infosession`) REFERENCES `infosessions` (`infosession_id`),
  CONSTRAINT `FK_approval_user` FOREIGN KEY (`approval_user`) REFERENCES `users` (`user_id`)
);

CREATE TABLE `jobs` (
  `job_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `job_type` ENUM('IS_REMINDER') NOT NULL DEFAULT 'IS_REMINDER',
  `job_ref_id` INT NULL DEFAULT '0',
  `job_time` TIMESTAMP,
  `job_finished` BIT(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`job_id`)
);


CREATE TABLE `receipts` (
  `receipt_id` INT NOT NULL AUTO_INCREMENT,
  `receipt_name` CHAR(32) NOT NULL,
  `receipt_date` DATE NULL DEFAULT NULL,
  `receipt_fileID` INT NULL DEFAULT NULL,
  `receipt_userID` INT NOT NULL,
  `receipt_price` DECIMAL(19,4),
  PRIMARY KEY (`receipt_id`),
  FOREIGN KEY (`receipt_fileID`) REFERENCES files(`file_id`),
  FOREIGN KEY (`receipt_userID`) REFERENCES users(`user_id`)
);

CREATE TABLE `billing` (
   `billing_id` INT NOT NULL AUTO_INCREMENT,
   `billing_description` CHAR(128) NOT NULL, -- ex. Kwartaal 1 2015
   `billing_prefix` CHAR(10) NOT NULL, -- used on invoice ex. 1-2015
   `billing_limit` DATE NOT NULL, -- only what is *strictly* before this date will be billed
   `billing_start` DATE NOT NULL, -- only for informational purposes
   `billing_simulation_date` DATE, -- date at which the simulation was run
   `billing_drivers_date` DATE, -- date at which the drivers invoices were created
   `billing_owners_date` DATE, -- date at which the owners invoices were created
   `billing_status` ENUM (
       'CREATED', 'PREPARING', 'SIMULATION', 'USERS_DONE', 'ALL_DONE', 'ARCHIVED'
   ) NOT NULL DEFAULT 'CREATED',
   `billing_created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`billing_id`)
);

CREATE TABLE `cars_billed` (
    billing_id INT REFERENCES billing(billing_id),
    car_id INT REFERENCES cars(car_id),
    included BIT(1) NOT NULL DEFAULT b'0',
    PRIMARY KEY (`billing_id`, `car_id`)
);

CREATE TABLE `km_price` (
    `km_price_billing_id` INT NOT NULL,
    `km_price_from` INT NOT NULL,      -- typically 1, 101, 201
    `km_price_eurocents` INT NOT NULL, -- price for interval - as printed
    `km_price_factor` INT NOT NULL,    -- cummulative price (for computations)
    PRIMARY KEY (`km_price_billing_id`, `km_price_from`),
    FOREIGN KEY (`km_price_billing_id`) REFERENCES billing(billing_id)
);

CREATE TABLE `b_trip` (
    bt_billing_id INT REFERENCES billing(billing_id),
    bt_reservation_id INT REFERENCES reservations(reservation_id),
    bt_user_id INT REFERENCES users(user_id),
    bt_privileged BIT(1) NOT NULL DEFAULT 0,
    bt_car_id INT REFERENCES cars(car_id),
    bt_car_name VARCHAR(64) NOT NULL,
    bt_datetime DATETIME NOT NULL,
    bt_km INT NOT NULL DEFAULT 0,
    bt_km_cost INT NOT NULL DEFAULT 0, -- total cost of km in eurocent
    PRIMARY KEY (bt_billing_id, bt_reservation_id)
);

CREATE TABLE `b_fuel` (
    bf_billing_id INT REFERENCES billing(billing_id),
    bf_refuel_id INT REFERENCES refuels(refuel_id),
    bf_reservation_id INT REFERENCES reservations(`reservation_id`),
    bf_car_id INT REFERENCES cars(car_id),
    bf_user_id INT REFERENCES users(user_id),
    bf_privileged BIT(1) NOT NULL DEFAULT 0,
    bf_car_name VARCHAR(64) NOT NULL,
    bf_datetime DATETIME NOT NULL,
    bf_fuel_cost INT NOT NULL DEFAULT 0, -- fuel paid (in eurocent)
    PRIMARY KEY (bf_billing_id, bf_refuel_id)
);

CREATE TABLE b_user (
    bu_billing_id INT REFERENCES billing(billing_id),
    bu_user_id INT REFERENCES users(user_id),
    bu_seq_nr INT,
    bu_km_cost INT NOT NULL DEFAULT 0, -- total cost of kms over all trips (in drivers invoice)
    bu_fuel_cost INT NOT NULL DEFAULT 0, -- total cost of fuel over all trips (in drivers invoice)
    PRIMARY KEY (bu_billing_id, bu_user_id)
);

CREATE TABLE b_cars (
    bc_billing_id INT REFERENCES billing(billing_id),
    bc_car_id INT REFERENCES cars(car_id),
    bc_first_km INT,
    bc_last_km INT,
    bc_total_km INT,  -- sum of all trips  (need not be the same as bc_last_km - bc_first_km)
    bc_owner_km INT,  -- kilometers driven by privileged users
    bc_deprec_km INT, -- kilometers counted for depreciation
    bc_fuel_total INT,      -- total fuel cost for this period
    bc_fuel_owner INT,      -- fuel already paid by owner for this period
    bc_fuel_due INT,        -- fuel to be paid by owner for this period
    bc_deprec_recup INT,    -- deprecation cost to be recuperated by owner
    bc_costs INT,           -- total car costs for this period
    bc_costs_recup INT,     -- recuperation of these car costs by owner
    bc_seq_nr INT,
    PRIMARY KEY (bc_billing_id, bc_car_id)
);

CREATE TABLE b_costs (
    bcc_billing_id INT REFERENCES billing(billing_id),
    bcc_cost_id INT REFERENCES carcosts(car_cost_id),
    bcc_refunded INT, -- amount actually refunded for this cost during this period
    PRIMARY KEY (bcc_billing_id, bcc_cost_id)
);

-- Keeps track of car privileges for billings (reporting only)
CREATE TABLE `b_privileges` (
  `bp_billing_id` INT NOT NULL,
  `bp_user_id`    INT NOT NULL,
  `bp_car_id`     INT NOT NULL,
  PRIMARY KEY (`bp_billing_id`, `bp_user_id`, `bp_car_id`),
  FOREIGN KEY (`bp_billing_id`) REFERENCES billing (`billing_id`),
  FOREIGN KEY (`bp_user_id`) REFERENCES users (`user_id`),
  FOREIGN KEY (`bp_car_id`) REFERENCES cars (`car_id`)
);


-- EVENTS
-- ~~~~~~

DROP EVENT IF EXISTS update_reservation_status;
CREATE EVENT update_reservation_status
   ON SCHEDULE EVERY 6 MINUTE
COMMENT 'Updates reservations from future to past'
DO
   UPDATE reservations SET reservation_status='REQUEST_DETAILS'
     WHERE reservation_from < NOW() AND reservation_status = 'ACCEPTED';

DELIMITER $$

DROP FUNCTION IF EXISTS structured_comment $$
CREATE FUNCTION structured_comment (b_id INT, xtra INT, ix INT, id INT)
   RETURNS VARCHAR(20) DETERMINISTIC
BEGIN
   DECLARE pre INT;
   DECLARE mid INT;
   DECLARE e INT;
   DECLARE m INT;

   SET pre = (3*b_id + xtra) % 1000;
   SET mid = ix % 10000;
   SET e = id % 1000;
   SET m = (10000000 * pre + 1000 * mid + e) % 97;
   IF m = 0 THEN
      SET m = 97;
   END IF;
   RETURN CONCAT ('+++',
       SUBSTRING(1000+pre FROM -3),
       '/',
       SUBSTRING(10000+mid FROM -4),
       '/',
       SUBSTRING(1000+e FROM -3),
       SUBSTRING(100+m FROM -2),
       '+++'
   );
END $$

DELIMITER ;

-- reporting
CREATE VIEW b_car_overview AS
  SELECT
     bc_billing_id AS billing_id,
     bc_car_id AS car_id,
     car_name AS name,
     concat(user_lastname, ', ', user_firstname) AS owner_name,
     bc_fuel_due - bc_fuel_owner AS fuel,
     - bc_deprec_recup AS deprec,
     - bc_costs_recup AS  costs,
     bc_fuel_due - bc_fuel_owner - bc_deprec_recup - bc_costs_recup AS total,
     structured_comment(bc_billing_id,1,bc_seq_nr,bc_car_id) as sc,
     bc_seq_nr as seq_nr,
     bc_total_km,
     bc_deprec_km,
     bc_costs,
     bc_first_km,
     bc_last_km,
     bc_fuel_total,
     car_deprec,
     car_deprec_limit
  FROM b_cars
      JOIN cars ON cars.car_id = b_cars.bc_car_id
      JOIN users ON users.user_id = cars.car_owner_user_id;

CREATE VIEW b_user_overview AS
  SELECT
     bu_billing_id AS billing_id,
     bu_user_id AS user_id,
     concat(user_lastname, ', ', user_firstname) AS name,
     bu_km_cost AS km,
     bu_fuel_cost AS fuel,
     bu_km_cost - bu_fuel_cost AS total,
     structured_comment(bu_billing_id,0,bu_seq_nr,bu_user_id) as sc,
     bu_seq_nr as seq_nr
  FROM b_user JOIN users ON users.user_id = b_user.bu_user_id;

CREATE VIEW b_user_km
   AS SELECT
        bt_billing_id,
        bt_user_id,
        km_price_from,
        SUM(bt_km - km_price_from + 1) AS sum_of_excess_kms
    FROM b_trip
      JOIN km_price ON bt_km >= km_price_from AND km_price_billing_id = bt_billing_id
    WHERE NOT bt_privileged
    GROUP BY bt_billing_id, bt_user_id, km_price_from;

-- TRIGGERS
-- ~~~~~~~~

-- TODO: can this not be done with a default value?

DELIMITER $$

CREATE TRIGGER users_create BEFORE INSERT ON users FOR EACH ROW
BEGIN
   INSERT INTO addresses VALUES ();
   SET NEW.user_address_domicile_id = last_insert_id();
   INSERT INTO addresses VALUES ();
   SET NEW.user_address_residence_id = last_insert_id();
END $$

CREATE TRIGGER cars_make AFTER INSERT ON cars FOR EACH ROW
BEGIN
  INSERT INTO technicalcardetails(details_id) VALUES (new.car_id);
  INSERT INTO carinsurances(insurance_id) VALUES (new.car_id);
  INSERT INTO carassistances(assistance_id) VALUES (new.car_id);
END $$

CREATE TRIGGER cars_create BEFORE INSERT ON cars FOR EACH ROW
BEGIN
  INSERT INTO addresses VALUES ();
  SET NEW.car_location = last_insert_id();
END $$

CREATE TRIGGER infosession_create BEFORE INSERT ON infosessions FOR EACH ROW
BEGIN
  INSERT INTO addresses VALUES ();
  SET NEW.infosession_address_id = last_insert_id();
END $$

CREATE TRIGGER reservations_ins BEFORE INSERT ON reservations FOR EACH ROW
BEGIN
    DECLARE privileged int default 0;

    SELECT 1 INTO privileged FROM carprivileges
       WHERE car_privilege_car_id = NEW.reservation_car_id AND  car_privilege_user_id = NEW.reservation_user_id;
    SELECT 1 INTO privileged FROM cars
       WHERE car_id = NEW.reservation_car_id AND car_owner_user_id = NEW.reservation_user_id;

    SET NEW.reservation_owner_id =
       ( SELECT car_owner_user_id FROM cars WHERE car_id = NEW.reservation_car_id );

    IF NEW.reservation_from < NOW() THEN
        SET NEW.reservation_status = 'REQUEST_DETAILS';
    ELSEIF privileged THEN
        SET NEW.reservation_status = 'ACCEPTED';
    ELSE
        SET NEW.reservation_status = 'REQUEST';
    END IF;

    IF new.reservation_created_at IS NULL THEN
        SET new.reservation_created_at = now();
    END IF;
END $$

CREATE TRIGGER billing_ins AFTER INSERT ON billing FOR EACH ROW
BEGIN
  DECLARE old INT;

  INSERT INTO cars_billed(billing_id,car_id,included)
     SELECT NEW.billing_id,car_id,FALSE FROM cars;

  SELECT MAX(km_price_billing_id) FROM km_price INTO old;

  INSERT INTO km_price(km_price_billing_id,km_price_from,km_price_eurocents,km_price_factor)
      SELECT NEW.billing_id,km_price_from,km_price_eurocents,km_price_factor
      FROM km_price WHERE km_price_billing_id = old;
END $$

DELIMITER ;

