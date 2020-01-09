/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  simz
 * Created: Jan 2, 2020
 */

CREATE TABLE IF NOT EXISTS `auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` varchar(38) DEFAULT NULL,
  `account_expired` bit(1) DEFAULT NULL,
  `account_locked` bit(1) DEFAULT NULL,
  `credentials_expired` bit(1) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `verified` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `USERNAME_UK_auth_user` (`username`)
); 

CREATE TABLE IF NOT EXISTS `oauth_client_details` (
  `client_id` varchar(255) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
); 

CREATE TABLE IF NOT EXISTS `oauth_client_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
);

CREATE TABLE IF NOT EXISTS `oauth_access_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication_id` varchar(255) NOT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  `authentication` mediumblob,
  `refresh_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
);

CREATE TABLE IF NOT EXISTS `oauth_refresh_token` (
  `token_id` varchar(255) DEFAULT NULL,
  `token` mediumblob,
  `authentication` mediumblob
);

CREATE TABLE IF NOT EXISTS `oauth_code` (
  `code` varchar(255) DEFAULT NULL,
  `authentication` mediumblob
);

CREATE TABLE IF NOT EXISTS `oauth_approvals` (
  `userId` varchar(255) DEFAULT NULL,
  `clientId` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `expiresAt` timestamp NULL DEFAULT NULL,
  `lastModifiedAt` timestamp NULL DEFAULT NULL
);

CREATE TABLE IF NOT EXISTS `auth_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` varchar(38) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `permission_group` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UK_auth_permission` (`name`)
);

CREATE TABLE IF NOT EXISTS `auth_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_id` varchar(38) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_disabled` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UK_auth_role` (`name`)
);

CREATE TABLE IF NOT EXISTS `auth_permission_role` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  KEY `FK5aakahneupoy14p2each26kt1` (`permission_id`),
  KEY `fk_role_permission_id` (`role_id`),
  UNIQUE KEY `ROLE_PERMISSION_UK_auth_permission_role` (`role_id`, `permission_id`),
  CONSTRAINT `FK5aakahneupoy14p2each26kt1` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `fk_role_permission_id` FOREIGN KEY (`role_id`) REFERENCES `auth_role` (`id`)
);

CREATE TABLE IF NOT EXISTS `auth_role_user` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  KEY `FK3j1wsr4gpvhijdh35u8j02jy2` (`role_id`),
  KEY `fk_user_roles_id` (`user_id`),
  UNIQUE KEY `USER_ROLE_UK_auth_role_user` (`user_id`, `role_id`),
  CONSTRAINT `FK3j1wsr4gpvhijdh35u8j02jy2` FOREIGN KEY (`role_id`) REFERENCES `auth_role` (`id`),
  CONSTRAINT `fk_user_roles_id` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
);

CREATE TABLE IF NOT EXISTS `sequence_data` (
  `sequence_name` varchar(255) NOT NULL,
  `company_id` varchar(38) DEFAULT NULL,
  `sequence_cur_value` bigint(20) DEFAULT NULL,
  `sequence_cycle` bit(1) DEFAULT NULL,
  `sequence_increment` int(11) DEFAULT NULL,
  `sequence_max_value` bigint(20) DEFAULT NULL,
  `sequence_min_value` int(11) DEFAULT NULL,
  PRIMARY KEY (`sequence_name`),
  UNIQUE KEY `sequence_name_UK_auth_sequence_data` (`sequence_name`)
)

# IGNORE IF ERROR OCCURS DURING INSERT

INSERT IGNORE INTO `oauth_client_details` (client_id,client_secret, resource_ids,scope,authorized_grant_types, web_server_redirect_uri,authorities, access_token_validity,refresh_token_validity, additional_information, autoapprove)
VALUES( 'smarthealth-web-client','{bcrypt}$2a$10$EOs8VROb14e7ZnydvXECA.4LoIhPOoFHKvVF/iBZ/ker17Eocz4Vi', 'USER_CLIENT_RESOURCE,USER_ADMIN_RESOURCE,SMARTHEALTH_API', 'role_admin,role_user', 'authorization_code,password,refresh_token,implicit', NULL,NULL, 900,3600, '{}',NULL);

INSERT IGNORE INTO `auth_permission` (NAME) VALUES
('can_create_user'),
('can_update_user'),
('can_read_user'),
('can_delete_user');

INSERT IGNORE INTO `auth_role` (name) VALUES ('role_admin'),('role_user');

INSERT IGNORE INTO `auth_permission_role` (permission_id, role_id) VALUES
(1,1),
(2,1),
(3,1),
(4,1),
(3,2);

INSERT IGNORE INTO `auth_user` VALUES (1,NULL,_binary '\0',_binary '\0',_binary '\0','kelsas@gmail.com',_binary '',NULL,'Kelsas Admin','{bcrypt}$2a$10$EOs8VROb14e7ZnydvXECA.4LoIhPOoFHKvVF/iBZ/ker17Eocz4Vi','admin',_binary ''),
(2,NULL,_binary '\0',_binary '\0',_binary '\0','kelvin@gmail.com',_binary '',NULL,'Kelvin YMCA','{bcrypt}$2a$10$EOs8VROb14e7ZnydvXECA.4LoIhPOoFHKvVF/iBZ/ker17Eocz4Vi','user',_binary ''),
(3,NULL,_binary '\0',_binary '\0',_binary '\0','wawesh@gmail.com',_binary '',NULL,'Simon Waweru','{bcrypt}$2a$10$EOs8VROb14e7ZnydvXECA.4LoIhPOoFHKvVF/iBZ/ker17Eocz4Vi','wawesh@gmail.com',_binary '\0');

INSERT IGNORE INTO `auth_role_user` VALUES (1,1),(2,2);

INSERT IGNORE INTO `person` VALUES (2,NULL,'demo@smartapps.org','2019-11-06 11:36:46.311000','demo@smartapps.org',
'2019-11-06 11:36:46.338000',1,NULL,'2019-09-27','2019-11-06','M','N',_binary '','MARRIED','Simon','Waweru','Mr');

INSERT IGNORE INTO `patient` VALUES (NULL,NULL,'A+',NULL,_binary '','564','Active',2);

INSERT IGNORE INTO `sequence_data` VALUES ('doctor_request_seq','1',33,_binary '\0',1,9223372036854775807,1),('drug_code_seq','1',1,_binary '\0',1,9223372036854775807,700),('journal_seq','1',30,_binary '\0',1,9223372036854775807,1),('patient_number_seq','1',591,_binary '\0',1,9223372036854775807,560),('patient_prescription_seq','1',27,_binary '\0',1,999999999999,1),('visit_number_seq','1',28,_binary '\0',1,9223372036854775807,1);
